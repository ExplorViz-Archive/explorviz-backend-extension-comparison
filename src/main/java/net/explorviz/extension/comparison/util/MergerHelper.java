package net.explorviz.extension.comparison.util;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.extension.comparison.model.Status;
import net.explorviz.extension.comparison.repository.Merger;
import net.explorviz.extension.comparison.repository.PrepareForMerger;
import net.explorviz.model.application.AggregatedClazzCommunication;
import net.explorviz.model.application.Clazz;
import net.explorviz.model.application.ClazzCommunication;
import net.explorviz.model.application.Component;

/**
 * Provides helper methods for {@link Merger}.
 *
 * @author josw
 *
 */
public class MergerHelper {

	/**
	 * Set the status of the {@link Component}, child{@link Component}s and all
	 * {@link Clazz}es.
	 *
	 * @param component
	 * @param status
	 */
	public void setStatusComponentCLazzesAndChildren(final Component component, final Status status) {
		component.getExtensionAttributes().put(PrepareForMerger.STATUS, status);
		if (!component.getClazzes().isEmpty()) {
			for (final Clazz clazz : component.getClazzes()) {
				clazz.getExtensionAttributes().put(PrepareForMerger.STATUS, status);
			}
		}
		for (final Component child : component.getChildren()) {
			child.getExtensionAttributes().put(PrepareForMerger.STATUS, status);
			setStatusComponentCLazzesAndChildren(child, status);
			for (final Clazz clazz : child.getClazzes()) {
				clazz.getExtensionAttributes().put(PrepareForMerger.STATUS, status);
			}
		}
	}

	/**
	 * Breaks up the structure {@link Component} and child{@link Component}s.
	 * Reason: searching is easier on the resulting flat list of {@link Component}s.
	 *
	 * @param components
	 * @return
	 */
	public List<Component> createFlatComponents(final List<Component> components) {
		final List<Component> flatComponents = new ArrayList<>();

		for (final Component component : components) {
			flatComponents.add(component);
			// add children recursively
			addChildrenToList(component.getChildren(), flatComponents);
		}

		return flatComponents;
	}

	public void addChildrenToList(final List<Component> children, final List<Component> componentList) {
		if (!children.isEmpty()) {
			for (final Component component : children) {
				componentList.add(component);
				addChildrenToList(component.getChildren(), componentList);
			}
		}
	}

	/**
	 * Breaks up the structure of {@link Clazz}es (inside {@link Component}s).
	 * Reason: searching is easier on the resulting flat list of {@link Clazz}es.
	 *
	 * @param flatComponentsFrom1
	 * @return
	 */
	public List<Clazz> createFlatClazzes(final List<Component> flatComponentsFrom1) {
		final List<Clazz> flatClazzes = new ArrayList<>();
		for (final Component component : flatComponentsFrom1) {
			flatClazzes.addAll(component.getClazzes());
		}
		return flatClazzes;
	}

	/**
	 * Breaks up the structure of {@link AggregatedClazzCommunication} and
	 * {@link ClazzCommunication}. Reason: searching is easier on the resulting flat
	 * list of {@link ClazzCommunication}s.
	 *
	 * @param aggregatedCommus
	 * @return
	 */
	public List<ClazzCommunication> createFlatClazzCommunications(
			final List<AggregatedClazzCommunication> aggregatedCommus) {
		final List<ClazzCommunication> clazzCommus = new ArrayList<>();

		for (final AggregatedClazzCommunication aggregatedCommu : aggregatedCommus) {
			for (final ClazzCommunication clazzCommu : aggregatedCommu.getOutgoingClazzCommunications()) {
				clazzCommus.add(clazzCommu);
			}
		}
		return clazzCommus;
	}
}
