package net.explorviz.extension.comparison.repository;

import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.EntityComparison;
import net.explorviz.extension.comparison.util.MergerHelper;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.CumulatedClazzCommunication;

/**
 * Provides methods to merge two {@link Application}s.
 *
 * @author jweg
 *
 */

public class Merger {

	static final Logger logger = LoggerFactory.getLogger(Merger.class.getName());
	private final EntityComparison entityComparison = new EntityComparison();
	private final MergerHelper mergerHelper = new MergerHelper();

	/**
	 * Takes two {@link Application}s and merges them into a new
	 * {@link Application}. Further it adds a {@link Status} flag to show whether an
	 * element between the two {@link Application}s was added, edited, original or
	 * deleted.
	 *
	 * @param appVersion1
	 * @param appVersion2
	 * @return mergedApp
	 */
	public Application appMerge(final Application appVersion1, final Application appVersion2) {

		// prepares each version for merging
		// set default status for components, clazzes and communications
		// set default diffInstanceCount for clazzes
		PrepareForMerger.addStatusToApp(appVersion1);
		PrepareForMerger.addStatusToApp(appVersion2);

		/** merge components and clazzes */
		// we work on the second version of the application
		final Application mergedApp = appVersion2;

		final List<Component> componentsVersion1 = appVersion1.getComponents();
		List<Component> flatComponentsFrom1 = mergerHelper.createFlatComponents(componentsVersion1);

		final List<Component> mergedComponents = mergedApp.getComponents();
		List<Component> flatMergedComponents = mergerHelper.createFlatComponents(mergedComponents);

		// check, if components and clazzes from version 2 exist in version 1 (possible
		// status: ORIGINAL, ADDED, EDITED)
		checkComponentsAndClazzesAddedAndEdited(mergedComponents, flatComponentsFrom1);
		// check, if components from version 1 exist in version 2 (possible
		// status: ORIGINAL, DELETED)
		checkComponentsDeleted(componentsVersion1, flatMergedComponents);

		// check, if clazzes from version 1 exist in version 2 (possible
		// status: ORIGINAL, DELETED)
		final List<Clazz> flatClazzesFrom1 = mergerHelper
				.createFlatClazzes(mergerHelper.createFlatComponents(appVersion1.getComponents()));
		flatMergedComponents = mergerHelper.createFlatComponents(mergedApp.getComponents());
		final List<Clazz> flatMergedClazzes = mergerHelper.createFlatClazzes(flatMergedComponents);
		checkClazzesDeleted(flatClazzesFrom1, flatMergedClazzes, flatMergedComponents);

		flatComponentsFrom1 = mergerHelper.createFlatComponents(appVersion1.getComponents());
		setDiffInstanceCountForMergedClazzes(mergerHelper.createFlatComponents(mergedApp.getComponents()),
				mergerHelper.createFlatClazzes(flatComponentsFrom1));

		/** merge communication between clazzes */
		final List<AggregatedClazzCommunication> aggregatedCommunications1 = appVersion1
				.getAggregatedOutgoingClazzCommunications();
		final List<CumulatedClazzCommunication> cumulatedCommunications2 = appVersion2
				.getCumulatedClazzCommunications();

		final List<CumulatedClazzCommunication> cumulatedCommunicationsMergedVersion = checkCommunicationAll(
				aggregatedCommunications1, cumulatedCommunications2);
		mergedApp.setCumulatedClazzCommunications(cumulatedCommunicationsMergedVersion);

		return mergedApp;
	}

	/**
	 * Checks, whether {@link Component}s and {@link Clazz}es from version 2 exist
	 * in version 1 (possible status: ORIGINAL, ADDED, EDITED)
	 *
	 * @param mergedComponents
	 *            List<{@link Component}> with {@link Component}s (incl. children
	 *            and clazzes) from version 2
	 * @param flatComponentsFrom1
	 *            List<{@link Component}> with {@link Component}s from version 1
	 */

	private void checkComponentsAndClazzesAddedAndEdited(final List<Component> mergedComponents,
			final List<Component> flatComponentsFrom1) {

		if (!mergedComponents.isEmpty()) {
			Component mergedComponentIn1;
			for (final Component mergedComponent : mergedComponents) {

				mergedComponentIn1 = flatComponentsFrom1.stream()
						.filter(c -> mergedComponent.getFullQualifiedName().equals(c.getFullQualifiedName()))
						.findFirst().orElse(null);

				if (mergedComponentIn1 == null) {
					// case: mergedComponent does not exist in version 1 -> status ADDED
					// status of clazzes in component: ADDED
					mergerHelper.setStatusComponentCLazzesAndChildren(mergedComponent, Status.ADDED);
				} else {
					// case: mergedComponent does exist in version 1 -> status EDITED or ORIGINAL
					// check EDITED
					checkComponentEdited(mergedComponentIn1, mergedComponent);
					// check children
					if (!mergedComponent.getChildren().isEmpty()) {
						checkComponentsAndClazzesAddedAndEdited(mergedComponent.getChildren(), flatComponentsFrom1);
					}
				}
			}
		}
	}

