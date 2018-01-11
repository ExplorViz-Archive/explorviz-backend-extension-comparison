package net.explorviz.extension.comparison.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import de.svenjacobs.loremipsum.LoremIpsum;
import net.explorviz.extension.comparison.model.ComparisonModel;
import net.explorviz.extension.comparison.model.SubComparisonModel;

// @Secured
// Add the "Secured" annotation to enable authentication

@Path("/test")
public class TestResource {

	@GET
	@Path("/get")
	@Produces(MediaType.APPLICATION_JSON)
	public ComparisonModel getModel() {
		final SubComparisonModel subDummy = new SubComparisonModel(10);
		return new ComparisonModel("myDummy", subDummy);
	}

	@GET
	@Path("/show")
	public String show() {
		// Note the dependency in the respective build.gradle
		final LoremIpsum loremIpsum = new LoremIpsum();

		return "Hi from dummy extension: " + loremIpsum.getParagraphs(1);
	}
}