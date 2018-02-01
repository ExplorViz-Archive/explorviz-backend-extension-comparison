package net.exlorviz.extension.comparison.repository;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.EntityComparison;
import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.Component;

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

		mergedApp = appVersion1;

		// merge packages and clazzes
		final List<Component> componentsVersion1 = appVersion1.getComponents();
		final List<Component> componentsVersion2 = appVersion2.getComponents();

		final List<Component> componentsMergedVersion = componentMerge(componentsVersion1, componentsVersion2);

		mergedApp.setComponents(componentsMergedVersion);

		// TODO merge communication

		return mergedApp;
	}

	/**
	 * Takes two lists of {@link Component}s and returns one merged list of
	 * {@link Component}s. Two {@link Component}s are identical, if they have the
	 * same fullQualifiedName, the same {@link Clazz}es and the same
	 * child{@link Component}s.
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
			final boolean componentContained = components1.stream()
					.filter(e -> e.getFullQualifiedName().equals(fullName2)).findFirst().isPresent();

			if (componentContained) {
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
			} else if (!componentContained) {
				// case: the component does not exist in version 1, but exists in version 2
				component2.getExtensionAttributes().put("status", Status.ADDED);
			}
			// check the childcomponents of ORIGINAL and EDITED components
			if ((componentFrom1.getChildren().size() > 0) && (component2.getChildren().size() > 0)) {
				componentMerge(componentFrom1.getChildren(), component2.getChildren());
			}
			// check clazzes
			mergedClazzes = this.clazzMerge(componentFrom1.getClazzes(), component2.getClazzes());
			component2.setClazzes(mergedClazzes);
		}
		return componentsMergedVersion;
	}

	/**
	 * Takes two lists of {@link Clazz}es and returns one merged list of
	 * {@link Clazz}es. Two {@link Clazz}es are identical, if they have the same
	 * fullQualifiedName.
	 *
	 * @param clazzes1
	 *            list of {@link Clazz}es
	 * @param clazzes2
	 *            list of {@link Clazz}es
	 * @return merged list of {@link Clazz}es
	 */
	// TODO define class EDITED
	private List<Clazz> clazzMerge(final List<Clazz> clazzes1, final List<Clazz> clazzes2) {
		final List<Clazz> clazzesMergedVersion = clazzes2;
		boolean clazzContained;

		for (final Clazz clazz2 : clazzesMergedVersion) {
			clazzContained = clazzes1.stream()
					.filter(e -> e.getFullQualifiedName().equals(clazz2.getFullQualifiedName())).findFirst()
					.isPresent();
			// case: the identical component exists in version 1 and version 2 -> do nothing
			if (!clazzContained) {
				// case: the clazz does not exist in version 1, but exists in version 2
				clazz2.getExtensionAttributes().put("status", Status.ADDED);
			}
		}

		return clazzesMergedVersion;
	}
}
