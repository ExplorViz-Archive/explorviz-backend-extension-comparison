package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.Clazz;

public class ClassMergerTest extends MergerTest {

	@Test
	public void testAppMergeOriginalClazz() {
		// clazz: orgV1.demoV1
		final Clazz originalClazz = mergedApplication.getComponents().get(0).getClazzes().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("orgV1.demoV1")).findFirst().get();
		assertEquals(Status.ORIGINAL, originalClazz.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeAddedClazz() {
		// clazz: netV2.subOrg2.subsubOrg2.subDemoNet
		final Clazz addedClazz = mergedApplication.getComponents().get(2).getChildren().get(0).getChildren().get(0)
				.getClazzes().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("netV2.subOrg2.subsubOrg2.subDemoNet")).findFirst()
				.get();
		assertEquals(Status.ADDED, addedClazz.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeDeletedClazz() {
		// clazz orgV1.subOrgV1.subDemo3V1
		final Clazz deletedClazz = mergedApplication.getComponents().get(0).getChildren().get(0).getClazzes().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("orgV1.subOrgV1.subDemo3V1")).findFirst().get();
		assertEquals(Status.DELETED, deletedClazz.getExtensionAttributes().get("status"));
	}
}
