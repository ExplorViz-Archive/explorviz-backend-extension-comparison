package net.explorviz.extension.comparison.resources;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import net.exlorviz.extension.comparison.repository.LandscapeFetchService;
import net.explorviz.model.Landscape;
import net.explorviz.model.Timestamp;

// @Secured
// Add the "Secured" annotation to enable authentication

@Path("/timestampComparing")
public class TimestampResourceComparing {

	private final LandscapeFetchService service;

	@Inject
	public TimestampResourceComparing(final LandscapeFetchService service) {
		this.service = service;
	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/landscape/firstCompare")
	public Landscape getBeforeLandscape() {
		final List<Timestamp> filteredTimestamps = service.filterTwoTimestampsForComparison();
		final Landscape firstLandscapeForComparison = service.fetchLandscapeForComparison(filteredTimestamps.get(0));
		return firstLandscapeForComparison;

	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/landscape/secondCompare")
	public Landscape getAfterLandscape() {
		final List<Timestamp> filteredTimestamps = service.filterTwoTimestampsForComparison();
		final Landscape secondLandscapeForComparison = service.fetchLandscapeForComparison(filteredTimestamps.get(1));
		return secondLandscapeForComparison;

	}

	@Produces("application/vnd.api+json")
	@GET
	@Path("/landscape/merged")
	public Landscape getMergedLandscape() {
		final List<Timestamp> filteredTimestamps = service.filterTwoTimestampsForComparison();
		final Landscape firstLandscape = service.fetchLandscapeForComparison(filteredTimestamps.get(0));
		final Landscape secondLandscape = service.fetchLandscapeForComparison(filteredTimestamps.get(1));
		return service.fetchMergedLandscape(firstLandscape, secondLandscape);

	}
}