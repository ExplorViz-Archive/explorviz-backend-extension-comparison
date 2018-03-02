package net.exlorviz.extension.comparison.repository;

import static org.junit.Assert.assertEquals;

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

	@Test
	public void testAppMergeOriginalCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz originalCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromDemoToSub2()")).findFirst().get();
		assertEquals(Status.ORIGINAL, originalCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeAddedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz addedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromSub2ToDemo()")).findFirst().get();
		assertEquals(Status.ADDED, addedCommunication.getExtensionAttributes().get("status"));
	}

	@Test
	public void testAppMergeEditedCommunication() {
		// communications2: FromDemoToSubEdited(), FromSubToDemo(), FromDemoToSub2(),
		// FromSub2ToDemo()
		final CommunicationClazz editedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromDemoToSubEdited()")).findFirst().get();
		assertEquals(Status.EDITED, editedCommunication.getExtensionAttributes().get("status"));

	}

	@Test
	public void testAppMergeDeletedCommunication() {
		final CommunicationClazz deletedCommunication = mergedApplication.getCommunications().stream()
				.filter(c1 -> c1.getMethodName().equals("FromSub1ToSub3()")).findFirst().get();
		assertEquals(Status.DELETED, deletedCommunication.getExtensionAttributes().get("status"));
	}

}
