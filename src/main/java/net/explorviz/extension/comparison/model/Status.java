package net.explorviz.extension.comparison.model;

import java.io.Serializable;

import net.explorviz.extension.comparison.repository.Merger;

/**
 * This flag is attached to each entity of the meta-model. It shows whether an
 * entity is modified. This information is needed for building the
 * comparing-application object {@link Merger}.(static view) Plus it holds the
 * difference of class instances and communication requests between the two
 * models.(dynamic view)
 *
 * @author josw
 *
 */
public enum Status implements Serializable {
	ORIGINAL, ADDED, EDITED, DELETED;
}
