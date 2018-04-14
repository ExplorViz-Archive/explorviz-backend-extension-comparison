package net.explorviz.extension.comparison.repository;

import java.util.Random;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.ApplicationCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.Component;
import net.explorviz.model.helper.EProgrammingLanguage;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.landscape.System;
import net.explorviz.repository.LandscapePreparer;

public class LandscapeExampleCreator {
	public static int counter = 1;

	/**
	 * Creates an example landscape for testing
	 * explorviz-backend-extension-comparison. For an overview refer to the graphic
	 * in the Wiki of this extension.
	 *
	 * @return example landscape
	 */
	public static Landscape createSimpleLandscapeVersion1() {

		final Landscape landscape = new Landscape();
		landscape.setOverallCalls(new Random().nextInt(300000));
		landscape.initializeID();

		final net.explorviz.model.landscape.System ocnEditor = new System();
		ocnEditor.initializeID();
		ocnEditor.setName("systemVersion1");
		ocnEditor.setParent(landscape);

		final NodeGroup ocnEditorNodeGroup = createNodeGroup("10.0.1.1", landscape, ocnEditor);
		final Node ocnEditorNode = createNode("10.0.1.1", ocnEditorNodeGroup);
		createSimpleApplicationVersion1(ocnEditorNode);

		ocnEditorNodeGroup.getNodes().add(ocnEditorNode);
		ocnEditor.getNodeGroups().add(ocnEditorNodeGroup);
		landscape.getSystems().add(ocnEditor);

		return LandscapePreparer.prepareLandscape(landscape);

	}

	public static Landscape createSimpleLandscapeVersion2() {

		final Landscape landscapeV2 = createSimpleLandscapeVersion1();

		createSimpleApplicationVersion2(landscapeV2.getSystems().get(0).getNodeGroups().get(0).getNodes().get(0));

		return LandscapePreparer.prepareLandscape(landscapeV2);
	}

