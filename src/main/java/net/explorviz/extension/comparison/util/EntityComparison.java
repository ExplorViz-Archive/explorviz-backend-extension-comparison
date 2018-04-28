package net.explorviz.extension.comparison.util;

import java.util.List;

import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.Component;

/**
 * Class for methods that compare elements from the data model, e.g.
 * {@link Component}, {@link Communication}.
 *
 * @author josw
 *
 */
public class EntityComparison {

	/**
	 * Two {@link Component}s are identical, if they have the same
	 * fullQualifiedName, the same children and {@link Clazz}es. The children and
	 * {@link Clazz}es may be empty.
	 *
	 * @param component1
	 * @param component2
	 * @return true: if {@link Component}s are identical, false: else
	 */
	public boolean componentsIdentical(final Component component1, final Component component2) {
		boolean componentsIdentical = false;

		final List<Clazz> clazzes1 = component1.getClazzes();
		final List<Clazz> clazzes2 = component2.getClazzes();
		final List<Component> children1 = component1.getChildren();
		final List<Component> children2 = component2.getChildren();

		// fullQualifiedNames are equal -> was already checked before
		if (clazzes1.size() == clazzes2.size()) {
			if (clazzesEqual(clazzes1, clazzes2)) {
				if ((children1.size() == children2.size())) {
					componentsIdentical = childrenEqual(children1, children2);
				}
			}
		}
		return componentsIdentical;
	}

	/**
	 * Help function that checks whether the children of two {@link Component}s are
	 * identical. If only one pair of children is not identical, then the children
	 * of the {@link Component}s are marked as not identical.
	 *
	 * @param children1
	 * @param children2
	 * @return true: if all children of two {@link Component}s are identical, false:
	 *         else
	 */
	public boolean childrenEqual(final List<Component> children1, final List<Component> children2) {
		boolean childrenEqual = true;

		for (int i = 0; i < children1.size(); i++) {
			if (children1.get(i).getFullQualifiedName().equals(children2.get(i).getFullQualifiedName())) {
				childrenEqual = this.componentsIdentical(children1.get(i), children2.get(i));

				if (!childrenEqual) {
					break;
				}
			} else {
				childrenEqual = false;
				break;
			}
		}

		return childrenEqual;
	}

	/**
	 * Two List<{@link Clazz}> are equal, if they contain the same elements in the
	 * same order.
	 *
	 * @param clazzes1
	 *            List<{@link Clazz}>
	 * @param clazzes2
	 *            List<{@link Clazz}>
	 * @return true: if List<{@link Clazz}>1 and List<{@link Clazz}>2 are equal,
	 *         false:else
	 */
	public boolean clazzesEqual(final List<Clazz> clazzes1, final List<Clazz> clazzes2) {
		boolean clazzesEqual = true;

		for (int i = 0; i < clazzes1.size(); i++) {
			clazzesEqual = clazzes1.get(i).getFullQualifiedName().equals(clazzes2.get(i).getFullQualifiedName());
			if (!clazzesEqual) {
				break;
			}
		}

		return clazzesEqual;
	}

}
