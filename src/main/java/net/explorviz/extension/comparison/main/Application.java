package net.explorviz.extension.comparison.main;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.github.jasminb.jsonapi.ResourceConverter;

import net.explorviz.api.ExtensionAPI;
import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.server.injection.ResourceConverterFactory;

@ApplicationPath("/extension/comparison")
public class Application extends ResourceConfig {

	private final ExtensionAPIImpl api = ExtensionAPI.get();

	public Application() {

		// register the models that you wan't to parse to JSONAPI-conform JSON,
		// i.e. exchange with frontend
		api.registerAllCoreModels();
		final ResourceConverterFactory factory = new ResourceConverterFactory();

		final AbstractBinder dependencyBinder = new ExtensionDependencyInjectionBinder();
		dependencyBinder.bindFactory(factory).to(ResourceConverter.class).in(Singleton.class);

		// register DI
		register(dependencyBinder);

		// Enable CORS
		register(CORSResponseFilter.class);

		// register all providers in the given package
		packages("net.explorviz.server.providers");

		// register all resources in the given package
		packages("net.explorviz.extension.comparison.resources");
	}
}
