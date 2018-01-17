package net.exlorviz.extension.comparison.repository;

import java.util.ArrayList;
import java.util.List;

import net.explorviz.api.ExtensionAPIImpl;
import net.explorviz.model.Application;
import net.explorviz.model.Landscape;
import net.explorviz.model.Node;
import net.explorviz.model.NodeGroup;
import net.explorviz.model.Timestamp;

public class LandscapeFetchService {

	private final ExtensionAPIImpl extensionApi = ExtensionAPIImpl.getInstance();
	private List<Timestamp> allTimestamps;

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
		// TODO if you have found the same application in both versions than do appMerge
		for (final net.explorviz.model.System sys : secondLandscape.getSystems()) {
			for (final NodeGroup nodegroup : sys.getNodeGroups()) {
				for (final Node node : nodegroup.getNodes()) {
					for (final Application app : node.getApplications()) {
						// TODO does firstLandscape contains app? If so, merge these two apps with
						// appMerge()
						final String app2Name = app.getName();
					}
				}

			}
		}
		// TODO
		return firstLandscape;
	}

}
