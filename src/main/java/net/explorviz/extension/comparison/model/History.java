package net.explorviz.extension.comparison.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.explorviz.extension.comparison.services.Status;

public class History {
	
	private Map<String, Map<Long, Status>> componentHistory = new HashMap<>();
	
	public History() {
		
	}
	
	public void addHistoryToComponent(String componentName, long timestamp, Status status) {
		if(!componentHistory.containsKey(componentName)) {
			componentHistory.put(componentName, new LinkedHashMap<>());
		}
		
		componentHistory.get(componentName).put(timestamp, status);
	}
	
	public Map<String, Map<Long, Status>> getComponentHistory() {
		return componentHistory;
	}

}
