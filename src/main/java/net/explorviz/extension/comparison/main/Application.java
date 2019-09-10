package net.explorviz.extension.comparison.main;

import net.explorviz.extension.comparison.model.CommunicationHistory;
import net.explorviz.extension.comparison.model.History;
import net.explorviz.extension.comparison.resources.ComparisonResource;
import net.explorviz.extension.comparison.resources.HistoryResource;
import net.explorviz.extension.comparison.services.HistoryService;
import net.explorviz.extension.comparison.services.LandscapeRetrievalService;
import net.explorviz.extension.comparison.services.PersistenceService;
import net.explorviz.shared.common.provider.GenericTypeFinder;
import net.explorviz.shared.common.provider.JsonApiListProvider;
import net.explorviz.shared.common.provider.JsonApiProvider;
import net.explorviz.shared.landscape.model.helper.TypeProvider;
import net.explorviz.shared.security.filters.AuthenticationFilter;
import net.explorviz.shared.security.filters.AuthorizationFilter;
import net.explorviz.shared.security.filters.CorsResponseFilter;
import net.explorviz.shared.security.model.User;

import org.glassfish.jersey.server.ResourceConfig;

public class Application extends ResourceConfig {

  public Application() {
    GenericTypeFinder.getTypeMap().put("History", History.class);
    GenericTypeFinder.getTypeMap().put("User", User.class);
    GenericTypeFinder.getTypeMap().put("CommunicationHistory", CommunicationHistory.class);

    // register Landscape Model classes, since we want to use them
    TypeProvider.getExplorVizCoreTypesAsMap().forEach((classname, classRef) -> {
      GenericTypeFinder.getTypeMap().put(classname, classRef);
    });

    // register DI
    register(new DependencyInjectionBinder());

    // Security
    register(AuthenticationFilter.class);
    register(AuthorizationFilter.class);
    register(CorsResponseFilter.class);

    // register providers
    register(JsonApiProvider.class);
    register(JsonApiListProvider.class);

    // Starting point for your DI-based extension
    register(SetupApplicationListener.class);
    
    register(HistoryService.class);
    register(ComparisonResource.class);
    register(PersistenceService.class);
    register(HistoryResource.class);
    
    register(LandscapeRetrievalService.class);
  }
}
