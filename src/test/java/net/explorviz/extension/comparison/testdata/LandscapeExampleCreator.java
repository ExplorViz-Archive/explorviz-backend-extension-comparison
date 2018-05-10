package net.explorviz.extension.comparison.testdata;

import java.util.List;
import java.util.Random;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.repository.Merger;
import net.explorviz.extension.comparison.util.MergerHelper;
import net.explorviz.extension.comparison.util.PrepareForMerger;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.helper.EProgrammingLanguage;
import net.explorviz.model.helper.ModelHelper;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.model.landscape.System;
import net.explorviz.repository.LandscapePreparer;

public class LandscapeExampleCreator {

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

		final Component org1 = createComponent(simpleAppV1, null, "org1V1", "org1V1");
		final Clazz clazz1 = createClazz(simpleAppV1, org1, "org1V1.clazz1V1", "clazz1V1", 100);
		final Component subOrg1 = createComponent(simpleAppV1, org1, "orgV1.subOrg1V1", "subOrg1V1");
		final Clazz subClazz1 = createClazz(simpleAppV1, subOrg1, "org1V1.subOrg1V1.subClazz1V1", "subClazz1V1", 90);
		final Clazz subClazz3 = createClazz(simpleAppV1, subOrg1, "org1V1.subOrg1V1.subClazz3V1", "subClazz3V1", 42);

		final Component org2 = createComponent(simpleAppV1, null, "org2V1", "org2V1");

		final Component org3 = createComponent(simpleAppV1, null, "org3V1", "org3V1");
		final Clazz clazz3 = createClazz(simpleAppV1, org3, "org3V1.clazz3V1", "clazz3V1", 500);

		final ClazzCommunication communication_clazz1ToSubClazz1 = createClazzCommunication(clazz1, subClazz1,
				"fromClazz1ToSubClazz1()");
		final ClazzCommunication communication_subClazz1ToClazz1 = createClazzCommunication(subClazz1, clazz1,
				"fromSubClazz1ToClazz1()");
		final ClazzCommunication communication2_clazz1ToSubClazz1 = createClazzCommunication(clazz1, subClazz1,
				"AnotherFromClazz1ToSubClazz1()");
		final ClazzCommunication communication_subClazz1ToSubClazz3 = createClazzCommunication(subClazz1, subClazz3,
				"fromSubClazz1ToSubClazz3");

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
		final List<Component> flatComponents = MergerHelper.createFlatComponents(simpleAppV2.getComponents());

		// component ADDED
		final Component net = createComponent(simpleAppV2, null, "netV2", "netV2");

		final Component subNet = createComponent(simpleAppV2, net, "netV2.subNet1V2", "subNet1V2");
		final Component subSubNet = createComponent(simpleAppV2, subNet, "netV2.subNet1V2.subsubNet1V2",
				"subsubNet1V2");

		// component EDITED

		// component DELETED
		final Component toBeDeletedComponent = flatComponents.stream()
				.filter(c1 -> "org3V1".equals(c1.getFullQualifiedName())).findFirst().get();
		simpleAppV2.getComponents().remove(toBeDeletedComponent);

		// clazz ADDED
		final Clazz subSubNetClazz = createClazz(simpleAppV2, subSubNet, "netV2.subNet1V2.subsubNet1V2.clazz1V2",
				"clazz1V2", 280);

		// clazz ADDED
		final Component subOrg = flatComponents.stream().filter(c -> "orgV1.subOrg1V1".equals(c.getFullQualifiedName()))
				.findFirst().get();
		final Clazz subOrgClazzV2 = createClazz(simpleAppV2, subOrg, "orgV1.subOrg1V1.clazz2V2", "clazz2V2", 66);

		// clazz DELETED
		final List<Clazz> flatClazzes = MergerHelper.createFlatClazzes(flatComponents);
		final Clazz toBeDeletedClazz = flatClazzes.stream()
				.filter(c1 -> "org1V1.subOrg1V1.subClazz3V1".equals(c1.getFullQualifiedName())).findFirst().get();
		toBeDeletedClazz.getParent().getClazzes().remove(toBeDeletedClazz);

		// clazzCommunication ADDED
		final Clazz clazz1V1 = flatClazzes.stream().filter(c -> "org1V1.clazz1V1".equals(c.getFullQualifiedName()))
				.findFirst().get();
		final ClazzCommunication communication_subOrgClazzV2ToClazz1 = createClazzCommunication(subOrgClazzV2, clazz1V1,
				"fromSubOrgClazzV2ToClazz1()");

