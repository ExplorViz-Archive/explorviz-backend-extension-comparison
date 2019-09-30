package net.explorviz.extension.comparison.main;

import javax.inject.Singleton;

import net.explorviz.extension.comparison.services.HistoryService;
import net.explorviz.extension.comparison.services.LandscapeRetrievalService;
import net.explorviz.extension.comparison.services.LandscapeSerializationHelper;
import net.explorviz.extension.comparison.services.MergeService;
import net.explorviz.shared.common.injection.CommonDependencyInjectionBinder;

/**
 * The DependencyInjectionBinder is used to register Contexts and Dependency
 * Injection (CDI) aspects for this application.
 */
public class DependencyInjectionBinder extends CommonDependencyInjectionBinder {

	@Override
	public void configure() {

		// Common DI
		super.configure();

		// Service-specific DI
		
		this.bind(HistoryService.class).to(HistoryService.class).in(Singleton.class);

		this.bind(LandscapeSerializationHelper.class).to(LandscapeSerializationHelper.class).in(Singleton.class);
		
		this.bind(MergeService.class).to(MergeService.class).in(Singleton.class);
		
		this.bind(LandscapeRetrievalService.class).to(LandscapeRetrievalService.class).in(Singleton.class);
	}
}
