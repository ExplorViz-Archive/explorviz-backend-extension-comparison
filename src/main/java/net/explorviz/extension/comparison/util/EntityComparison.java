package net.explorviz.extension.comparison.util;

import java.util.List;

import net.explorviz.model.Communication;
import net.explorviz.model.Component;

/**
 * Class for methods that compare elements from the meta-model, e.g.
 * {@link Component}, {@link Communication}.
 * 
 * @author josw
 *
 */
public class EntityComparison {

	public boolean componentsEqual(final Component component1, final Component component2) {

		boolean componentsEqual = false;

		final String fullName1 = component1.getFullQualifiedName();
		final String fullName2 = component2.getFullQualifiedName();
		final long timestamp1 = component1.getTimestamp();
		final long timestamp2 = component2.getTimestamp();

		if ((fullName1.equals(fullName2)) && (timestamp1 != timestamp2)) {
			componentsEqual = true;
		}
		return componentsEqual;
	}

	// TODO parameter more general, problem with casting in AppMerger.java:
	// List<Draw3DNodeEntity>
	public boolean containsFullQualifiedName(final List<Component> list, final String fullName) {
		return list.stream().filter(e -> e.getFullQualifiedName().equals(fullName)).findFirst().isPresent();
	}

}
