package net.explorviz.extension.comparison.services;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.explorviz.extension.comparison.model.History;
import net.explorviz.shared.landscape.model.application.AggregatedClazzCommunication;
import net.explorviz.shared.landscape.model.application.Application;
import net.explorviz.shared.landscape.model.application.Clazz;
import net.explorviz.shared.landscape.model.application.Component;
import net.explorviz.shared.landscape.model.landscape.Landscape;

/**
 * A service that is used to compute the history of landscapes.
 */
public class HistoryService {

	/**
	 * Computes the history of the given landscapes.
	 * @param landscapes the landscapes for the history
	 * @return the computed history
	 */
	public History computeHistory(List<Landscape> landscapes) {
		History history = new History();

		// compare each landscape with the next one in the list
		for (int i = 0; i < landscapes.size() - 1; i++) {
			compareLandscapes(landscapes.get(i), landscapes.get(i + 1), history);
		}

		return history;
	}

	/**
	 * Compares two landscapes with each other.
	 * 
	 * @param oldLandscape the older version
	 * @param newLandscape the newer version
	 * @param history the history to write into
	 */
	private void compareLandscapes(Landscape oldLandscape, Landscape newLandscape, History history) {
		Map<String, Application> oldApplications = MergerHelper.getApplicationsFromLandscape(oldLandscape);
		Map<String, Application> newApplications = MergerHelper.getApplicationsFromLandscape(newLandscape);

		long timestamp = newLandscape.getTimestamp().getTimestamp();

		for (Map.Entry<String, Application> oldApplication : oldApplications.entrySet()) {
			Application newApplication = newApplications.get(oldApplication.getKey());

			if (newApplication != null) {
				compareApplications(oldApplication.getValue(), newApplication, history, timestamp);
			} else {
				markApplication(oldApplication.getValue(), history, timestamp, Status.DELETED);
			}
		}

		for (Map.Entry<String, Application> newApplication : newApplications.entrySet()) {
			if (!oldApplications.containsKey(newApplication.getKey())) {
				markApplication(newApplication.getValue(), history, timestamp, Status.ADDED);
			}
		}
	}

	/**
	 * Adds an entire application into a history.
	 * 
	 * @param application the application to add
	 * @param history the history to add into
	 * @param timestamp the timestamp to add
	 * @param status the status to add
	 */
	private void markApplication(Application application, History history, long timestamp, Status status) {
		application.getExtensionAttributes().put(MergerHelper.STATUS, status);
		history.addApplication(application.getName());

		Collection<Component> components = MergerHelper.flatComponents(application.getComponents()).values();
		Collection<Clazz> clazzes = MergerHelper.getAllClazzes(components).values();

		for (Component component : application.getComponents()) {
			history.addHistoryToComponent(application.getName(), component.getFullQualifiedName(), timestamp, status);
		}

		for (Clazz clazz : clazzes) {
			history.addHistoryToClazz(application.getName(), clazz.getFullQualifiedName(), timestamp, status);
		}

		for (AggregatedClazzCommunication communication : application.getAggregatedClazzCommunications()) {
			history.addHistoryToCommunication(application.getName(), communication.getSourceClazz().getFullQualifiedName(),
					communication.getTargetClazz().getFullQualifiedName(), timestamp, status);
		}

	}

	/**
	 * Compares two applications and adds their differences into a histroy
	 * @param oldApplication the older version
	 * @param newApplication the newer version
	 * @param history the history to add into
	 * @param timestamp the timestamp of the changes
	 */
	private void compareApplications(Application oldApplication, Application newApplication, History history,
			long timestamp) {
		
		assert oldApplication.getName() == newApplication.getName();
		String applicationName = oldApplication.getName();
		
		history.addApplication(applicationName);

		// compare components
		Map<String, Component> flatOldComponents = MergerHelper.flatComponents(oldApplication.getComponents());
		Map<String, Component> flatNewComponents = MergerHelper.flatComponents(newApplication.getComponents());
		
		for (Map.Entry<String, Component> component : flatOldComponents.entrySet()) {
			if (!flatNewComponents.containsKey(component.getKey())) {
				history.addHistoryToComponent(applicationName, component.getValue().getFullQualifiedName(), timestamp, Status.DELETED);
			}
		}

		for (Map.Entry<String, Component> component : flatNewComponents.entrySet()) {
			if (!flatOldComponents.containsKey(component.getKey())) {
				history.addHistoryToComponent(applicationName, component.getValue().getFullQualifiedName(), timestamp, Status.ADDED);
			}
		}

		// compare classes
		Map<String, Clazz> oldClazzes = MergerHelper.getAllClazzes(flatOldComponents.values());
		Map<String, Clazz> newClazzes = MergerHelper.getAllClazzes(flatNewComponents.values());

		for (Map.Entry<String, Clazz> clazz : oldClazzes.entrySet()) {
			if (!newClazzes.containsKey(clazz.getKey())) {
				history.addHistoryToClazz(applicationName, clazz.getValue().getFullQualifiedName(), timestamp, Status.DELETED);
			}
		}

		for (Map.Entry<String, Clazz> clazz : newClazzes.entrySet()) {
			if (!oldClazzes.containsKey(clazz.getKey())) {
				history.addHistoryToComponent(applicationName, clazz.getValue().getFullQualifiedName(), timestamp, Status.ADDED);
			}
		}

		// compare communications
		Map<String, AggregatedClazzCommunication> communications1 = MergerHelper
				.prepareCommuncations(oldApplication.getAggregatedClazzCommunications());
		Map<String, AggregatedClazzCommunication> communications2 = MergerHelper
				.prepareCommuncations(newApplication.getAggregatedClazzCommunications());

		for (Map.Entry<String, AggregatedClazzCommunication> communication : communications2.entrySet()) {
			if (!communications1.containsKey(communication.getKey())) {
				history.addHistoryToCommunication(applicationName, communication.getValue().getSourceClazz().getFullQualifiedName(),
						communication.getValue().getTargetClazz().getFullQualifiedName(), timestamp, Status.ADDED);
			}
		}

		for (Map.Entry<String, AggregatedClazzCommunication> communication : communications1.entrySet()) {
			if (!communications2.containsKey(communication.getKey())) {
				history.addHistoryToCommunication(applicationName, communication.getValue().getSourceClazz().getFullQualifiedName(),
						communication.getValue().getTargetClazz().getFullQualifiedName(), timestamp, Status.DELETED);
			}
		}

	}
}
