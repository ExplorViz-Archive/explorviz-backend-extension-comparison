package net.explorviz.extension.comparison.services;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.extension.comparison.model.History;
import net.explorviz.shared.landscape.model.application.Component;
import net.explorviz.shared.landscape.model.landscape.Landscape;

public class HistoryService {
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryService.class);

	public History computeHistory(List<Landscape> landscapes) {
		Landscape lastLandscape = landscapes.get(0);
		//initializeComponents(mergedLandscape.getSystems().get(0).getNodeGroups().get(0).getNodes().get(0)
				//.getApplications().get(0).getComponents());
		
		History history = new History();

		for (int i = 1; i < landscapes.size(); i++) {
			computeLandscapeDifference(lastLandscape, landscapes.get(i), history);
			lastLandscape = landscapes.get(i);
		}

		return history;
	}

	private void computeLandscapeDifference(Landscape mergedLandscape, Landscape additionalLandscape, History history) {
		Map<String, Component> mergedComponents = MergerHelper.flatComponents(mergedLandscape.getSystems().get(0).getNodeGroups()
				.get(0).getNodes().get(0).getApplications().get(0).getComponents());
		Map<String, Component> additionalComponents = MergerHelper.flatComponents(additionalLandscape.getSystems().get(0)
				.getNodeGroups().get(0).getNodes().get(0).getApplications().get(0).getComponents());
		
		long timestamp = additionalLandscape.getTimestamp().getTimestamp();

		//Map<Component, Status> history = new HashMap<>();

		for (Map.Entry<String, Component> component : mergedComponents.entrySet()) {
			if (!additionalComponents.containsKey(component.getKey())) {
				history.addHistoryToComponent(component.getValue().getFullQualifiedName(), timestamp, Status.REMOVED);
			} 
		}

		for (Map.Entry<String, Component> component : additionalComponents.entrySet()) {
			if (!mergedComponents.containsKey(component.getKey())) {
				history.addHistoryToComponent(component.getValue().getFullQualifiedName(), timestamp, Status.ADDED);
			} 
		}

	}

	private void initializeComponents(List<Component> components) {
		for (Component component : components) {
			List<Component> children = component.getChildren();

			if (children.isEmpty()) {
				component.getExtensionAttributes().put("Status", Status.ORIGINAL);
				component.getExtensionAttributes().put("History", new LinkedHashMap<Long, Status>());
			} else {
				initializeComponents(children);
			}
		}
	}
}
