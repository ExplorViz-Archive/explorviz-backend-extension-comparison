package net.explorviz.extension.comparison.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.extension.comparison.services.Status;

/**
 * Represents the history of a single landscape.
 */
@Type("history")
public class History extends BaseModel {
	private Map<String, Map<String, Map<Long, Status>>> componentHistory = new HashMap<>();
	private Map<String, Map<String, Map<Long, Status>>> clazzHistory = new HashMap<>();

	@Relationship("communicationHistory")
	private List<CommunicationHistory> communicationHistory = new ArrayList<>();

	public History() {
	}

	public Map<String, Map<String, Map<Long, Status>>> getComponentHistory() {
		return componentHistory;
	}

	public void setComponentHistory(Map<String, Map<String, Map<Long, Status>>> componentHistory) {
		this.componentHistory = componentHistory;
	}

	public Map<String, Map<String, Map<Long, Status>>> getClazzHistory() {
		return clazzHistory;
	}

	public void setClazzHistory(Map<String, Map<String, Map<Long, Status>>> clazzHistory) {
		this.clazzHistory = clazzHistory;
	}

	public List<CommunicationHistory> getCommunicationHistory() {
		return communicationHistory;
	}

	public void setCommunicationHistory(List<CommunicationHistory> communicationHistory) {
		this.communicationHistory = communicationHistory;
	}

	public void addApplication(String applicationName) {
		if (!componentHistory.containsKey(applicationName)) {
			componentHistory.put(applicationName, new HashMap<>());
		}

		if (!clazzHistory.containsKey(applicationName)) {
			clazzHistory.put(applicationName, new HashMap<>());
		}

	}

	/**
	 * Adds a history entry to the component history.
	 * 
	 * @param applicationName the name of the application
	 * @param componentName the full qualified name of the component
	 * @param timestamp the timestamp of the change
	 * @param status the change that occurred
	 */
	public void addHistoryToComponent(String applicationName, String componentName, long timestamp, Status status) {
		Map<String, Map<Long, Status>> componentHistory = this.componentHistory.get(applicationName);

		if (!componentHistory.containsKey(componentName)) {
			componentHistory.put(componentName, new LinkedHashMap<>());
		}

		componentHistory.get(componentName).put(timestamp, status);
	}

	
	/**
	 * Adds a history entry to the clazz history.
	 * 
	 * @param applicationName the name of the application
	 * @param componentName the full qualified name of the clazz
	 * @param timestamp the timestamp of the change
	 * @param status the change that occurred
	 */
	public void addHistoryToClazz(String applicationName, String clazzName, long timestamp, Status status) {
		Map<String, Map<Long, Status>> clazzHistory = this.clazzHistory.get(applicationName);

		if (!clazzHistory.containsKey(clazzName)) {
			clazzHistory.put(clazzName, new LinkedHashMap<>());
		}

		clazzHistory.get(clazzName).put(timestamp, status);
	}

	
	/**
	 * Adds a history entry to the communication history.
	 * 
	 * @param applicationName the name of the application
	 * @param componentName the full qualified name of the communication
	 * @param timestamp the timestamp of the change
	 * @param status the change that occurred
	 */
	public void addHistoryToCommunication(String applicationName, String sourceClazz, String targetClazz,
			long timestamp, Status status) {
		CommunicationHistory clazzPair = new CommunicationHistory(applicationName, sourceClazz, targetClazz);
		int index = communicationHistory.indexOf(clazzPair);

		if (index == -1) {
			communicationHistory.add(clazzPair);
			index = communicationHistory.size() - 1;
		}

		communicationHistory.get(index).addHistory(timestamp, status);
	}
}
