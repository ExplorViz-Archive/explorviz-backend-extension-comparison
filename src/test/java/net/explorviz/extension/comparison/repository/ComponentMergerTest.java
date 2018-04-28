package net.explorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.util.MergerHelper;
import net.explorviz.model.application.Component;

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
	/* create flat components for easier searching */
	List<Component> flatComponents = new ArrayList<>();

	@Before
	public void createFlatComponents() {
		flatComponents = MergerHelper.createFlatComponents(mergedApplication.getComponents());
	}

	@Test
	public void testAppMergeOriginalComponent() {
		final Component originalComponent = flatComponents.stream()
				.filter(c1 -> "org2V1".equals(c1.getFullQualifiedName())).findFirst().get();
		assertEquals(originalComponent.getFullQualifiedName() + "is not ORIGINAL.", Status.ORIGINAL,
				originalComponent.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeAddedComponent() {
		final Component addedComponent = flatComponents.stream().filter(c1 -> "netV2".equals(c1.getFullQualifiedName()))
				.findFirst().get();
		assertEquals(addedComponent.getFullQualifiedName() + "is not ADDED.", Status.ADDED,
				addedComponent.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeAddedSubSubComponent() {
		final Component addedSubComponent = flatComponents.stream()
				.filter(c1 -> "netV2.subNet1V2.subsubNet1V2".equals(c1.getFullQualifiedName())).findFirst().get();
		assertEquals(addedSubComponent.getFullQualifiedName() + "is not ADDED.", Status.ADDED,
				addedSubComponent.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeEditedComponent() {
		final Component editedComponent = flatComponents.stream()
				.filter(c1 -> "org1V1".equals(c1.getFullQualifiedName())).findFirst().get();
		assertEquals(editedComponent.getFullQualifiedName() + "is not EDITED.", Status.EDITED,
				editedComponent.getExtensionAttributes().get(PrepareForMerger.STATUS));
	}

	@Test
	public void testAppMergeDeletedComponent() {
		final Component deletedComponent = flatComponents.stream()
				.filter(c1 -> "org3V1".equals(c1.getFullQualifiedName())).findFirst().get();
		assertEquals(deletedComponent.getFullQualifiedName() + "is not DELETED.", Status.DELETED,
				deletedComponent.getExtensionAttributes().get(PrepareForMerger.STATUS));

	}

}