	public static Application createSimpleApplicationVersion1(final Node node) {
		final Application simpleAppV1 = createApplication("AppVersion1", node);

		final Component org = new Component();
		org.initializeID();
		org.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		org.setName("orgV1");
		org.setFullQualifiedName("orgV1");
		org.setParentComponent(null);
		org.setBelongingApplication(simpleAppV1);

		final Component org2 = new Component();
		org2.initializeID();
		org2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		org2.setName("org2V1");
		org2.setFullQualifiedName("org2V1");
		org2.setParentComponent(null);
		org2.setBelongingApplication(simpleAppV1);

		final Component org3 = new Component();
		org3.initializeID();
		org3.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		org3.setName("org3V1");
		org3.setFullQualifiedName("org3V1");
		org3.setParentComponent(null);
		org3.setBelongingApplication(simpleAppV1);

		final Clazz demoClass = new Clazz();
		demoClass.initializeID();
		demoClass.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		demoClass.setName("demoV1");
		demoClass.setFullQualifiedName("orgV1.demoV1");
		demoClass.setInstanceCount(100);
		demoClass.setParent(org);

		org.getClazzes().add(demoClass);

		final Clazz demoClass3 = new Clazz();
		demoClass3.initializeID();
		demoClass3.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		demoClass3.setName("demo3V1");
		demoClass3.setFullQualifiedName("org3V1.demo3V1");
		demoClass3.setInstanceCount(100);
		demoClass3.setParent(org3);

		final Component subOrg = new Component();
		subOrg.initializeID();
		subOrg.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		subOrg.setName("subOrgV1");
		subOrg.setFullQualifiedName("orgV1.subOrgV1");
		subOrg.setParentComponent(org);
		subOrg.setBelongingApplication(simpleAppV1);

		org.getChildren().add(subOrg);

		final Clazz subDemoClass = new Clazz();
		subDemoClass.initializeID();
		subDemoClass.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		subDemoClass.setName("subDemoV1");
		subDemoClass.setFullQualifiedName("orgV1.subOrgV1.subDemoV1");
		subDemoClass.setInstanceCount(100);
		subDemoClass.setParent(subOrg);

		subOrg.getClazzes().add(subDemoClass);

		final Clazz subDemo3Class = new Clazz();
		subDemo3Class.initializeID();
		subDemo3Class.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		subDemo3Class.setName("subDemo3V1");
		subDemo3Class.setFullQualifiedName("orgV1.subOrgV1.subDemo3V1");
		subDemo3Class.setInstanceCount(100);
		subDemo3Class.setParent(subOrg);

		subOrg.getClazzes().add(subDemo3Class);

		simpleAppV1.getComponents().add(org);
		simpleAppV1.getComponents().add(org2);
		simpleAppV1.getComponents().add(org3);

		// // communication
		// final ClazzCommunication comm1 = new ClazzCommunication();
		// comm1.initializeID();
		// comm1.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		// comm1.setSourceClazz(demoClass);
		// comm1.setTargetClazz(subDemoClass);
		// comm1.setOperationName("FromDemoToSub()");
		// ModelHelper.addClazzCommunication(demoClass, subDemoClass, simpleAppV1, 6, 3,
		// 65, 2, 4, "FromDemoToSub()");
		//
		// final ClazzCommunication comm1Return = new ClazzCommunication();
		// comm1Return.initializeID();
		// comm1Return.getExtensionAttributes().put(PrepareForMerger.STATUS,
		// Status.ORIGINAL);
		// comm1Return.setSourceClazz(subDemoClass);
		// comm1Return.setTargetClazz(demoClass);
		// comm1Return.setOperationName("FromSubToDemo()");
		// ModelHelper.addClazzCommunication(subDemoClass, demoClass, simpleAppV1, 6, 3,
		// 65, 2, 4, "FromSubToDemo()");
		//
		// final ClazzCommunication comm2 = new ClazzCommunication();
		// comm2.initializeID();
		// comm2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		// comm2.setSourceClazz(demoClass);
		// comm2.setTargetClazz(subDemoClass);
		// comm2.setOperationName("FromDemoToSub2()");
		// ModelHelper.addClazzCommunication(demoClass, subDemoClass, simpleAppV1, 6, 3,
		// 65, 2, 4, "FromDemoToSub2()");
		//
		// final ClazzCommunication comm3 = new ClazzCommunication();
		// comm3.initializeID();
		// comm3.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		// comm3.setSourceClazz(subDemoClass);
		// comm3.setTargetClazz(subDemo3Class);
		// comm3.setOperationName("FromSub1ToSub3()");
		// ModelHelper.addClazzCommunication(subDemoClass, subDemo3Class, simpleAppV1,
		// 6, 3, 65, 2, 4, "FromSub1ToSub3()");

		// final List<ClazzCommunication> communications = new ArrayList<>();
		// communications.add(comm1);
		// communications.add(comm1Return);
		// communications.add(comm2);
		// communications.add(comm3);
		//
		// simpleAppV1.setCommunications(communications);

		counter = 1;

		return simpleAppV1;
	}

	/**
	 * Extends the {@link Application} created in
	 * {@link LandscapeExampleCreator#createSimpleApplicationVersion1(Node)}, such
	 * that it can be used for testing
	 * {@link Merger#appMerge(Application, Application)}.
	 *
	 * @param node
	 * @return
	 */
	public static Application createSimpleApplicationVersion2(final Node node) {
		final Application simpleAppV2 = createSimpleApplicationVersion1(node);

		// component ADDED
		final Component net = new Component();
		net.initializeID();
		net.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		net.setName("netV2");
		net.setFullQualifiedName("netV2");
		net.setParentComponent(null);
		net.setBelongingApplication(simpleAppV2);

		final Component subOrg2 = new Component();
		subOrg2.initializeID();
		subOrg2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		subOrg2.setName("subOrg2");
		subOrg2.setFullQualifiedName("netV2.subOrg2");
		subOrg2.setParentComponent(net);

		final Component subsubOrg2 = new Component();
		subsubOrg2.initializeID();
		subsubOrg2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		subsubOrg2.setName("subsubOrg2");
		subsubOrg2.setFullQualifiedName("netV2.subOrg2.subsubOrg2");
		subsubOrg2.setParentComponent(subOrg2);

		// component EDITED

		// component DELETED
		final Component toBeDeletedComponent = simpleAppV2.getComponents().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("org3V1")).findFirst().get();
		simpleAppV2.getComponents().remove(toBeDeletedComponent);

