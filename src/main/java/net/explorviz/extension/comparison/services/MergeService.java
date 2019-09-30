package net.explorviz.extension.comparison.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import net.explorviz.shared.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.shared.landscape.model.application.Application;
import net.explorviz.shared.landscape.model.application.Clazz;
import net.explorviz.shared.landscape.model.application.Component;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.landscape.Node;
import net.explorviz.shared.landscape.model.landscape.NodeGroup;
import net.explorviz.shared.landscape.model.landscape.System;

/**
 * A service that merges two landscapes.
 */
public class MergeService {

	/**
	 * Merges two landscapes
	 * 
	 * @param landscape1 the first version
	 * @param landscape2 the second version
	 * @return the merged landscape
	 */
	public Landscape mergeLandscapes(Landscape landscape1, Landscape landscape2) {
		Map<String, Application> applications1 = MergerHelper.getApplicationsFromLandscape(landscape1);
		Map<String, Application> applications2 = MergerHelper.getApplicationsFromLandscape(landscape2);

		for (Map.Entry<String, Application> application2 : applications2.entrySet()) {
			Application application1 = applications1.get(application2.getKey());

			if (application1 != null) {
				application2.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ORIGINAL);
				mergeApplications(application1, application2.getValue());
			} else {
				markApplication(application2.getValue(), Status.ADDED);
			}
		}

		for (Map.Entry<String, Application> application : applications1.entrySet()) {
			if (!applications2.containsKey(application.getKey())) {
				// get all entities the application is contained in
				Node node = application.getValue().getParent();
				NodeGroup nodeGroup = node.getParent();
				System system = nodeGroup.getParent();

				// test if those entities already exist in landscape 2
				int systemIndex = indexOfSystem(landscape2, system.getName());

				if (systemIndex != -1) {
					system = landscape2.getSystems().get(systemIndex);
				} else {
					system.getNodeGroups().clear();
					system.setParent(landscape2);
					landscape2.getSystems().add(system);
				}

				int nodeGroupIndex = indexOfNodeGroup(system, nodeGroup.getName());

				if (nodeGroupIndex != -1) {
					nodeGroup = system.getNodeGroups().get(nodeGroupIndex);
				} else {
					nodeGroup.getNodes().clear();
					nodeGroup.setParent(system);
					system.getNodeGroups().add(nodeGroup);
				}

				int nodeIndex = indexOfNode(nodeGroup, node.getName());

				if (nodeIndex != -1) {
					node = nodeGroup.getNodes().get(nodeIndex);
				} else {
					node.getApplications().clear();
					node.setParent(nodeGroup);
					nodeGroup.getNodes().add(node);
				}

				node.getApplications().add(application.getValue());
				markApplication(application.getValue(), Status.DELETED);
			}
		}

		return landscape2;
	}

	private int indexOfSystem(Landscape landscape, String systemName) {
		for (int i = 0; i < landscape.getSystems().size(); i++) {
			if (landscape.getSystems().get(i).getName().equals(systemName)) {
				return i;
			}
		}

		return -1;
	}

	private int indexOfNodeGroup(System system, String nodeGroupName) {
		for (int i = 0; i < system.getNodeGroups().size(); i++) {
			if (system.getNodeGroups().get(i).getName().equals(nodeGroupName)) {
				return i;
			}
		}

		return -1;
	}

	private int indexOfNode(NodeGroup nodeGroup, String nodeName) {
		for (int i = 0; i < nodeGroup.getNodes().size(); i++) {
			if (nodeGroup.getNodes().get(i).getName().equals(nodeName)) {
				return i;
			}
		}

		return -1;
	}

	/**
	 * Marks an application with a single status.
	 * 
	 * @param application the application to mark
	 * @param status the status to mark
	 */
	private void markApplication(Application application, Status status) {
		application.getExtensionAttributes().put(MergerHelper.STATUS, status);

		Collection<Component> components = MergerHelper.flatComponents(application.getComponents()).values();
		Collection<Clazz> clazzes = MergerHelper.getAllClazzes(components).values();

		for (Component component : components) {
			component.getExtensionAttributes().put(MergerHelper.STATUS, status);
		}

		for (Clazz clazz : clazzes) {
			clazz.getExtensionAttributes().put(MergerHelper.STATUS, status);
		}

		for (AggregatedClazzCommunication communication : application.getAggregatedClazzCommunications()) {
			communication.getExtensionAttributes().put(MergerHelper.STATUS, status);
		}
	}

	/**
	 * Merges two applications together.
	 * 
	 * @param application1 the first version
	 * @param application2 the second version
	 */
	private void mergeApplications(Application application1, Application application2) {
		
		// compare components
		Map<String, Component> components1 = MergerHelper.flatComponents(application1.getComponents());
		Map<String, Component> components2 = MergerHelper.flatComponents(application2.getComponents());

		for (Map.Entry<String, Component> component : components2.entrySet()) {
			if (components1.containsKey(component.getKey())) {
				component.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ORIGINAL);
			} else {
				component.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ADDED);
			}
		}

		Map<String, Component> addedComponentsTo2 = new HashMap<>();

		for (Map.Entry<String, Component> component : components1.entrySet()) {
			if (!components2.containsKey(component.getKey())) {
				// component gets added to the second application
				Component parentIn1 = component.getValue().getParentComponent();

				if (parentIn1 == null) {
					// component is a top level component
					application2.getComponents().add(component.getValue());
				} else {
					Component parentIn2 = components2.get(parentIn1.getFullQualifiedName());

					if (parentIn2 != null) {
						// component gets added as a child
						parentIn2.getChildren().add(component.getValue());
						component.getValue().setParentComponent(parentIn2);
					}
					addedComponentsTo2.put(component.getKey(), component.getValue());
				}

				component.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.DELETED);
			}
		}

		// compare clazzes
		Map<String, Clazz> clazzes1 = MergerHelper.getAllClazzes(components1.values());
		Map<String, Clazz> clazzes2 = MergerHelper.getAllClazzes(components2.values());

		// add the added components so they can be found by the clazzes
		components2.putAll(addedComponentsTo2);

		for (Map.Entry<String, Clazz> clazz : clazzes2.entrySet()) {
			if (clazzes1.containsKey(clazz.getKey())) {
				clazz.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ORIGINAL);
			} else {
				clazz.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ADDED);
			}
		}

		for (Map.Entry<String, Clazz> clazz : clazzes1.entrySet()) {
			if (!clazzes2.containsKey(clazz.getKey())) {
				
				// add clazz to the second application
				Component clazzParent = components2.get(clazz.getValue().getParent().getFullQualifiedName());

				clazz.getValue().setParent(clazzParent);
				clazzParent.getClazzes().add(clazz.getValue());
				clazz.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.DELETED);
			}
		}

		// compare communications
		Map<String, AggregatedClazzCommunication> communications1 = MergerHelper
				.prepareCommuncations(application1.getAggregatedClazzCommunications());
		Map<String, AggregatedClazzCommunication> communications2 = MergerHelper
				.prepareCommuncations(application2.getAggregatedClazzCommunications());

		for (Map.Entry<String, AggregatedClazzCommunication> communication : communications2.entrySet()) {
			if (communications1.containsKey(communication.getKey())) {
				communication.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ORIGINAL);
			} else {
				communication.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ADDED);
			}
		}

		for (Map.Entry<String, AggregatedClazzCommunication> communication : communications1.entrySet()) {
			if (!communications2.containsKey(communication.getKey())) {
				// add the communication to the second application
				application2.getAggregatedClazzCommunications().add(communication.getValue());
				communication.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.DELETED);
			}
		}
	}
}