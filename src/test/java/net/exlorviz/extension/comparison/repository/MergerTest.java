package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.CommunicationClazz;
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
		mergedApplication = application2;
		mergedApplication = merger.appMerge(application1, application2);

	}

	@Test
	public void testAppMergeOriginalClazz() {
		// clazz: orgV1.demoV1
		final Clazz originalClazz = mergedApplication.getComponents().get(0).getClazzes().get(0);
		assertEquals(Status.ORIGINAL, originalClazz.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeAddedClazz() {
		// clazz: netV2.subOrg2.subDemoNet
		final Clazz addedClazz = mergedApplication.getComponents().get(2).getChildren().get(0).getClazzes().get(0);
		assertEquals(Status.ADDED, addedClazz.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeEditedClazz() {
		fail("not implemented yet");
	}

	@Test
	public void testAppMergeOriginalComponent() {
		// component: org2
		final Component originalComponent = mergedApplication.getComponents().get(1);
		assertEquals(Status.ORIGINAL, originalComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedComponent() {
		// component: netV2
		final Component addedComponent = mergedApplication.getComponents().get(2);
		assertEquals(Status.ADDED, addedComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeEditedComponent() {
		// component: orgV1
		final Component editedComponent = mergedApplication.getComponents().get(0);
		assertEquals(Status.EDITED, editedComponent.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeOriginalCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz originalCommunication = mergedApplication.getCommunications().get(2);
		assertEquals(Status.ORIGINAL, originalCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz addedCommunication = mergedApplication.getCommunications().get(3);
		assertEquals(Status.ADDED, addedCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeEditedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz editedCommunication = mergedApplication.getCommunications().get(0);
		assertEquals(Status.EDITED, editedCommunication.getExtensionAttributes().get("status"));

	}

}
