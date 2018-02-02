package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.Component;
import net.explorviz.model.Node;

public class MergerTest {

	private final Merger merger = new Merger();

	private Application application1 = new Application();
	private Application application2 = new Application();
	private Application mergedApplication = new Application();
	private final Node dummyNode = new Node();

	/**
	 * For more information on how the two {@link Application}s differ, take a look
	 * at {@link LandscapeExampleCreator#createSimpleApplicationVersion1(Node)} and
	 * {@link LandscapeExampleCreator#createSimpleApplicationVersion2(Node)}.
	 */
	@Before
	public void setUpApps() {
		dummyNode.setName("dummyNode");
		application1 = LandscapeExampleCreator.createSimpleApplicationVersion1(dummyNode);
		application2 = LandscapeExampleCreator.createSimpleApplicationVersion2(dummyNode);

	}

	@Test
	public void testAppMerge() {
		mergedApplication = application2;
		mergedApplication = merger.appMerge(application1, application2);

		// clazz: orgV1.demoV1
		final Clazz originalClazz = mergedApplication.getComponents().get(0).getClazzes().get(0);
		// TODO component: ? ORIGINAL
		// clazz: netV2.subOrgV1.subDemoClass2
		final Clazz addedClazz = mergedApplication.getComponents().get(1).getChildren().get(0).getClazzes().get(0);
		// component: netV2
		final Component addedComponent = mergedApplication.getComponents().get(1);
		// TODO clazz: ? EDITED
		// component: orgV1
		final Component editedComponent = mergedApplication.getComponents().get(0);

		// expected status = ORIGINAL
		assertEquals(Status.ORIGINAL, originalClazz.getExtensionAttributes().get("status"));

		// expected status = ADDED
		assertEquals(Status.ADDED, addedClazz.getExtensionAttributes().get("status"));
		assertEquals(Status.ADDED, addedComponent.getExtensionAttributes().get("status"));

		// expected status = EDITED
		assertEquals(editedComponent.getExtensionAttributes().get("status"), Status.EDITED);

	}

}
