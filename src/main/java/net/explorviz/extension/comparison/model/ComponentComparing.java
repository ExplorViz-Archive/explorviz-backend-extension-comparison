package net.explorviz.extension.comparison.model;

import net.explorviz.model.Component;

public class ComponentComparing extends Component {

	Status status = Status.ORIGINAL;

	public Status getStatus() {
		return this.status;
	}

	public void setStatus(final Status input) {
		this.status = input;
	}

}
