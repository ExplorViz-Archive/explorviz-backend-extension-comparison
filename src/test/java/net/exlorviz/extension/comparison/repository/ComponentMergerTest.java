package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.Component;

public class ComponentMergerTest extends MergerTest {

	@Test
	public void testAppMergeOriginalComponent() {
		// component: org2V1
		final Component originalComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("org2V1")).findFirst().get();
		assertEquals(Status.ORIGINAL, originalComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedComponent() {
		// component: netV2
		final Component addedComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("netV2")).findFirst().get();
		assertEquals(Status.ADDED, addedComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedSubSubComponent() {
		// component: netV2.subOrg2.subsubOrg2
		final Component addedSubComponent = mergedApplication.getComponents().get(2).getChildren().get(0).getChildren()
				.stream().filter(c1 -> c1.getFullQualifiedName().equals("netV2.subOrg2.subsubOrg2")).findFirst().get();
		assertEquals(Status.ADDED, addedSubComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeEditedComponent() {
		// component: orgV1
		final Component editedComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("orgV1")).findFirst().get();
		assertEquals(Status.EDITED, editedComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeDeletedComponent() {
		// org3V1
		final Component deletedComponent = mergedApplication.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("org3V1")).findFirst().get();
		assertEquals(Status.DELETED, deletedComponent.getExtensionAttributes().get("status"));

	}

}
