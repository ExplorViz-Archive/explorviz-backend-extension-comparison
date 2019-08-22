package net.explorviz.extension.comparison.resources;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.extension.comparison.services.MergeService;
import net.explorviz.extension.comparison.services.PersistenceService;
import net.explorviz.shared.landscape.model.landscape.Landscape;

@Path(value = "merged-landscapes")
@PermitAll
public class ComparisonResource {

	private static final Logger LOGGER = LoggerFactory.getLogger(ComparisonResource.class);

	@Inject
	private MergeService mergeService;

	@Inject
	private PersistenceService persistanceService;

	@GET
	public Landscape getMergedLandscape(@QueryParam("timestamp1") final long timestamp1,
			@QueryParam("timestamp2") final long timestamp2) {
		
		return mergeService.mergeLandscapes(persistanceService.retrieveLandscapeByTimestamp(timestamp1),
				persistanceService.retrieveLandscapeByTimestamp(timestamp2));
	}
}
