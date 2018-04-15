package net.explorviz.extension.comparison.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.EntityComparison;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.CumulatedClazzCommunication;

/**
 * Provides methods to merge two {@link Application}s.
 *
 * @author josw
 *
 */

public class Merger {

	static final Logger logger = LoggerFactory.getLogger(Merger.class.getName());
	private final EntityComparison entityComparison = new EntityComparison();
	private final PrepareForMerger preparing = new PrepareForMerger();

	/**
	 * * Takes two {@link Application}s and merges them into a new
	 * {@link Application}. Further it adds a {@link Status} flag to show whether an
	 * element between the two {@link Application}s was added, edited, original or
	 * deleted.
	 *
	 * @param appVersion1
	 * @param appVersion2
	 * @return
	 */
	public Application appMerge(final Application appVersion1, final Application appVersion2) {

		Application mergedApp = new Application();

		// prepares each version for merging
		preparing.addStatusToApp(appVersion1);
		preparing.addStatusToApp(appVersion2);

		// we work on the second version of the application
		mergedApp = appVersion2;

		// merge packages and clazzes
		final List<Component> componentsVersion1 = appVersion1.getComponents();
		final List<Component> componentsVersion2 = appVersion2.getComponents();

		final List<Component> componentsMergedVersion = componentMerge(componentsVersion1, componentsVersion2);

		mergedApp.setComponents(componentsMergedVersion);

		// merge communication between clazzes
		final List<AggregatedClazzCommunication> aggregatedCommunications1 = appVersion1
				.getAggregatedOutgoingClazzCommunications();
		final List<CumulatedClazzCommunication> cumulatedCommunications2 = appVersion2
				.getCumulatedClazzCommunications();

		final List<CumulatedClazzCommunication> cumulatedCommunicationsMergedVersion = cumulatedClazzCommunicationMerge(
				aggregatedCommunications1, cumulatedCommunications2);
		mergedApp.setCumulatedClazzCommunications(cumulatedCommunicationsMergedVersion);

		return mergedApp;
	}

