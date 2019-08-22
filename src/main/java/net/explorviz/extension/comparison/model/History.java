package net.explorviz.extension.comparison.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.extension.comparison.services.Status;

@Type("history")
public class History extends BaseModel {
	private Map<String, Map<Long, Status>> componentHistory = new HashMap<>();
	private Map<String, Map<Long, Status>> clazzHistory = new HashMap<>();

	public void setComponentHistory(Map<String, Map<Long, Status>> componentHistory) {
		this.componentHistory = componentHistory;
	}

	public Map<String, Map<Long, Status>> getClazzHistory() {
		return clazzHistory;
	}

	public void setClazzHistory(Map<String, Map<Long, Status>> clazzHistory) {
		this.clazzHistory = clazzHistory;
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
	
	public void addHistoryToClazz(String clazzName, long timestamp, Status status) {
		if(!clazzHistory.containsKey(clazzName)) {
			clazzHistory.put(clazzName, new LinkedHashMap<>());
		}
		
		clazzHistory.get(clazzName).put(timestamp, status);
	}

	public History() {
		// default constructor for JSON API parsing
	}
}
