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

		api.registerAllCoreModels();

		// GenericTypeFinder.typeMap.putIfAbsent("Timestamp", Timestamp.class);
		// GenericTypeFinder.typeMap.putIfAbsent("Landscape", Landscape.class);
		// GenericTypeFinder.typeMap.putIfAbsent("System",
		// net.explorviz.model.landscape.System.class);
		// GenericTypeFinder.typeMap.putIfAbsent("NodeGroup", NodeGroup.class);
		// GenericTypeFinder.typeMap.putIfAbsent("Node", Node.class);
		// GenericTypeFinder.typeMap.putIfAbsent("Application", Application.class);
		// GenericTypeFinder.typeMap.putIfAbsent("Component", Component.class);
		// GenericTypeFinder.typeMap.putIfAbsent("Clazz", Clazz.class);
		// GenericTypeFinder.typeMap.putIfAbsent("ClazzCommunication",
		// ClazzCommunication.class);
		// GenericTypeFinder.typeMap.putIfAbsent("ApplicationCommunication",
		// ApplicationCommunication.class);
		// GenericTypeFinder.typeMap.putIfAbsent("AggregatedClazzCommunication",
		// AggregatedClazzCommunication.class);
		// GenericTypeFinder.typeMap.putIfAbsent("CumulatedClazzCommunication",
		// CumulatedClazzCommunication.class);
		// // GenericTypeFinder.typeMap.putIfAbsent("CommunicationAccumulator",
		// // CommunicationAccumulator.class);
		// // GenericTypeFinder.typeMap.putIfAbsent("CommunicationTileAccumulator",
		// // CommunicationTileAccumulator.class);
		// GenericTypeFinder.typeMap.putIfAbsent("DatabaseQuery", DatabaseQuery.class);
		// GenericTypeFinder.typeMap.putIfAbsent("User", User.class);

		// register the models that you wan't to parse to JSONAPI-conform JSON,
		// i.e. exchange with frontend
		final ResourceConverterFactory factory = new ResourceConverterFactory();
		// factory.registerClass(ComparisonModel.class);
		// factory.registerClass(SubComparisonModel.class);

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
