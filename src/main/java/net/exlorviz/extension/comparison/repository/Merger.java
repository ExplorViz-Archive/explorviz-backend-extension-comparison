package net.exlorviz.extension.comparison.repository;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.EntityComparison;
import net.explorviz.model.Application;
import net.explorviz.model.Component;
import net.explorviz.model.helper.BaseEntity;

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
	 * element between the two {@link Application}s was added, edited or deleted.
	 * The {@link BaseEntity#timestamp} attribute of the element shows to which of
	 * the applications the elements belong to.
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

		mergedApp = appVersion1;

		// Merges elements from appVersion2 into appVersion1
		// merge packages
		final List<Component> componentsVersion1 = appVersion1.getComponents();
		final List<Component> componentsVersion2 = appVersion2.getComponents();
		final List<Component> componentsMergedVersion = componentMerge(componentsVersion1, componentsVersion2);

		// final Component newComponent = new Component();
		// newComponent.setName("withStatus");
		// newComponent.setFullQualifiedName("withStatus");
		// newComponent.setParentComponent(null);
		// newComponent.setBelongingApplication(mergedApp);
		//
		// componentsMergedVersion.add(newComponent);

		mergedApp.setComponents(componentsMergedVersion);

		// merge classes (instances of classes)

		// merge communication

		return mergedApp;
	}

	/**
	 * Takes two list of components and returns one merged list of components. Two
	 * components are equal, if they have the same fullQualifiedName.
	 *
	 * @param components1
	 * @param components2
	 * @return
	 */
	private List<Component> componentMerge(final List<Component> components1, final List<Component> components2) {
		final List<Component> componentsMergedVersion = components1;

		for (final Component component2 : components2) {
			final String fullName2 = component2.getFullQualifiedName();
			final boolean componentContained = entityComparison.containsFullQualifiedName(components1, fullName2);

			if (componentContained) {
				final Component componentFrom1 = components1.stream()
						.filter(c1 -> c1.getFullQualifiedName().equals(fullName2)).collect(Collectors.toList()).get(0);
				if (entityComparison.componentsEqual(componentFrom1, component2)) {
					// case: the same component exists in version 1 and version 2
					// do not change the status ORIGINAL of the component, but merge subcomponents
					// and classes,
					// if they exist
					if (componentFrom1.getChildren().size() > 0 && component2.getChildren().size() > 0) {
						componentMerge(componentFrom1.getChildren(), component2.getChildren());
					}
				}
				// case: the component exists in both versions, but was edited in version 2
				// TODO What does "edited" mean for component:
				// name, fullQualifiedName changed; what about children and clazzes?
				// take component of version2 and mark as EDITED
				Collections.replaceAll(componentsMergedVersion, componentFrom1, component2);

			} else if (!componentContained) {
				// case: the component does not exist in version 1, but exists in version 2
				// take component of version2 and mark as ADDED, mark all subcomponents and
				// classes as ADDED too
				componentsMergedVersion.add(component2);
			}
		}
		return componentsMergedVersion;
	}
}
