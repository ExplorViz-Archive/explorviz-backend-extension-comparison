package net.explorviz.extension.comparison.services;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
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
 * A utility class with methods for the MergeService and HistroyService.
 * @author Daniel
 *
 */
public class MergerHelper {

	public static final String STATUS = "status";

	// utility class -> private constructor
	private MergerHelper() {

	}

	/**
	 * Returns a list of all given components together with all components that are contained in them
	 * @param components the original components
	 * @return all contained components
	 */
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

	/**
	 * Get all classes contained in a collection of components
	 * 
	 * @param collection the components
	 * @return all clazzes contained within
	 */
	public static Map<String, Clazz> getAllClazzes(Collection<Component> collection) {
		Map<String, Clazz> clazzes = new HashMap<>();

		for (Component component : collection) {
			for (Clazz clazz : component.getClazzes()) {
				clazzes.put(clazz.getFullQualifiedName(), clazz);
			}
		}

		return clazzes;
	}

	/**
	 * Returns all applications contained in a landscape
	 * 
	 * @param landscape the landscape
	 * @return all contained applications
	 */
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

	/**
	 * Puts the given communications in a format that allows for easier comparison.
	 * Each communication gets a key with full qualified name of the target clazz and source
	 * clazz concatenated.
	 * 
	 * @param communcations the communications
	 * @return the new map
	 */
	public static Map<String, AggregatedClazzCommunication> prepareCommuncations(
			List<AggregatedClazzCommunication> communcations) {
		Map<String, AggregatedClazzCommunication> newCommunications = new HashMap<>();

		for (AggregatedClazzCommunication communication : communcations) {
			newCommunications.put(communication.getSourceClazz().getFullQualifiedName() + " "
					+ communication.getTargetClazz().getFullQualifiedName(), communication);
		}
		
		return newCommunications;
	}
}