	/**
	 * Check status of {@link Component}. Possible status is ORIGINAL or EDITED.
	 *
	 * @param component1
	 * @param component2
	 */
	private void checkComponentEdited(final Component component1, final Component component2) {
		final boolean componentsIdentical = entityComparison.componentsIdentical(component1, component2);

		if (!componentsIdentical) {
			// case: the component exists in both versions, but children and/or clazzes are
			// not identical
			component2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.EDITED);
			// if not identical clazzes: check if clazzes added and set status of clazz
			checkClazzesAdded(component1, component2);
		}
		// case: the identical component exists in version 1 and version 2 -> status:
		// ORIGINAL (default)
	}

	private void checkClazzesAdded(final Component component1, final Component component2) {

		final List<Clazz> clazzes1 = component1.getClazzes();
		final List<Clazz> clazzes2 = component2.getClazzes();

		boolean clazzIn1;
		for (final Clazz clazz2 : clazzes2) {
			clazzIn1 = clazzes1.stream().filter(c -> clazz2.getFullQualifiedName().equals(c.getFullQualifiedName()))
					.findFirst().isPresent();
			if (!clazzIn1) {
				clazz2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ADDED);
			}
		}
	}

	/**
	 * Checks, whether {@link Component}s from version 1 exist in version 2
	 * (possible status: ORIGINAL, DELETED)
	 *
	 * @param componentsVersion1
	 *            List<{@link Component>s from version 1
	 * @param flatMergedComponents
	 *            List<{@link Component}>s from version 2
	 */
	private void checkComponentsDeleted(final List<Component> componentsVersion1,
			final List<Component> flatMergedComponents) {
		if (!componentsVersion1.isEmpty()) {
			Component component1ContainedInMerged;
			for (final Component component1 : componentsVersion1) {

				component1ContainedInMerged = flatMergedComponents.stream()
						.filter(c -> component1.getFullQualifiedName().equals(c.getFullQualifiedName())).findFirst()
						.orElse(null);

				if (component1ContainedInMerged == null) {
					// component1 does not exist in mergedComponents
					// create component with clazzes with DELETED and set in mergedApp
					createDeletedComponent(component1, flatMergedComponents);
				} else {
					// component1 exists in mergedComponents
					// check children
					if (!component1.getChildren().isEmpty()) {
						checkComponentsDeleted(component1.getChildren(), flatMergedComponents);
					}
				}

			}
		}

	}

	/**
	 * Inserts the DELETED {@link Component} from version 1 into the merged version.
	 *
	 * @param component1
	 * @param flatMergedComponents
	 *            List<{@link Component}>s from version 2
	 */
	private void createDeletedComponent(final Component component1, final List<Component> flatMergedComponents) {
		final Component parent = component1.getParentComponent();
		final boolean isRootComponent;

		if (parent == null) {
			isRootComponent = true;
		} else {
			isRootComponent = false;
		}
		;

		String parentFullName;
		Component parentInMerged = null;

		final Component newMergedComponent = component1;
		// newMergedComponent.getExtensionAttributes().put(PrepareForMerger.STATUS,
		// Status.DELETED);
		mergerHelper.setStatusComponentCLazzesAndChildren(newMergedComponent, Status.DELETED);

		if (isRootComponent) {
			// case: component has no parent -> add newMergedComponent to components of
			// merged application
			flatMergedComponents.get(0).getBelongingApplication().getComponents().add(newMergedComponent);
		} else {
			// case: component has a parent
			parentFullName = parent.getFullQualifiedName();

			parentInMerged = flatMergedComponents.stream().filter(c -> parentFullName.equals(c.getFullQualifiedName()))
					.findFirst().orElse(null);

			if (parentInMerged != null) {
				// link the newMergedComponent to the parent in the merged version
				parentInMerged.getChildren().add(newMergedComponent);
				newMergedComponent.setParentComponent(parentInMerged);
			} else {
				logger.error("parent of deleted component {} not found in merged component list.",
						component1.getFullQualifiedName());
			}
		}

	}

	/**
	 * Checks, whether {@link Clazz}es from version 1 exist in version 2 (possible
	 * status: ORIGINAL, DELETED). If clazz is deleted, insert deleted clazz in the
	 * merged version
	 *
	 * @param flatClazzesFrom1
	 * @param flatMergedClazzes
	 *            List<{@link Clazz}> clazzes of version 2
	 * @param flatMergedComponents
	 *            List<{@link Component}> components of version 2
	 */
	private void checkClazzesDeleted(final List<Clazz> flatClazzesFrom1, final List<Clazz> flatMergedClazzes,
			final List<Component> flatMergedComponents) {

		Clazz clazzInMerged;
		Component parentInMerged = null;
		for (final Clazz clazzFrom1 : flatClazzesFrom1) {
			clazzInMerged = flatMergedClazzes.stream()
					.filter(c -> clazzFrom1.getFullQualifiedName().equals(c.getFullQualifiedName())).findFirst()
					.orElse(null);
			if (clazzInMerged == null) {
				// clazz not exist in merged version -> status:DELETED
				clazzFrom1.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.DELETED);
				final String parentFullName = clazzFrom1.getParent().getFullQualifiedName();
				// search parent in merged components with fullname
				parentInMerged = flatMergedComponents.stream()
						.filter(c -> parentFullName.equals(c.getFullQualifiedName())).findFirst().orElse(null);
				if (parentInMerged != null) {
					// link the clazzFrom1 to the parent in the merged version
					parentInMerged.getClazzes().add(clazzFrom1);
					clazzFrom1.setParent(parentInMerged);
				} else {
					logger.error("parent of deleted clazz {} not found in merged component list.",
							clazzFrom1.getFullQualifiedName());
				}
			}
		}
	}

	/**
	 * Computes the difference of the instance count of clazzes between version 1
	 * and version 2.
	 *
	 * @param flatMergedComponents
	 *            List<{@link Component} components from version 2
	 * @param flatClazzesFrom1
	 */
	private void setDiffInstanceCountForMergedClazzes(final List<Component> flatMergedComponents,
			final List<Clazz> flatClazzesFrom1) {
		Clazz clazzIn1;

		for (final Component component : flatMergedComponents) {
			for (final Clazz clazz : component.getClazzes()) {

				if (Status.DELETED.equals(clazz.getExtensionAttributes().get(PrepareForMerger.STATUS))) {
					clazz.getExtensionAttributes().put(PrepareForMerger.DIFF_INSTANCE_COUNT,
							clazz.getInstanceCount() * -1);
				} else if (Status.ORIGINAL.equals(clazz.getExtensionAttributes().get(PrepareForMerger.STATUS))) {
					clazzIn1 = flatClazzesFrom1.stream()
							.filter(c -> clazz.getFullQualifiedName().equals(c.getFullQualifiedName())).findFirst()
							.orElse(null);
					if (clazzIn1 == null) {
						logger.error("instanceCount: clazz {} with status ORIGINAL not found in clazzFrom1 list.",
								clazz.getFullQualifiedName());
					} else {
						clazz.getExtensionAttributes().put(PrepareForMerger.DIFF_INSTANCE_COUNT,
								clazz.getInstanceCount() - clazzIn1.getInstanceCount());
					}
				} else if (Status.ADDED.equals(clazz.getExtensionAttributes().get(PrepareForMerger.STATUS))) {
					clazz.getExtensionAttributes().put(PrepareForMerger.DIFF_INSTANCE_COUNT, clazz.getInstanceCount());
				}
			}
		}

	}

	/**
	 * Takes one list of {@link AggregatedClazzCommunication}s and one list of
	 * {@link CumulatedClazzCommunication}. It returns one merged list of
	 * {@link CumulatedClazzCommunication}s. This method is used by
	 * {@link Merger#appMerge(Application, Application)}. Two
	 * {@link ClazzCommunication}s are identical, if they have the same source and
	 * target {@link Application} and the same methodName.
	 *
	 * @param aggregatedCommunications1
	 *            list of {@link AggregatedClazzCommunication}s
	 * @param cumulatedCommunications2
	 *            list of {@link CumulatedClazzCommunication}s
	 * @return merged list of {@link CumulatedClazzCommunication}s
	 */
	private List<CumulatedClazzCommunication> checkCommunicationAll(
			final List<AggregatedClazzCommunication> aggregatedCommunications1,
			final List<CumulatedClazzCommunication> cumulatedCommunications2) {

		final List<CumulatedClazzCommunication> mergedCumulatedCommunications = cumulatedCommunications2;
		final List<ClazzCommunication> clazzCommunications1 = MergerHelper
				.createFlatClazzCommunications(aggregatedCommunications1);

		ClazzCommunication communication2ContainedIn1;
		for (final CumulatedClazzCommunication cumulatedCommunication2 : mergedCumulatedCommunications) {
			for (final AggregatedClazzCommunication aggregatedCommunication2 : cumulatedCommunication2
					.getAggregatedClazzCommunications()) {
				for (final ClazzCommunication clazzCommunication2 : aggregatedCommunication2
						.getOutgoingClazzCommunications()) {

					communication2ContainedIn1 = clazzCommunications1.stream()
							.filter(c1 -> c1.getSourceClazz().getFullQualifiedName()
									.equals(clazzCommunication2.getSourceClazz().getFullQualifiedName())
									&& c1.getTargetClazz().getFullQualifiedName()
											.equals(clazzCommunication2.getTargetClazz().getFullQualifiedName())
									&& c1.getOperationName().equals(clazzCommunication2.getOperationName()))
							.findFirst().orElse(null);

					if (communication2ContainedIn1 != null) {
						// communication is contained in version 1 and in version 2, thus the
						// default status is not changed
						// marked that communication exists in both versions, used for detection of
						// deleted communication
						communication2ContainedIn1.getExtensionAttributes().put("exists", true);

					} else {
						clazzCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ADDED);
					}

				}
				// deleted communication exists in version one, but not in version two and is
				// marked with "exists"
				final List<ClazzCommunication> deletedCommunications = clazzCommunications1.stream()
						.filter(c1 -> c1.getSourceClazz().getFullQualifiedName()
								.equals(aggregatedCommunication2.getSourceClazz().getFullQualifiedName()))
						.filter(c1 -> c1.getTargetClazz().getFullQualifiedName()
								.equals(aggregatedCommunication2.getTargetClazz().getFullQualifiedName()))
						.filter(c1 -> !c1.getExtensionAttributes().containsKey("exists")).collect(Collectors.toList());
				for (final ClazzCommunication deletedCommunication : deletedCommunications) {
					deletedCommunication.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.DELETED);
				}
				aggregatedCommunication2.getOutgoingClazzCommunications().addAll(deletedCommunications);

				// booleans for checking status of clazz communications in aggregated
				// communication
				final boolean addedExist = aggregatedCommunication2.getOutgoingClazzCommunications().stream()
						.filter(c -> Status.ADDED.equals(c.getExtensionAttributes().get(PrepareForMerger.STATUS)))
						.findAny().isPresent();

				final boolean deletedExist = aggregatedCommunication2.getOutgoingClazzCommunications().stream()
						.filter(c -> Status.DELETED.equals(c.getExtensionAttributes().get(PrepareForMerger.STATUS)))
						.findAny().isPresent();

				// set status of aggregated communications
				if (addedExist) {
					if (deletedExist) {
						// EDITED: addedExist=true, deletedExist=true
						aggregatedCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.EDITED);
					} else {
						// ADDED: addedExist=true, deletedExist=false
						aggregatedCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ADDED);
					}
				} else {
					if (deletedExist) {
						// DELETED: addedExist=false, deletedExist=true
						aggregatedCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.DELETED);
					}
				}
			}
			// booleans for checking status of aggregated communications of cumulated
			// communication
			final boolean addedExist = cumulatedCommunication2.getAggregatedClazzCommunications().stream()
					.filter(c -> Status.ADDED.equals(c.getExtensionAttributes().get(PrepareForMerger.STATUS))).findAny()
					.isPresent();

			final boolean deletedExist = cumulatedCommunication2.getAggregatedClazzCommunications().stream()
					.filter(c -> Status.DELETED.equals(c.getExtensionAttributes().get(PrepareForMerger.STATUS)))
					.findAny().isPresent();

			final boolean editedExist = cumulatedCommunication2.getAggregatedClazzCommunications().stream()
					.filter(c -> Status.EDITED.equals(c.getExtensionAttributes().get(PrepareForMerger.STATUS)))
					.findAny().isPresent();

			// set status of cumulated communication
			if (editedExist) {
				cumulatedCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.EDITED);
			}
			if (addedExist) {
				if (deletedExist) {
					// EDITED: addedExist=true, deletedExist=true
					cumulatedCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.EDITED);
				} else {
					// ADDED: addedExist=true, deletedExist=false
					cumulatedCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ADDED);
				}
			} else {
				if (deletedExist) {
					// DELETED: addedExist=false, deletedExist=true
					cumulatedCommunication2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.DELETED);
				}
			}
		}

		return mergedCumulatedCommunications;
	}
}
