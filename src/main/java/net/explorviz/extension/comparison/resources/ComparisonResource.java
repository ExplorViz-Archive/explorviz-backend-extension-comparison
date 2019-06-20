package net.explorviz.extension.comparison.resources;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.explorviz.extension.comparison.services.HistoryService;
import net.explorviz.extension.comparison.services.PersistenceService;
import net.explorviz.shared.landscape.model.landscape.Landscape;

@Path(value = "merged")
@PermitAll
public class ComparisonResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(ComparisonResource.class);
	
	@Inject
	private HistoryService historyService;
	
	@Inject
	private PersistenceService persistanceService;
	
	@GET
	public Landscape getMergedLandscape(@QueryParam("timestamps") final List<String> timestamps) {
		List<Landscape> landscapes = new ArrayList<>(timestamps.size());
		
		for(String timestamp : timestamps) {
			landscapes.add(persistanceService.retrieveLandscapeByTimestamp(Long.parseLong(timestamp)));
		}
		
		return null;
	}
}
