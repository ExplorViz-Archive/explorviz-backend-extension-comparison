package net.explorviz.extension.comparison.main;

import javax.servlet.annotation.WebListener;

import org.glassfish.jersey.server.monitoring.ApplicationEvent;
import org.glassfish.jersey.server.monitoring.ApplicationEvent.Type;
import org.glassfish.jersey.server.monitoring.ApplicationEventListener;
import org.glassfish.jersey.server.monitoring.RequestEvent;
import org.glassfish.jersey.server.monitoring.RequestEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.extension.comparison.model.BaseModel;
import net.explorviz.shared.common.idgen.AtomicEntityIdGenerator;
import net.explorviz.shared.common.idgen.IdGenerator;
import net.explorviz.shared.common.idgen.UuidServiceIdGenerator;
import net.explorviz.shared.config.annotations.Config;

/**
 * Primary starting class - executed, when the servlet context is started.
 */
@WebListener
public class SetupApplicationListener implements ApplicationEventListener {

  private static final Logger LOGGER = LoggerFactory.getLogger(SetupApplicationListener.class);
  
  @Config("service.prefix")
  String prefix;

  @Override
  public void onEvent(final ApplicationEvent event) {

    // After this type, CDI (e.g. injected LandscapeExchangeService) has been
    // fullfilled
    final Type t = Type.INITIALIZATION_FINISHED;


    if (event.getType().equals(t)) {
      startExtension();
    }

  }

  @Override
  public RequestEventListener onRequest(final RequestEvent requestEvent) {
    return null;
  }

  private void startExtension() {
    LOGGER.info("* * * * * * * * * * * * * * * * * * *\n");
    LOGGER.info("Comparison Extension Servlet initialized.\n");
    LOGGER.info("* * * * * * * * * * * * * * * * * * *");

    BaseModel.initialize(new IdGenerator(new UuidServiceIdGenerator(), new AtomicEntityIdGenerator(), prefix));
  }
}