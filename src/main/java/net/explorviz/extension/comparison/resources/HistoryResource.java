package net.explorviz.extension.comparison.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import com.github.jasminb.jsonapi.exceptions.DocumentSerializationException;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.tags.Tag;
import net.explorviz.extension.comparison.model.History;
import net.explorviz.extension.comparison.services.HistoryService;
import net.explorviz.extension.comparison.services.LandscapeRetrievalService;
import net.explorviz.shared.landscape.model.landscape.Landscape;

/**
 * Resource that provides the history of multiple landscapes to the frontend.
 */
@Path(value = "histories")
@RolesAllowed({ "admin", "user" })
@Tag(name = "History")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer", bearerFormat = "JWT")
@SecurityRequirement(name = "token")
public class HistoryResource {

	@Inject
	private HistoryService historyService;

	@Inject
	private LandscapeRetrievalService landscapeRetrievalService;

	/**
	 * Endpoint for the history calculation.
	 * 
	 * @param timestamps the landscapes for the history calculation
	 * @return the computed history
	 * @throws IOException
	 * @throws DocumentSerializationException
	 */
	@GET
	public History getHistory(@QueryParam("timestamps[]") final List<Long> timestamps)
			throws IOException, DocumentSerializationException {
		List<Landscape> landscapes = new ArrayList<>(timestamps.size());

		for (Long timestamp : timestamps) {
			landscapes.add(landscapeRetrievalService.retrieveLandscapeByTimestamp(timestamp));
		}

		return historyService.computeHistory(landscapes);
	}
}