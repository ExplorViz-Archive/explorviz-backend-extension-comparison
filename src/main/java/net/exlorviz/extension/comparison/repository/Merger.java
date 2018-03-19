package net.exlorviz.extension.comparison.repository;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.EntityComparison;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.application.RuntimeInformation;
import net.explorviz.model.helper.ModelHelper;

/**
 * Provides methods to merge two {@link Application}s.
 *
 * @author josw
 *
 */

public class Merger {

	private final EntityComparison entityComparison = new EntityComparison();
	private final PrepareForMerger preparing = new PrepareForMerger();

	/**
	 * * Takes two {@link Application}s and merges them into a new
	 * {@link Application}. Further it adds a {@link Status} flag to show whether an
	 * element between the two {@link Application}s was added, edited or original.
	 * The timestamp attribute of the element shows to which of the applications the
	 * elements belong to.
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

		mergedApp = appVersion2;

		// merge packages and clazzes
		final List<Component> componentsVersion1 = appVersion1.getComponents();
		final List<Component> componentsVersion2 = appVersion2.getComponents();

		final List<Component> componentsMergedVersion = componentMerge(componentsVersion1, componentsVersion2);

		mergedApp.setComponents(componentsMergedVersion);

		// merge communication between clazzes
		final List<AggregatedClazzCommunication> aggregatedCommunications1 = appVersion1
				.getAggregatedOutgoingClazzCommunications();
		final List<AggregatedClazzCommunication> aggregatedCommunications2 = appVersion2
				.getAggregatedOutgoingClazzCommunications();

		final List<ClazzCommunication> clazzCommunications1 = collectAllClazzCommunications(aggregatedCommunications1);
		final List<ClazzCommunication> clazzCommunications2 = collectAllClazzCommunications(aggregatedCommunications2);

		final List<ClazzCommunication> communicationsMergedVersion = communicationClazzMerge(clazzCommunications1,
				clazzCommunications2);
		mergedApp.setCommunications(communicationsMergedVersion);

		for (final ClazzCommunication mergedCommunication : communicationsMergedVersion) {
			final List<RuntimeInformation> commuRuntimeInfo = mergedCommunication.getRuntimeInformations();
			ModelHelper.addClazzCommunication(mergedCommunication.getSourceClazz(),
					mergedCommunication.getTargetClazz(), mergedApp, mergedCommunication.getRequests(),average, overallTraceDuration, traceId,
					mergedCommunication., mergedCommunication.getOperationName());
		}

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
		Component componentFrom1 = new Component();

		for (final Component component2 : componentsMergedVersion) {

			final String fullName2 = component2.getFullQualifiedName();
			final boolean component2Containedin1 = components1.stream()
					.filter(e -> e.getFullQualifiedName().equals(fullName2)).findFirst().isPresent();

			if (component2Containedin1) {
				// get the component in components1 with the same fullQualifiedName as
				// component2
				componentFrom1 = components1.stream().filter(c1 -> c1.getFullQualifiedName().equals(fullName2))
						.findFirst().get();

				final boolean componentsIdentical = entityComparison.componentsIdentical(componentFrom1, component2);

				// case: the identical component exists in version 1 and version 2 -> do nothing
				if (!componentsIdentical) {
					// case: the component exists in both versions, but children and/or clazzes are
					// not identical
					component2.getExtensionAttributes().put("status", Status.EDITED);
				}
				// check the childcomponents of ORIGINAL and EDITED components
				if ((componentFrom1.getChildren().size() > 0) && (component2.getChildren().size() > 0)) {
					componentMerge(componentFrom1.getChildren(), component2.getChildren());
				}
				// check clazzes
				mergedClazzes = clazzMerge(componentFrom1.getClazzes(), component2.getClazzes());
				component2.setClazzes(mergedClazzes);

			} else if (!component2Containedin1) {
				// case: the component does not exist in version 1, but exists in version 2
				component2.getExtensionAttributes().put("status", Status.ADDED);
				setStatusCLazzesAndChildren(component2, Status.ADDED);
			}
		}

		for (final Component component1 : components1) {
			final String fullName1 = component1.getFullQualifiedName();
			final boolean component1Containedin2 = componentsMergedVersion.stream()
					.filter(e -> e.getFullQualifiedName().equals(fullName1)).findFirst().isPresent();

			if (!component1Containedin2) {
				component1.getExtensionAttributes().put("status", Status.DELETED);
				componentsMergedVersion.add(component1);
				setStatusCLazzesAndChildren(component1, Status.DELETED);
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

		for (final Clazz clazz2 : clazzesMergedVersion) {
			clazz2Containedin1 = clazzes1.stream()
					.filter(e -> e.getFullQualifiedName().equals(clazz2.getFullQualifiedName())).findFirst()
					.isPresent();
			// case: the identical component exists in version 1 and version 2 -> do nothing
			if (!clazz2Containedin1) {
				// case: the clazz does not exist in version 1, but exists in version 2
				clazz2.getExtensionAttributes().put("status", Status.ADDED);
			}
		}

		for (final Clazz clazz1 : clazzes1) {
			clazz1Containedin2 = clazzesMergedVersion.stream()
					.filter(e -> e.getFullQualifiedName().equals(clazz1.getFullQualifiedName())).findFirst()
					.isPresent();

			if (!clazz1Containedin2) {
				clazz1.getExtensionAttributes().put("status", Status.DELETED);
				clazzesMergedVersion.add(clazz1);
			}
		}

		return clazzesMergedVersion;
	}

	/**
	 * Takes two lists of {@link ClazzCommunication}s and returns one merged list of
	 * {@link ClazzCommunication}s. This method is used by
	 * {@link Merger#appMerge(Application, Application)}. Two
	 * {@link ClazzCommunication}s are identical, if they have the same source and
	 * target {@link Application} and the same methodName.
	 *
	 * @param clazzes1
	 *            list of {@link ClazzCommunication}s
	 * @param clazzes2
	 *            list of {@link ClazzCommunication}s
	 * @return merged list of {@link ClazzCommunication}s
	 */
	private List<ClazzCommunication> communicationClazzMerge(final List<ClazzCommunication> communications1,
			final List<ClazzCommunication> communications2) {
		final List<ClazzCommunication> mergedCommunications = communications2;
		ClazzCommunication communication2ContainedIn1;
		ClazzCommunication communication1ContainedIn2;

		for (final ClazzCommunication communication2 : mergedCommunications) {
			communication2ContainedIn1 = communications1.stream()
					.filter(c1 -> c1.getSourceClazz().getFullQualifiedName()
							.equals(communication2.getSourceClazz().getFullQualifiedName())
							&& c1.getTargetClazz().getFullQualifiedName()
									.equals(communication2.getTargetClazz().getFullQualifiedName()))
					.findFirst().orElse(null);

			if (communication2ContainedIn1 == null) {
				// case: communication with same source and target does not exist, the
				// methodName is not important yet
				communication2.getExtensionAttributes().put("status", Status.ADDED);
			} else if (!(communication2.getOperationName().equals(communication2ContainedIn1.getOperationName()))) {
				// case: communication with same source and target exists, but the methodNames
				// differ
				communication2.getExtensionAttributes().put("status", Status.EDITED);
				communications1.remove(communication2ContainedIn1);

			}
		}

		for (final ClazzCommunication communication1 : communications1) {
			communication1ContainedIn2 = mergedCommunications.stream()
					.filter(c2 -> c2.getSourceClazz().getFullQualifiedName()
							.equals(communication1.getSourceClazz().getFullQualifiedName())
							&& c2.getTargetClazz().getFullQualifiedName()
									.equals(communication1.getTargetClazz().getFullQualifiedName()))
					.findFirst().orElse(null);

			if (communication1ContainedIn2 == null) {
				communication1.getExtensionAttributes().put("status", Status.DELETED);
				mergedCommunications.add(communication1);
			}

		}

		return mergedCommunications;
	}

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
				clazz.getExtensionAttributes().put("status", status);
			}
		}
		for (final Component child : component.getChildren()) {
			child.getExtensionAttributes().put("status", status);
			setStatusCLazzesAndChildren(child, status);
			for (final Clazz clazz : child.getClazzes()) {
				clazz.getExtensionAttributes().put("status", status);
			}
		}
	}
}
