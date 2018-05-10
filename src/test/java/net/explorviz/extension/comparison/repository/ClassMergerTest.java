package net.explorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.MergerHelper;
import net.explorviz.extension.comparison.util.PrepareForMerger;
import net.explorviz.model.application.Clazz;

/**
 * This class holds tests for all states a {@link Clazz} can adopt, refer
 * {@link Status.ORIGINAL}, {@link Status.ADDED} and {@link Status.DELETED}. The
 * merged application is taken from {@link MergerTest}.
 *
 * @author josw
 *
 */
public class ClassMergerTest extends MergerTest {
	/* create flat clazzes for easier searching */
	List<Clazz> flatClazzes = new ArrayList<Clazz>();

	@Before
	public void createFlatClazzes() {
		flatClazzes = MergerHelper
				.createFlatClazzes(MergerHelper.createFlatComponents(mergedApplication.getComponents()));
	}

	@Test
	public void testAppMergeOriginalClazz() {
		final Clazz originalClazz = flatClazzes.stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("org1V1.clazz1V1")).findFirst().get();
		assertEquals(originalClazz.getFullQualifiedName() + "is not ORIGINAL.", Status.ORIGINAL,
				originalClazz.getExtensionAttributes().get(PrepareForMerger.STATUS));

	}

	@Test
	public void testAppMergeAddedClazz() {
		final Clazz addedClazz = flatClazzes.stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("netV2.subNet1V2.subsubNet1V2.clazz1V2")).findFirst()
				.get();
		assertEquals(addedClazz.getFullQualifiedName() + "is not ADDED.", Status.ADDED,
				addedClazz.getExtensionAttributes().get(PrepareForMerger.STATUS));

	}

	@Test
	public void testAppMergeDeletedClazz() {
		final Clazz deletedClazz = flatClazzes.stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("org1V1.subOrg1V1.subClazz3V1")).findFirst().get();
		assertEquals(deletedClazz.getFullQualifiedName() + "is not DELETED.", Status.DELETED,
				deletedClazz.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}
}
