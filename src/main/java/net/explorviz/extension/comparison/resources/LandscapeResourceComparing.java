package net.explorviz.extension.comparison.resources;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import net.explorviz.extension.comparison.repository.LandscapeFetchService;
import net.explorviz.model.application.Application;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.server.security.Secured;

/**
 * REST resource that provides a {@link Landscape} with merged
 * {@link Application}s to the frontend.
 * 
 * @author josw
 *
 */
@Secured
@Path("/landscape")
public class LandscapeResourceComparing {

	private final LandscapeFetchService service;

	@Inject
	public LandscapeResourceComparing(final LandscapeFetchService service) {
		this.service = service;
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/merged/{timestamps}")
	public Landscape getMergedLandscape(@PathParam("timestamps") final String timestamps) {
		final String[] timestampsString = timestamps.split(",");

		final Landscape firstLandscape = service.fetchLandscapeForComparison(Long.parseLong(timestampsString[0]));
		final Landscape secondLandscape = service.fetchLandscapeForComparison(Long.parseLong(timestampsString[1]));

		return service.fetchMergedLandscape(firstLandscape, secondLandscape);
	}

}