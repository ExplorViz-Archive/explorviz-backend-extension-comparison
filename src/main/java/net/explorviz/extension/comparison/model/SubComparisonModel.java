package net.explorviz.extension.comparison.model;

import com.github.jasminb.jsonapi.annotations.Type;

@Type("sub-dummy")
public class SubComparisonModel extends BaseModel {

	private long value;

	public SubComparisonModel() {
		// default constructor for JSON API parsing
	}

	public SubComparisonModel(final long value) {
		this.value = value;
	}

	public void setSubDummyValue(final long value) {
		this.value = value;
	}

	public long getSubDummyValue() {
		return value;
	}

}
