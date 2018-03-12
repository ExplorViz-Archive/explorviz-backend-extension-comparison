package net.explorviz.extension.comparison.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.exlorviz.extension.comparison.repository.LandscapeFetchService;
import net.explorviz.model.Landscape;
import net.explorviz.model.Timestamp;
import net.explorviz.server.security.Secured;

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
	@Path("/firstCompare")
	public Landscape getBeforeLandscape() {
		final List<Timestamp> filteredTimestamps = service.filterTwoTimestampsForComparison();
		final Landscape firstLandscape = service.fetchLandscapeForComparison(filteredTimestamps.get(0));
		// TODO this is just the example landscape used for developing
		// final Landscape firstLandscape =
		// LandscapeExampleCreator.createSimpleLandscapeVersion1();
		return firstLandscape;

	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/secondCompare")
	public Landscape getAfterLandscape() {
		final List<Timestamp> filteredTimestamps = service.filterTwoTimestampsForComparison();
		final Landscape secondLandscape = service.fetchLandscapeForComparison(filteredTimestamps.get(1));
		// TODO this is just the example landscape used for developing
		// final Landscape secondLandscape =
		// LandscapeExampleCreator.createSimpleLandscapeVersion2();
		return secondLandscape;

	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/merged")
	public Landscape getMergedLandscape() {
		final List<Timestamp> filteredTimestamps = service.filterTwoTimestampsForComparison();
		final Landscape firstLandscape = service.fetchLandscapeForComparison(filteredTimestamps.get(0));
		final Landscape secondLandscape = service.fetchLandscapeForComparison(filteredTimestamps.get(1));
		// TODO this is just the example landscape used for developing
		// final Landscape firstLandscape =
		// LandscapeExampleCreator.createSimpleLandscapeVersion1();
		// final Landscape secondLandscape =
		// LandscapeExampleCreator.createSimpleLandscapeVersion2();
		return service.fetchMergedLandscape(firstLandscape, secondLandscape);

	}

}