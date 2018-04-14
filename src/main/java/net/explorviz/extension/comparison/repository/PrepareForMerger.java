package net.explorviz.extension.comparison.repository;

import java.util.List;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Application;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;
import net.explorviz.model.landscape.Landscape;

/**
 * Currently extends an {@link Application} by a {@link Status}. The
 * {@link Status} is necessary to merge two {@link Application}s. In future
 * {@link Landscape}s should be merged, too.
 *
 * @author josw
 *
 */
public class PrepareForMerger {

	public static final String STATUS = "status";

	/**
	 * Adds a {@link Status} attribute to every element in the {@link Application}.
	 * This serves as preparation for merging two {@link Application}s.
	 *
	 * @param mergingApp
	 *            {@link Application} that should be merged without status attribute
	 * @return {@link Application} that should be merged with status attribute
	 */
	public Application addStatusToApp(final Application mergingApp) {

		for (final AggregatedClazzCommunication aggregatedCommunication : mergingApp
				.getAggregatedOutgoingClazzCommunications()) {
			for (final ClazzCommunication communication : aggregatedCommunication.getOutgoingClazzCommunications()) {
				communication.getExtensionAttributes().put(STATUS, Status.ORIGINAL);
			}
		}

		for (final Component component : mergingApp.getComponents()) {
			component.getExtensionAttributes().put(STATUS, Status.ORIGINAL);

			for (final Clazz clazz : component.getClazzes()) {
				clazz.getExtensionAttributes().put(STATUS, Status.ORIGINAL);
			}

			this.componentRecursive(component.getChildren());
		}

		return mergingApp;
	}

	/**
	 * Helps the {@link #addStatusToApp(Application)} method to add the status
	 * recursively to all children of {@link Component}s.
	 *
	 * @param components
	 *            list of child components
	 */
	private void componentRecursive(final List<Component> components) {
		for (final Component component : components) {
			component.getExtensionAttributes().put(STATUS, Status.ORIGINAL);

			for (final Clazz clazz : component.getClazzes()) {
				clazz.getExtensionAttributes().put(STATUS, Status.ORIGINAL);
			}

			this.componentRecursive(component.getChildren());

		}
	}
}
