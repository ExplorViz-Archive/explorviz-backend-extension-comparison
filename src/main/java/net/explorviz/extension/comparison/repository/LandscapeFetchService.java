package net.explorviz.extension.comparison.repository;

import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.application.Application;
import net.explorviz.model.landscape.Landscape;
import net.explorviz.model.landscape.Node;
import net.explorviz.model.landscape.NodeGroup;
import net.explorviz.server.main.Configuration;

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

	public Landscape fetchLandscapeForComparison(final long timestamp) {
		return extensionApi.getLandscape(timestamp, Configuration.REPLAY_REPOSITORY);
	}

	// Right now this works for two landscapes, this can be extended to more than
	// two landscapes in the future
	public Landscape fetchMergedLandscape(final Landscape firstLandscape, final Landscape secondLandscape) {
		final Landscape mergedLandscape = secondLandscape;

		for (final net.explorviz.model.landscape.System sys : mergedLandscape.getSystems()) {
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

		for (final net.explorviz.model.landscape.System sys : landscape.getSystems()) {
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
