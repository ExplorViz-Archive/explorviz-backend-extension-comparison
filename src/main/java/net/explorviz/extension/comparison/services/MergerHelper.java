package net.explorviz.extension.comparison.services;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.explorviz.shared.landscape.model.application.Component;

public class MergerHelper {
	private MergerHelper() {

	}
	
	public static Map<String, Component> flatComponents(List<Component> components) {
		Map<String, Component> flatComponents = new HashMap<>();

		for (Component component : components) {
			List<Component> children = component.getChildren();

			if (children.isEmpty()) {
				flatComponents.put(component.getFullQualifiedName(), component);
			} else {
				flatComponents.putAll(flatComponents(children));
			}
		}

		return flatComponents;
	}

}
