package net.explorviz.extension.comparison.model;

import java.io.Serializable;

import net.explorviz.extension.comparison.repository.Merger;

/**
 * This flag is attached to each entity of the data model. It shows whether an
 * entity is modified. This information is needed for building the merged
 * application object {@link Merger}.
 *
 * @author josw
 *
 */
public enum Status implements Serializable {
	ORIGINAL, ADDED, EDITED, DELETED;
}
