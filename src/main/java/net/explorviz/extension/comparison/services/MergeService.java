package net.explorviz.extension.comparison.services;

import java.util.ArrayList;
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

public class MergeService {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MergeService.class);

	public Landscape mergeLandscapes(Landscape landscape1, Landscape landscape2) {
		Map<String, Application> applications1 = MergerHelper.getApplicationsFromLandscape(landscape1);
		Map<String, Application> applications2 = MergerHelper.getApplicationsFromLandscape(landscape2);

		for (Map.Entry<String, Application> application : applications2.entrySet()) {
			if (applications1.containsKey(application.getKey())) {
				application.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.ORIGINAL);
				mergeApplications(applications1.get(application.getKey()), application.getValue());
			} else {
				markApplication(application.getValue(), Status.ADDED);
			}
		}

		for (Map.Entry<String, Application> application : applications1.entrySet()) {
			if (!applications2.containsKey(application.getKey())) {
				// TODO: add Application to second landscape
			}
		}

		return landscape2;
	}

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
	}

	private void mergeApplications(Application application1, Application application2) {
		Map<String, Component> components1 = MergerHelper.flatComponents(application1.getComponents());
		Map<String, Component> components2 = MergerHelper.flatComponents(application2.getComponents());
		
		List<String> applications = new ArrayList<>();
		
		application1.getComponents().forEach((component) -> applications.add(component.getFullQualifiedName()));
		
		LOGGER.info(applications.toString());
		//LOGGER.info(application2.getComponents().toString());
		
		LOGGER.info(String.join(",", components1.keySet()));
		LOGGER.info(String.join(",", components2.keySet()));

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
				Component parentIn1 = component.getValue().getParentComponent();

				if (parentIn1 == null) {
					application2.getComponents().add(component.getValue());
				} else {
					if (components2.containsKey(parentIn1.getFullQualifiedName())) {
						Component parentIn2 = components2.get(parentIn1.getFullQualifiedName());

						parentIn2.getChildren().add(component.getValue());
						component.getValue().setParentComponent(parentIn2);
					} 
					addedComponentsTo2.put(component.getKey(), component.getValue());
				}

				component.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.DELETED);
			}
		}

		Map<String, Clazz> clazzes1 = MergerHelper.getAllClazzes(components1.values());
		Map<String, Clazz> clazzes2 = MergerHelper.getAllClazzes(components2.values());
		
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
				Component clazzParent = components2.get(clazz.getValue().getParent().getFullQualifiedName());

				clazz.getValue().setParent(clazzParent);
				clazzParent.getClazzes().add(clazz.getValue());
				clazz.getValue().getExtensionAttributes().put(MergerHelper.STATUS, Status.DELETED);
			}
		}
	}
}
