package net.exlorviz.extension.comparison.repository;

import java.util.Random;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.Application;
import net.explorviz.model.Clazz;
import net.explorviz.model.Communication;
import net.explorviz.model.Component;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.System;
import net.explorviz.model.helper.ELanguage;
import net.explorviz.repository.LandscapePreparer;

public class LandscapeExampleCreator {
	public static int counter = 1;

	/**
	 * system: systemVersion1 nodegroup node application: AppVersion1 components:
	 * orgVersion1, subOrgVersion1 classes: demoVersion1, subDemoVersion1
	 * communication:
	 *
	 * @return
	 */
	public static Landscape createSimpleLandscapeVersion1() {

		final Landscape landscape = new Landscape();
		landscape.setActivities(new Random().nextInt(300000));

		final System ocnEditor = new System();
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
		org.getExtensionAttributes().put("status", Status.ORIGINAL);
		org.setName("orgV1");
		org.setFullQualifiedName("orgV1");
		org.setParentComponent(null);
		org.setBelongingApplication(simpleAppV1);

		final Clazz demoClass = new Clazz();
		demoClass.initializeID();
		demoClass.getExtensionAttributes().put("status", Status.ORIGINAL);
		demoClass.setName("demoV1");
		demoClass.setFullQualifiedName("orgV1.demoV1");
		demoClass.setInstanceCount(100);
		demoClass.setParent(org);

		org.getClazzes().add(demoClass);

		final Component subOrg = new Component();
		subOrg.initializeID();
		subOrg.getExtensionAttributes().put("status", Status.ORIGINAL);
		subOrg.setName("subOrgV1");
		subOrg.setFullQualifiedName("orgV1.subOrgV1");
		subOrg.setParentComponent(org);
		subOrg.setBelongingApplication(simpleAppV1);

		org.getChildren().add(subOrg);

		final Clazz subDemoClass = new Clazz();
		subDemoClass.initializeID();
		subDemoClass.getExtensionAttributes().put("status", Status.ORIGINAL);
		subDemoClass.setName("subDemoV1");
		subDemoClass.setFullQualifiedName("orgV1.subOrgV1.subDemoV1");
		subDemoClass.setInstanceCount(100);
		subDemoClass.setParent(subOrg);

		subOrg.getClazzes().add(subDemoClass);

		simpleAppV1.getComponents().add(org);

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
		net.getExtensionAttributes().put("status", Status.ORIGINAL);
		net.setName("netV2");
		net.setFullQualifiedName("netV2");
		net.setParentComponent(null);
		net.setBelongingApplication(simpleAppV2);

		// component EDITED
		final Component subOrg2 = new Component();
		subOrg2.initializeID();
		subOrg2.getExtensionAttributes().put("status", Status.ORIGINAL);
		subOrg2.setName("subOrg2");
		subOrg2.setFullQualifiedName("netV2.subOrg2");
		subOrg2.setParentComponent(net);

		// clazz ADDED
		final Clazz subDemoClassNet = new Clazz();
		subDemoClassNet.initializeID();
		subDemoClassNet.getExtensionAttributes().put("status", Status.ORIGINAL);
		subDemoClassNet.setName("subDemoNet");
		subDemoClassNet.setFullQualifiedName("netV2.subOrg2.subDemoNet");
		subDemoClassNet.setInstanceCount(24);
		subDemoClassNet.setParent(subOrg2);

		subOrg2.getClazzes().add(subDemoClassNet);
		net.getChildren().add(subOrg2);

		// clazz ADDED
		final Component subOrg = simpleAppV2.getComponents().get(0).getChildren().get(0);
		final Clazz subDemoClass2 = new Clazz();
		subDemoClass2.initializeID();
		subDemoClass2.getExtensionAttributes().put("status", Status.ORIGINAL);
		subDemoClass2.setName("subDemoV2");
		subDemoClass2.setFullQualifiedName("orgV1.subOrgV1.subDemoV2");
		subDemoClass2.setInstanceCount(100);
		subDemoClass2.setParent(subOrg);

		subOrg.getClazzes().add(subDemoClass2);

		simpleAppV2.getComponents().add(net);

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

	private static NodeGroup createNodeGroup(final String name, final Landscape parent, final System system) {
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
		application.setProgrammingLanguage(ELanguage.JAVA);

		if (name == "Eprints") {
			application.setProgrammingLanguage(ELanguage.PERL);
		}

		application.setName(name);
		parent.getApplications().add(application);
		return application;
	}

	private static void createCommunication(final Application source, final Application target,
			final Landscape landscape, final int requests) {
		final Communication communication = new Communication();
		communication.initializeID();
		communication.setSource(source);
		communication.setTarget(target);
		communication.setRequests(requests);
		landscape.getApplicationCommunication().add(communication);
	}
}
