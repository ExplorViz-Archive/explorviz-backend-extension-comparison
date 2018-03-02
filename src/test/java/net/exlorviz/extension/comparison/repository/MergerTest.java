package net.exlorviz.extension.comparison.repository;

import org.junit.Before;

import net.explorviz.model.Application;
import net.explorviz.model.Node;

public class MergerTest {
	private final Merger merger = new Merger();

	private Application application1 = new Application();
	private Application application2 = new Application();
	protected Application mergedApplication = new Application();
	private final Node dummyNode = new Node();

	/**
	 * For more information on how the two {@link Application}s differ, take a look
	 * at {@link LandscapeExampleCreator#createSimpleApplicationVersion1(Node)} and
	 * {@link LandscapeExampleCreator#createSimpleApplicationVersion2(Node)}.
	 */
	@Before
	public void setUpApps() {
		dummyNode.setName("dummyNode");
		application1 = LandscapeExampleCreator.createSimpleApplicationVersion1(dummyNode);
		application2 = LandscapeExampleCreator.createSimpleApplicationVersion2(dummyNode);
		mergedApplication = application2;
		mergedApplication = merger.appMerge(application1, application2);

	}
}
