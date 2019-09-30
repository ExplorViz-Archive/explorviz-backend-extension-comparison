package net.explorviz.extension.comparison.resources;

import java.io.IOException;

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
import net.explorviz.extension.comparison.services.LandscapeRetrievalService;
import net.explorviz.extension.comparison.services.MergeService;
import net.explorviz.shared.landscape.model.landscape.Landscape;

/**
 * Resource that provides the comparison of two landscapes to the frontend
 *
 */
@Path(value = "merged-landscapes")
@RolesAllowed({"admin", "user"})
@Tag(name="Comparison")
@SecurityScheme(type = SecuritySchemeType.HTTP, name = "token", scheme = "bearer",
    bearerFormat = "JWT")
@SecurityRequirement(name = "token")
public class ComparisonResource {

	@Inject
	private MergeService mergeService;

	@Inject
	private LandscapeRetrievalService landscapeRetrievalService;

	/**
	 * Endpoint for the comparison and merging of two landscapes.
	 * 
	 * @param timestamp1 the first version
	 * @param timestamp2 the second version
	 * @return the merged landscape
	 * @throws IOException
	 * @throws DocumentSerializationException
	 */
	@GET
	public Landscape getMergedLandscape(@QueryParam("timestamp1") final long timestamp1,
			@QueryParam("timestamp2") final long timestamp2) throws IOException, DocumentSerializationException {

		return mergeService.mergeLandscapes(landscapeRetrievalService.retrieveLandscapeByTimestamp(timestamp1),
				landscapeRetrievalService.retrieveLandscapeByTimestamp(timestamp2));
	}
}