		// clazzCommunication DELETED
		final List<ClazzCommunication> flatClazzCommunications = MergerHelper
				.createFlatClazzCommunications(simpleAppV2.getAggregatedOutgoingClazzCommunications());
		final ClazzCommunication clazzCommunicationToBeDeleted = flatClazzCommunications.stream()
				.filter(c -> "fromSubClazz1ToSubClazz3".equals(c.getOperationName())).findFirst().get();
		// deleted communication from source class and from aggregated communication
		clazzCommunicationToBeDeleted.getSourceClazz().getOutgoingClazzCommunications()
				.remove(clazzCommunicationToBeDeleted);
		final List<AggregatedClazzCommunication> aggregatedClazzCommunications = simpleAppV2
				.getAggregatedOutgoingClazzCommunications();
		final AggregatedClazzCommunication deletedClazzCommunicationInAggregated = aggregatedClazzCommunications
				.stream().filter(ac -> clazzCommunicationToBeDeleted.getSourceClazz().equals(ac.getSourceClazz()))
				.filter(ac -> clazzCommunicationToBeDeleted.getTargetClazz().equals(ac.getTargetClazz())).findAny()
				.get();
		deletedClazzCommunicationInAggregated.getOutgoingClazzCommunications().remove(clazzCommunicationToBeDeleted);

		// prepare EDITED aggregatedCommunication
		// delete
		final ClazzCommunication clazzCommunicationToBeDeleted2 = flatClazzCommunications.stream()
				.filter(c -> "AnotherFromClazz1ToSubClazz1()".equals(c.getOperationName())).findFirst().get();
		clazzCommunicationToBeDeleted2.getSourceClazz().getOutgoingClazzCommunications()
				.remove(clazzCommunicationToBeDeleted2);

		final AggregatedClazzCommunication deletedClazzCommunication2InAggregated = aggregatedClazzCommunications
				.stream().filter(ac -> clazzCommunicationToBeDeleted2.getSourceClazz().equals(ac.getSourceClazz()))
				.filter(ac -> clazzCommunicationToBeDeleted2.getTargetClazz().equals(ac.getTargetClazz())).findAny()
				.get();
		deletedClazzCommunication2InAggregated.getOutgoingClazzCommunications().remove(clazzCommunicationToBeDeleted2);

		// add
		final Clazz subClazz1 = flatClazzes.stream()
				.filter(c -> "org1V1.subOrg1V1.subClazz1V1".equals(c.getFullQualifiedName())).findAny().get();
		final ClazzCommunication communication_clazz1TosubClazz1 = createClazzCommunication(clazz1V1, subClazz1,
				"NEWAnotherFromClazz1ToSubClazz1()");

		return simpleAppV2;
	}

	/**
	 * Helper methods for creating nodeGroups, nodes, applications..
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

		application.setName(name);
		parent.getApplications().add(application);
		return application;
	}

	private static Component createComponent(final Application application, final Component parent,
			final String fullName, final String name) {
		final Component newComponent = new Component();
		newComponent.initializeID();
		newComponent.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		newComponent.setName(name);
		newComponent.setFullQualifiedName(fullName);
		newComponent.setParentComponent(parent);
		newComponent.setBelongingApplication(application);

		if (parent != null) {
			parent.getChildren().add(newComponent);
		} else {
			application.getComponents().add(newComponent);
		}

		return newComponent;
	}

	private static Clazz createClazz(final Application application, final Component parent, final String fullName,
			final String name, final int instanceCount) {
		final Clazz newClazz = new Clazz();
		newClazz.initializeID();
		newClazz.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		newClazz.setName(name);
		newClazz.setFullQualifiedName(fullName);
		newClazz.setInstanceCount(instanceCount);
		newClazz.setParent(parent);

		parent.getClazzes().add(newClazz);

		return newClazz;
	}

	private static ClazzCommunication createClazzCommunication(final Clazz source, final Clazz target,
			final String operationName) {
		final ClazzCommunication newClazzCommunication = new ClazzCommunication();
		newClazzCommunication.initializeID();
		newClazzCommunication.getExtensionAttributes().put(PrepareForMerger.STATUS, Status.ORIGINAL);
		newClazzCommunication.setSourceClazz(source);
		newClazzCommunication.setTargetClazz(target);
		newClazzCommunication.setOperationName(operationName);

		source.getOutgoingClazzCommunications().add(newClazzCommunication);
		ModelHelper.updateAggregatedClazzCommunication(source.getParent().getBelongingApplication(),
				newClazzCommunication);
		return newClazzCommunication;
	}
}
