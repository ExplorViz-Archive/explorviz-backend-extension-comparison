package net.explorviz.extension.comparison.model;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.github.jasminb.jsonapi.annotations.Type;

import net.explorviz.extension.comparison.services.Status;

@Type("communicationHistory")
public class CommunicationHistory extends BaseModel {
	private String sourceClazz;
	private String targetClazz;
	private String application;

	private Map<Long, Status> history = new HashMap<>();

	public CommunicationHistory() {

	}

	public CommunicationHistory(String application, String sourceClazz, String targetClazz) {
		this.sourceClazz = sourceClazz;
		this.targetClazz = targetClazz;
		this.setApplication(application);
	}

	public void addHistory(long timestamp, Status status) {
		history.put(timestamp, status);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CommunicationHistory) {
			CommunicationHistory otherClazzPair = (CommunicationHistory) obj;
			return otherClazzPair.sourceClazz.equals(sourceClazz) && otherClazzPair.targetClazz.equals(targetClazz);
		}

		return false;
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
