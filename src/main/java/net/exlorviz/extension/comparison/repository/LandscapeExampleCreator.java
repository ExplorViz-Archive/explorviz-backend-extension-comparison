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
import net.explorviz.server.repository.LandscapePreparer;

public class LandscapeExampleCreator {
	public static int counter = 1;
	private static int applicationId = 0;

	private static Landscape dummyLandscape = null;

	/**
	 * system: systemVersion1 nodegroup node application: AppVersion1 components:
	 * orgVersion1, subOrgVersion1 classes: demoVersion1, subDemoVersion1
	 * communication:
	 *
	 * @return
	 */
	public static Landscape createSimpleExampleVersion1() {

		if (dummyLandscape != null) {
			dummyLandscape.setActivities(new Random().nextInt(300000));
			return dummyLandscape;
		}

		applicationId = 0;

		final Landscape landscape = new Landscape();
		landscape.setActivities(new Random().nextInt(300000));

		final System ocnEditor = new System();
		ocnEditor.setName("systemVersion1");
		ocnEditor.setParent(landscape);
		landscape.getSystems().add(ocnEditor);

		final NodeGroup ocnEditorNodeGroup = createNodeGroup("10.0.1.1", landscape, ocnEditor);
		final Node ocnEditorNode = createNode("10.0.1.1", ocnEditorNodeGroup);
		final Application ocnEditorApp = createApplication("AppVersion1", ocnEditorNode);

		ocnEditorNodeGroup.getNodes().add(ocnEditorNode);
		ocnEditor.getNodeGroups().add(ocnEditorNodeGroup);

		final Component org = new Component();
		org.getExtensionAttributes().put("status", Status.ORIGINAL);
		org.setName("orgV1");
		org.setFullQualifiedName("orgV1");
		org.setParentComponent(null);
		org.setBelongingApplication(ocnEditorApp);

		final Clazz demoClass = new Clazz();
		demoClass.getExtensionAttributes().put("status", Status.ORIGINAL);
		demoClass.setName("demoV1");
		demoClass.setFullQualifiedName("orgV1.demoV1");
		demoClass.setInstanceCount(100);
		demoClass.setParent(org);

		org.getClazzes().add(demoClass);

		final Component subOrg = new Component();
		subOrg.getExtensionAttributes().put("status", Status.ORIGINAL);
		subOrg.setName("subOrgV1");
		subOrg.setFullQualifiedName("orgV1.subOrgV1");
		subOrg.setParentComponent(org);
		subOrg.setBelongingApplication(ocnEditorApp);

		org.getChildren().add(subOrg);

		final Clazz subDemoClass = new Clazz();
		subDemoClass.getExtensionAttributes().put("status", Status.ORIGINAL);
		demoClass.setName("subDemoV1");
		demoClass.setFullQualifiedName("orgV1.subOrgV1.subDemoV1");
		demoClass.setInstanceCount(100);
		demoClass.setParent(subOrg);

		subOrg.getClazzes().add(subDemoClass);

		ocnEditorApp.getComponents().add(org);

		final Landscape preparedLandscape = LandscapePreparer.prepareLandscape(landscape);

		counter = 1;

		dummyLandscape = preparedLandscape;
		return dummyLandscape;
	}

	public static Landscape createSimpleExampleVersion2() {

		applicationId = 1;

		final Landscape landscapeV2 = LandscapeExampleCreator.createSimpleExampleVersion1();

		final Application ocnEditorApp = landscapeV2.getSystems().get(0).getNodeGroups().get(0).getNodes().get(0)
				.getApplications().get(0);

		// component ADDED
		final Component net = new Component();
		net.getExtensionAttributes().put("status", Status.ORIGINAL);
		net.setName("netV2");
		net.setFullQualifiedName("netV2");
		net.setParentComponent(null);
		net.setBelongingApplication(ocnEditorApp);

		ocnEditorApp.getComponents().add(net);

		// component EDITED
		final Component subOrg = ocnEditorApp.getComponents().get(0).getChildren().get(0);
		subOrg.getExtensionAttributes().put("status", Status.ORIGINAL);
		subOrg.setName("subOrgV1Renamed");
		subOrg.setFullQualifiedName("orgV1.subOrgV1.subOrgV1Renamed");

		// component DELETED

		// component ORIGINAL
		// component org stays the same
		final Landscape preparedLandscapeV2 = LandscapePreparer.prepareLandscape(landscapeV2);

		counter = 2;
		return preparedLandscapeV2;
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
		nodeGroup.setName(name);
		nodeGroup.setParent(system);
		return nodeGroup;
	}

	private static Node createNode(final String ipAddress, final NodeGroup parent) {
		final Node node = new Node();
		node.setIpAddress(ipAddress);
		node.setParent(parent);
		return node;
	}

	private static Application createApplication(final String name, final Node parent) {
		final Application application = new Application();

		// val newId = applicationId
		// application.id = newId
		applicationId = applicationId + 1;
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
		communication.setSource(source);
		communication.setTarget(target);
		communication.setRequests(requests);
		landscape.getApplicationCommunication().add(communication);
	}
}
