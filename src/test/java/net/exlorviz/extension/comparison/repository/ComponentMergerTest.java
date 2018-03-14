package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.Component;

/**
 * This class holds tests for all states a {@link Component} can adopt, refer
 * {@link Status.ORIGINAL}, {@link Status.ADDED}, {@link Status.EDITED} and
 * {@link Status.DELETED}. The merged application is taken from
 * {@link MergerTest}.
 *
 * @author josw
 *
 */
public class ComponentMergerTest extends MergerTest {

	@Test
	public void testAppMergeOriginalComponent() {
		// component: org2V1
		final Component originalComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("org2V1")).findFirst().get();
		assertEquals(originalComponent.getFullQualifiedName() + "is not ORIGINAL.", Status.ORIGINAL,
				originalComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedComponent() {
		// component: netV2
		final Component addedComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("netV2")).findFirst().get();
		assertEquals(addedComponent.getFullQualifiedName() + "is not ADDED.", Status.ADDED,
				addedComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedSubSubComponent() {
		// component: netV2.subOrg2.subsubOrg2
		final Component addedSubComponent = mergedApplication.getComponents().get(2).getChildren().get(0).getChildren()
				.stream().filter(c1 -> c1.getFullQualifiedName().equals("netV2.subOrg2.subsubOrg2")).findFirst().get();
		assertEquals(addedSubComponent.getFullQualifiedName() + "is not ADDED.", Status.ADDED,
				addedSubComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeEditedComponent() {
		// component: orgV1
		final Component editedComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("orgV1")).findFirst().get();
		assertEquals(editedComponent.getFullQualifiedName() + "is not EDITED.", Status.EDITED,
				editedComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeDeletedComponent() {
		// org3V1
		final Component deletedComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("org3V1")).findFirst().get();
		assertEquals(deletedComponent.getFullQualifiedName() + "is not DELETED.", Status.DELETED,
				deletedComponent.getExtensionAttributes().get("status"));

	}

}
