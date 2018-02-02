package net.explorviz.extension.comparison.main;

import javax.inject.Singleton;
import javax.ws.rs.ApplicationPath;

import org.glassfish.hk2.utilities.binding.AbstractBinder;
import org.glassfish.jersey.server.ResourceConfig;

import com.github.jasminb.jsonapi.ResourceConverter;

import net.explorviz.model.Clazz;
import net.explorviz.model.Communication;
import net.explorviz.model.CommunicationClazz;
import net.explorviz.model.Component;
import net.explorviz.model.DatabaseQuery;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.Timestamp;
import net.explorviz.model.helper.CommunicationAccumulator;
import net.explorviz.model.helper.CommunicationTileAccumulator;
import net.explorviz.server.injection.ResourceConverterFactory;
import net.explorviz.server.providers.GenericTypeFinder;
import net.explorviz.server.security.User;

@ApplicationPath("/extension/comparison")
public class Application extends ResourceConfig {

	public Application() {

		GenericTypeFinder.typeMap.putIfAbsent("Timestamp", Timestamp.class);
		GenericTypeFinder.typeMap.putIfAbsent("Landscape", Landscape.class);
		GenericTypeFinder.typeMap.putIfAbsent("System", net.explorviz.model.System.class);
		GenericTypeFinder.typeMap.putIfAbsent("NodeGroup", NodeGroup.class);
		GenericTypeFinder.typeMap.putIfAbsent("Node", Node.class);
		GenericTypeFinder.typeMap.putIfAbsent("Application", Application.class);
		GenericTypeFinder.typeMap.putIfAbsent("Component", Component.class);
		GenericTypeFinder.typeMap.putIfAbsent("Clazz", Clazz.class);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationClazz", CommunicationClazz.class);
		GenericTypeFinder.typeMap.putIfAbsent("Communication", Communication.class);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationAccumulator", CommunicationAccumulator.class);
		GenericTypeFinder.typeMap.putIfAbsent("CommunicationTileAccumulator", CommunicationTileAccumulator.class);
		GenericTypeFinder.typeMap.putIfAbsent("DatabaseQuery", DatabaseQuery.class);
		GenericTypeFinder.typeMap.putIfAbsent("User", User.class);

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
