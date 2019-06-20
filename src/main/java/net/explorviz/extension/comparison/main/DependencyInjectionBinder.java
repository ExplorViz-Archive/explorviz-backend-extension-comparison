package net.explorviz.extension.comparison.main;

import javax.inject.Singleton;
import net.explorviz.extension.comparison.services.HistoryService;
import net.explorviz.extension.comparison.services.KafkaLandscapeExchangeService;
import net.explorviz.extension.comparison.services.LandscapeSerializationHelper;
import net.explorviz.extension.comparison.services.PersistenceService;
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

		this.bind(KafkaLandscapeExchangeService.class).to(KafkaLandscapeExchangeService.class).in(Singleton.class);

		this.bind(LandscapeSerializationHelper.class).to(LandscapeSerializationHelper.class).in(Singleton.class);

		this.bind(PersistenceService.class).to(PersistenceService.class).in(Singleton.class);

	}
}