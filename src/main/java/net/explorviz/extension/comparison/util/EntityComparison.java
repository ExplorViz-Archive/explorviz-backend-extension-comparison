package net.explorviz.extension.comparison.util;

import java.util.List;

import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.Component;

/**
 * Class for methods that compare elements from the meta-model, e.g.
 * {@link Component}, {@link Communication}.
 *
 * @author josw
 *
 */
public class EntityComparison {

	boolean fullNameEqual = false;
	boolean clazzesEqual = false;
	boolean childrenEqual = false;

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

		final String fullName1 = component1.getFullQualifiedName();
		final String fullName2 = component2.getFullQualifiedName();

		final List<Clazz> clazzes1 = component1.getClazzes();
		final List<Clazz> clazzes2 = component2.getClazzes();
		final List<Component> children1 = component1.getChildren();
		final List<Component> children2 = component2.getChildren();

		// TODO && (timestamp1 != timestamp2) not for testing, because timestamp=0, in
		// example landscape
		if (fullName1.equals(fullName2)) {
			// same name
			if ((clazzes1.isEmpty()) && (clazzes2.isEmpty())) {
				if ((children1.isEmpty()) && (children2.isEmpty())) {
					// empty clazzes and empty children
					componentsIdentical = true;
				} else if ((children1.size() == children2.size())) {
					// empty clazzes, but children
					componentsIdentical = childrenEqual(children1, children2);
				}
			} else if (clazzes1.size() == clazzes2.size()) {
				// not empty clazzes
				clazzesEqual = clazzesEqual(clazzes1, clazzes2);
				if (clazzesEqual && (!children1.isEmpty()) && (children1.size() == children2.size())) {
					// same clazzes and not empty children
					componentsIdentical = childrenEqual(children1, children2);
				}
				if (clazzesEqual && children1.isEmpty() && children2.isEmpty()) {
					// same clazzes, but empty children
					componentsIdentical = true;
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
			childrenEqual = this.componentsIdentical(children1.get(i), children2.get(i));

			if (!childrenEqual) {
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

		for (int i = 0; clazzes1.size() > i; i++) {
			clazzesEqual = clazzEqual(clazzes1.get(i), clazzes2.get(i));
			if (!clazzesEqual) {
				break;
			}
		}

		return clazzesEqual;
	}

	/**
	 * Two {@link Clazz} are equal, if they have the same type and the same
	 * fullQualifiedName.
	 *
	 * @param clazz1
	 * @param clazz2
	 * @return true: if elem1 and elem2 are equal, false:else
	 */
	public boolean clazzEqual(final Clazz clazz1, final Clazz clazz2) {

		return clazz1.getFullQualifiedName().equals(clazz2.getFullQualifiedName());
	}

}