		// clazz ADDED
		final Clazz subDemoClassNet = new Clazz();
		subDemoClassNet.initializeID();
		subDemoClassNet.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		subDemoClassNet.setName("subDemoNet");
		subDemoClassNet.setFullQualifiedName("netV2.subOrg2.subsubOrg2.subDemoNet");
		subDemoClassNet.setInstanceCount(24);
		subDemoClassNet.setParent(subOrg2);

		subsubOrg2.getClazzes().add(subDemoClassNet);
		net.getChildren().add(subOrg2);
		subOrg2.getChildren().add(subsubOrg2);

		// clazz ADDED
		final Component subOrg = simpleAppV2.getComponents().get(0).getChildren().get(0);
		final Clazz subDemoClass2 = new Clazz();
		subDemoClass2.initializeID();
		subDemoClass2.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		subDemoClass2.setName("subDemoV2");
		subDemoClass2.setFullQualifiedName("orgV1.subOrgV1.subDemoV2");
		subDemoClass2.setInstanceCount(100);
		subDemoClass2.setParent(subOrg);

		subOrg.getClazzes().add(subDemoClass2);

		simpleAppV2.getComponents().add(net);

		// clazz DELETED
		final Clazz toBeDeletedClazz = simpleAppV2.getComponents().get(0).getChildren().get(0).getClazzes().stream()
				.filter(c1 -> c1.getFullQualifiedName().equals("orgV1.subOrgV1.subDemo3V1")).findFirst().get();
		simpleAppV2.getComponents().get(0).getChildren().get(0).getClazzes().remove(toBeDeletedClazz);

		// // communicationClazz ADDED
		// final ClazzCommunication commAdded = new ClazzCommunication();
		// commAdded.initializeID();
		// commAdded.getExtensionAttributes().put(PrepareForMerger.STATUS,
		// Status.ORIGINAL);
		// commAdded.setSourceClazz(subDemoClass2);
		// // orgV1.demoV1
		// commAdded.setTargetClazz(simpleAppV2.getComponents().get(0).getClazzes().get(0));
		// commAdded.setOperationName("FromSub2ToDemo()");
		//
		// // communicationClazz EDITED
		// final ClazzCommunication commEdited = simpleAppV2.getCommunications().get(0);
		// commEdited.setOperationName("FromDemoToSubEdited()");
		//
		// simpleAppV2.getAggregatedOutgoingClazzCommunications().add(commAdded);
		//
		// // communicationClazz DELETED
		// final ClazzCommunication toBeDeletedCommunication =
		// simpleAppV2.getCommunications().stream()
		// .filter(c1 ->
		// c1.getMethodName().equals("FromSub1ToSub3()")).findFirst().get();
		// simpleAppV2.getCommunications().remove(toBeDeletedCommunication);

		return simpleAppV2;
	}

	/**
	 * Helper classes for creating nodeGroups, nodes, applications..
	 *
	 * @param name
	 * @param parent
	 * @param system
	 * @return
	 */

	private static NodeGroup createNodeGroup(final String name, final Landscape parent,
			final net.explorviz.model.landscape.System system) {
		final NodeGroup nodeGroup = new NodeGroup();
		nodeGroup.initializeID();
		nodeGroup.setName(name);
		nodeGroup.setParent(system);
		return nodeGroup;
	}

	private static Node createNode(final String ipAddress, final NodeGroup parent) {
		final Node node = new Node();
		node.initializeID();
		node.setIpAddress(ipAddress);
		node.setParent(parent);
		return node;
	}

	private static Application createApplication(final String name, final Node parent) {
		final Application application = new Application();
		application.initializeID();
		application.setParent(parent);

		application.setLastUsage(java.lang.System.currentTimeMillis());
		application.setProgrammingLanguage(EProgrammingLanguage.JAVA);

		if (name == "Eprints") {
			application.setProgrammingLanguage(EProgrammingLanguage.PERL);
		}

		application.setName(name);
		parent.getApplications().add(application);
		return application;
	}

	private static void createCommunication(final Application source, final Application target,
			final Landscape landscape, final int requests) {
		final ApplicationCommunication communication = new ApplicationCommunication();
		communication.initializeID();
		communication.setSourceApplication(source);
		communication.setTargetApplication(target);
		communication.setRequests(requests);
		landscape.getOutgoingApplicationCommunications().add(communication);
	}
}
