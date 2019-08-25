package net.explorviz.extension.comparison.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import net.explorviz.extension.comparison.model.History;
import net.explorviz.extension.comparison.services.HistoryService;
import net.explorviz.extension.comparison.services.LandscapeRetrievalService;
import net.explorviz.shared.landscape.model.landscape.Landscape;

@Path(value = "histories")
@PermitAll
public class HistoryResource {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(HistoryResource.class);
	
	@Inject
	private HistoryService historyService;
	
	@Inject
	private LandscapeRetrievalService landscapeRetrievalService;
	
	@GET
	public History getMergedLandscape(@QueryParam("timestamps") final List<Long> timestamps) throws IOException, DocumentSerializationException {
		List<Landscape> landscapes = new ArrayList<>(timestamps.size());
		
		for(Long timestamp : timestamps) {
			landscapes.add(landscapeRetrievalService.retrieveLandscapeByTimestamp(timestamp));
		}
		
		return historyService.computeHistory(landscapes);
	}
}