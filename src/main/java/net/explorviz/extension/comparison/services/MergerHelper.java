package net.explorviz.extension.comparison.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.shared.landscape.model.application.Application;
import net.explorviz.shared.landscape.model.application.Clazz;
import net.explorviz.shared.landscape.model.application.Component;
import net.explorviz.shared.landscape.model.landscape.Landscape;
import net.explorviz.shared.landscape.model.landscape.Node;
import net.explorviz.shared.landscape.model.landscape.NodeGroup;
import net.explorviz.shared.landscape.model.landscape.System;

public class MergerHelper {

	public static final String STATUS = "status";
	private static final Logger LOGGER = LoggerFactory.getLogger(MergerHelper.class);

	private MergerHelper() {

	}

	public static Map<String, Component> flatComponents(List<Component> components) {
		Map<String, Component> flatComponents = new HashMap<>();
		
		flatComponentsInternal(components, flatComponents);

		return flatComponents;
	}

	private static void flatComponentsInternal(List<Component> components, Map<String, Component> flatComponents) {
		for (Component component : components) {
			List<Component> children = component.getChildren();
			flatComponents.put(component.getFullQualifiedName(), component);

			if (!children.isEmpty()) {
				flatComponentsInternal(children, flatComponents);
			}
		}
	}

	public static Map<String, Clazz> getAllClazzes(Collection<Component> collection) {
		Map<String, Clazz> clazzes = new HashMap<>();

		for (Component component : collection) {
			for (Clazz clazz : component.getClazzes()) {
				clazzes.put(clazz.getFullQualifiedName(), clazz);
			}
		}

		return clazzes;
	}

	public static Map<String, Application> getApplicationsFromLandscape(Landscape landscape) {
		Map<String, Application> applications = new HashMap<>();

		for (System system : landscape.getSystems()) {
			for (NodeGroup nodeGroup : system.getNodeGroups()) {
				for (Node node : nodeGroup.getNodes()) {
					for (Application application : node.getApplications()) {
						applications.put(application.getName(), application);
					}
				}
			}
		}

		return applications;
	}
}
