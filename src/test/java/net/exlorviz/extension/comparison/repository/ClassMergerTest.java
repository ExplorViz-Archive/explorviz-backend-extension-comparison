package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.Clazz;

/**
 * This class holds tests for all states a {@link Clazz} can adopt, refer
 * {@link Status.ORIGINAL}, {@link Status.ADDED} and {@link Status.DELETED}. The
 * merged application is taken from {@link MergerTest}.
 *
 * @author josw
 *
 */
public class ClassMergerTest extends MergerTest {

	@Test
	public void testAppMergeOriginalClazz() {
		// clazz: orgV1.demoV1
		final Clazz originalClazz = mergedApplication.getComponents().get(0).getClazzes().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("orgV1.demoV1")).findFirst().get();
		assertEquals(originalClazz.getFullQualifiedName() + "is not ORIGINAL.", Status.ORIGINAL,
				originalClazz.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeAddedClazz() {
		// clazz: netV2.subOrg2.subsubOrg2.subDemoNet
		final Clazz addedClazz = mergedApplication.getComponents().get(2).getChildren().get(0).getChildren().get(0)
				.getClazzes().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("netV2.subOrg2.subsubOrg2.subDemoNet")).findFirst()
				.get();
		assertEquals(addedClazz.getFullQualifiedName() + "is not ADDED.", Status.ADDED,
				addedClazz.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeDeletedClazz() {
		// clazz orgV1.subOrgV1.subDemo3V1
		final Clazz deletedClazz = mergedApplication.getComponents().get(0).getChildren().get(0).getClazzes().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("orgV1.subOrgV1.subDemo3V1")).findFirst().get();
		assertEquals(deletedClazz.getFullQualifiedName() + "is not DELETED.", Status.DELETED,
				deletedClazz.getExtensionAttributes().get("status"));
	}
}
