package net.explorviz.extension.comparison.main;

import javax.inject.Singleton;

import net.exlorviz.extension.comparison.repository.LandscapeFetchService;

public class ExtensionDependencyInjectionBinder extends net.explorviz.server.main.DependencyInjectionBinder {

	@Override
	public void configure() {
		// call core DI, so we can use it
		// in this extension if necessary
		super.configure();

		// bind your possible injections here
		// this.bind(X.class).to(X.class).in(Singleton.class)
		this.bind(LandscapeFetchService.class).to(LandscapeFetchService.class).in(Singleton.class);
	}
}
