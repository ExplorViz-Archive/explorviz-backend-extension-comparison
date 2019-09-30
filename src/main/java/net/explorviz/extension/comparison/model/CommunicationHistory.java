package net.explorviz.extension.comparison.model;

import java.util.HashMap;
import java.util.Map;

import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.extension.comparison.services.Status;

/**
 * Represents the history of a single communication.
 */
@Type("communicationHistory")
public class CommunicationHistory extends BaseModel {
	private String sourceClazz;
	private String targetClazz;
	private String application;

	private Map<Long, Status> history = new HashMap<>();

	public CommunicationHistory() {

	}

	/**
	 * Creates a new communication history.
	 * 
	 * @param application the name of the application
	 * @param sourceClazz the full qualified name of the source clazz of the communication
	 * @param targetClazz the full qualified name of the target clazz of the communication
	 */
	public CommunicationHistory(String application, String sourceClazz, String targetClazz) {
		this.sourceClazz = sourceClazz;
		this.targetClazz = targetClazz;
		this.application = application;
	}

	/**
	 * Adds a new entry to this communication history.
	 * 
	 * @param timestamp the timestamp of the change
	 * @param status the change that occured
	 */
	public void addHistory(long timestamp, Status status) {
		history.put(timestamp, status);
	}

	public String getSourceClazz() {
		return sourceClazz;
	}

	public void setSourceClazz(String sourceClazz) {
		this.sourceClazz = sourceClazz;
	}

	public String getTargetClazz() {
		return targetClazz;
	}

	public void setTargetClazz(String targetClazz) {
		this.targetClazz = targetClazz;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public Map<Long, Status> getHistory() {
		return history;
	}

	public void setHistory(Map<Long, Status> history) {
		this.history = history;
	}

}