	/**
	 * Takes two lists of {@link Component}s and returns one merged list of
	 * {@link Component}s. This method is used by
	 * {@link Merger#appMerge(Application, Application)}. Two {@link Component}s are
	 * identical, if they have the same fullQualifiedName, the same {@link Clazz}es
	 * and the same child{@link Component}s.
	 *
	 * @param components1
	 * @param components2
	 * @return merged list of {@link Component}s
	 */
	private List<Component> componentMerge(final List<Component> components1, final List<Component> components2) {
		final List<Component> componentsMergedVersion = components2;
		List<Clazz> mergedClazzes = new ArrayList<Clazz>();
		List<Component> componentsFrom1 = null;

		for (final Component component2 : componentsMergedVersion) {

			final String fullName2 = component2.getFullQualifiedName();
			final boolean component2Containedin1 = components1.stream()
					.filter(e -> e.getFullQualifiedName().equals(fullName2)).findFirst().isPresent();

			if (component2Containedin1) {
				// get the component in components1 with the same fullQualifiedName as
				// component2

				componentsFrom1 = components1.stream().filter(c1 -> c1.getFullQualifiedName().equals(fullName2))
						.collect(Collectors.toList());

				if (componentsFrom1.size() != 1) {
					logger.error("Merger.componentMerge(): wrong amount of components with: {}", fullName2);
				} else {
					final Component componentFrom1 = componentsFrom1.get(0);
					final boolean componentsIdentical = entityComparison.componentsIdentical(componentFrom1,
							component2);

					// case: the identical component exists in version 1 and version 2 -> do nothing
					if (!componentsIdentical) {
						// case: the component exists in both versions, but children and/or clazzes are
						// not identical
						component2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.EDITED);
					}
					// check the childcomponents of ORIGINAL and EDITED components
					if ((componentFrom1.getChildren().size() > 0) || (component2.getChildren().size() > 0)) {
						componentMerge(componentFrom1.getChildren(), component2.getChildren());
					}
					// check clazzes
					mergedClazzes = clazzMerge(componentFrom1.getClazzes(), component2.getClazzes());
					component2.setClazzes(mergedClazzes);

				}
			} else {
				// case: the component does not exist in version 1, but exists in version 2
				component2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ADDED);
				setStatusCLazzesAndChildren(component2, Status.ADDED);
			}
		}

		for (final Component component1 : components1) {

			final boolean component1Containedin2 = componentsMergedVersion.stream()
					.filter(e -> e.getFullQualifiedName().equals(component1.getFullQualifiedName())).findFirst()
					.isPresent();

			if (!component1Containedin2) {
				// case: the component and children do not exist in version 2, but existed in
				// version 1
				component1.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.DELETED);
				setStatusCLazzesAndChildren(component1, Status.DELETED);
				// add deleted component to result component list
				componentsMergedVersion.add(component1);
			}
		}
		return componentsMergedVersion;
	}

	/**
	 * Takes two lists of {@link Clazz}es and returns one merged list of
	 * {@link Clazz}es. This method is used by
	 * {@link Merger#componentMerge(List, List)}. Two {@link Clazz}es are identical,
	 * if they have the same fullQualifiedName.
	 *
	 * @param clazzes1
	 *            list of {@link Clazz}es
	 * @param clazzes2
	 *            list of {@link Clazz}es
	 * @return merged list of {@link Clazz}es
	 */
	private List<Clazz> clazzMerge(final List<Clazz> clazzes1, final List<Clazz> clazzes2) {
		final List<Clazz> clazzesMergedVersion = clazzes2;
		boolean clazz2Containedin1;
		boolean clazz1Containedin2;
		Clazz clazzIn1 = null;

		for (final Clazz clazz2 : clazzesMergedVersion) {
			try {
				clazzIn1 = clazzes1.stream()
						.filter(c1 -> c1.getFullQualifiedName().equals(clazz2.getFullQualifiedName())).findFirst()
						.get();
				clazz2Containedin1 = true;
			} catch (final NoSuchElementException e) {
				clazz2Containedin1 = false;
			}

			if (!clazz2Containedin1) {
				// case: the clazz does not exist in version 1, but exists in version 2
				clazz2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ADDED);
				clazz2.getExtensionAttributes().put(PrepareForMerger.DIFF_INSTANCE_COUNT, clazz2.getInstanceCount());
			} else {

				// case: the identical clazz exists in version 1 and version 2 -> do not set
				// status, but check instance count
				clazz2.getExtensionAttributes().put(PrepareForMerger.DIFF_INSTANCE_COUNT,
						clazz2.getInstanceCount() - clazzIn1.getInstanceCount());
			}
		}

		for (final Clazz clazz1 : clazzes1) {
			clazz1Containedin2 = clazzesMergedVersion.stream()
					.filter(e -> e.getFullQualifiedName().equals(clazz1.getFullQualifiedName())).findFirst()
					.isPresent();

			if (!clazz1Containedin2) {
				// case: the clazz does exist in version 1, but not exists in version 2
				clazz1.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.DELETED);
				clazz1.getExtensionAttributes().put(PrepareForMerger.DIFF_INSTANCE_COUNT,
						clazz1.getInstanceCount() * -1);
				clazzesMergedVersion.add(clazz1);
			}
		}

		return clazzesMergedVersion;
	}

	/**
	 * Takes one list of {@link AggregatedClazzCommunication}s and one list of
	 * {@link CumulatedClazzCommunication}. It returns one merged list of
	 * {@link CumulatedClazzCommunication}s. This method is used by
	 * {@link Merger#appMerge(Application, Application)}. Two
	 * {@link ClazzCommunication}s are identical, if they have the same source and
	 * target {@link Application} and the same methodName.
	 *
	 * @param clazzes1
	 *            list of {@link AggregatedClazzCommunication}s
	 * @param clazzes2
	 *            list of {@link CumulatedClazzCommunication}s
	 * @return merged list of {@link CumulatedClazzCommunication}s
	 */
	private List<CumulatedClazzCommunication> cumulatedClazzCommunicationMerge(
			final List<AggregatedClazzCommunication> aggregatedCommunications1,
			final List<CumulatedClazzCommunication> cumulatedCommunications2) {

		final List<CumulatedClazzCommunication> mergedCumulatedCommunications = cumulatedCommunications2;
		final List<ClazzCommunication> clazzCommunications1 = collectAllClazzCommunications(aggregatedCommunications1);

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
						.filter(c -> c.getExtensionAttributes().get(PrepareForMerger.STATUS).equals(Status.ADDED))
						.findAny().isPresent();

				final boolean deletedExist = aggregatedCommunication2.getOutgoingClazzCommunications().stream()
						.filter(c -> c.getExtensionAttributes().get(PrepareForMerger.STATUS).equals(Status.DELETED))
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
					.filter(c -> c.getExtensionAttributes().get(PrepareForMerger.STATUS).equals(Status.ADDED)).findAny()
					.isPresent();

			final boolean deletedExist = cumulatedCommunication2.getAggregatedClazzCommunications().stream()
					.filter(c -> c.getExtensionAttributes().get(PrepareForMerger.STATUS).equals(Status.DELETED))
					.findAny().isPresent();

			// set status of cumulated communication
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

	private List<ClazzCommunication> clazzCommunicationMerge(final List<ClazzCommunication> communications1,
			final List<ClazzCommunication> communications2) {

		final List<ClazzCommunication> mergedCommunications = communications2;
		boolean communication2ContainedIn1;
		for (final ClazzCommunication mergedCommunication : mergedCommunications) {
			communication2ContainedIn1 = communications1.stream()
					.filter(c1 -> c1.getSourceClazz().getFullQualifiedName()
							.equals(mergedCommunication.getSourceClazz().getFullQualifiedName())
							&& c1.getTargetClazz().getFullQualifiedName()
									.equals(mergedCommunication.getTargetClazz().getFullQualifiedName())
							&& c1.getOperationName().equals(mergedCommunication.getOperationName()))
					.findFirst().isPresent();
			if (!communication2ContainedIn1) {
				mergedCommunication.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ADDED);
			}
			// else: communication is contained in version 1 and in version 2, thus the
			// default status is not changed
		}

		boolean communication1ContainedIn2;
		for (final ClazzCommunication communication1 : communications1) {
			communication1ContainedIn2 = communications2.stream()
					.filter(c2 -> c2.getSourceClazz().getFullQualifiedName()
							.equals(communication1.getSourceClazz().getFullQualifiedName())
							&& c2.getTargetClazz().getFullQualifiedName()
									.equals(communication1.getTargetClazz().getFullQualifiedName())
							&& c2.getOperationName().equals(communication1.getOperationName()))
					.findFirst().isPresent();
			if (!communication1ContainedIn2) {
				communication1.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.DELETED);
				mergedCommunications.add(communication1);
			}
			// else: communication is contained in version 1 and in version 2, thus the
			// default status is not changed
		}

		return mergedCommunications;
	}

	/**
	 * Include deleted Communications
	 */
	// private List<AggregatedClazzCommunication> includeDeletedIntoMerged(
	// final List<AggregatedClazzCommunication> communications1,
	// final List<AggregatedClazzCommunication> communications2,
	// final List<AggregatedClazzCommunication> mergedCommunications) {
	//
	// final List<ClazzCommunication> clazzCommunications2 =
	// collectAllClazzCommunications(communications2);
	// ClazzCommunication communication1ContainedIn2;
	//
	// for (final AggregatedClazzCommunication aggregatedCommunication1 :
	// communications1) {
	// for (final ClazzCommunication communication1 :
	// aggregatedCommunication1.getOutgoingClazzCommunications()) {
	// communication1ContainedIn2 = clazzCommunications2.stream()
	// .filter(c2 -> c2.getSourceClazz().getFullQualifiedName()
	// .equals(communication1.getSourceClazz().getFullQualifiedName())
	// && c2.getTargetClazz().getFullQualifiedName()
	// .equals(communication1.getTargetClazz().getFullQualifiedName()))
	// .findFirst().orElse(null);
	//
	// if (communication1ContainedIn2 == null) {
	// communication1.getExtensionAttributes().put(PrepareForMerger.STATUS,
	// Status.DELETED);
	// aggregatedCommunication1.addClazzCommunication(communication1);
	// mergedCommunications.add(aggregatedCommunication1);
	// }
	//
	// }
	// }
	//
	// return mergedCommunications;
	// }

	/**
	 * Collect all clazzCommunications of an application in one list of
	 * ClazzCommunications
	 */

	public List<ClazzCommunication> collectAllClazzCommunications(final List<AggregatedClazzCommunication> inputList) {
		final List<ClazzCommunication> outputList = new ArrayList<>();

		for (final AggregatedClazzCommunication aggregatedCommu : inputList) {
			for (final ClazzCommunication clazzCommu : aggregatedCommu.getOutgoingClazzCommunications()) {
				outputList.add(clazzCommu);
			}
		}
		return outputList;
	}

	/**
	 * Set the status of all {@link Clazz}es and child{@link Component}s.
	 *
	 * @param component
	 * @param status
	 */
	private void setStatusCLazzesAndChildren(final Component component, final Status status) {
		if (!component.getClazzes().isEmpty()) {
			for (final Clazz clazz : component.getClazzes()) {
				clazz.getExtensionAttributes().put(PrepareForMerger.STATUS, status);
			}
		}
		for (final Component child : component.getChildren()) {
			child.getExtensionAttributes().put(PrepareForMerger.STATUS, status);
			setStatusCLazzesAndChildren(child, status);
			for (final Clazz clazz : child.getClazzes()) {
				clazz.getExtensionAttributes().put(PrepareForMerger.STATUS, status);
			}
		}
	}
}
