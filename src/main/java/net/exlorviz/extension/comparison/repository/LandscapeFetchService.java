package net.exlorviz.extension.comparison.repository;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.Application;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.Timestamp;

/**
 *
 * @author josw
 *
 */
public class LandscapeFetchService {

	private static LandscapeFetchService instance;

	private final ExtensionAPIImpl extensionApi = ExtensionAPIImpl.getInstance();
	private final Merger appMerger = new Merger();

	public static synchronized LandscapeFetchService getInstance() {
		if (LandscapeFetchService.instance == null) {
			LandscapeFetchService.instance = new LandscapeFetchService();
		}
		return LandscapeFetchService.instance;
	}

	// Right now this works for two landscapes, this can be extended to more than
	// two landscapes in the future

	public List<Timestamp> filterTwoTimestampsForComparison() {
		// Get all timestamps in the repository, because parameter is 0.
		final List<Timestamp> allTimestamps = extensionApi.getNewestTimestamps(0);
		final List<Timestamp> filteredTimestamps = new ArrayList<Timestamp>();

		filteredTimestamps.add(allTimestamps.get(0));
		filteredTimestamps.add(allTimestamps.get(1));

		return filteredTimestamps;
	}

	public Landscape fetchLandscapeForComparison(final Timestamp timestamp) {
		return extensionApi.getLandscape(timestamp.getId());
	}

	public Landscape fetchMergedLandscape(final Landscape firstLandscape, final Landscape secondLandscape) {
		final Landscape mergedLandscape = secondLandscape;

		for (final net.explorviz.model.System sys : mergedLandscape.getSystems()) {
			for (final NodeGroup nodegroup : sys.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application app2 : node.getApplications()) {
						final String app2Name = app2.getName();

						final Application app1 = appContained(firstLandscape, app2Name);
						if (app1 == null) {
							System.out.printf(
									"You can not compare two complete different applications. The application %s is not contained in the other landscape.\n",
									app2Name);
						} else {
							appMerger.appMerge(app1, app2);
						}
					}
				}

			}
		}
		return mergedLandscape;
	}

	private Application appContained(final Landscape landscape, final String appName) {
		Application app = new Application();

		for (final net.explorviz.model.System sys : landscape.getSystems()) {
			for (final NodeGroup nodegroup : sys.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application checkApp : node.getApplications()) {
						if (checkApp.getName().equals(appName)) {
							app = checkApp;
						} else {
							app = null;
						}
					}
				}

			}
		}

		return app;
	}

}
