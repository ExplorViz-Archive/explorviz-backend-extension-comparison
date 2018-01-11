package net.explorviz.extension.comparison.model;

import com.github.jasminb.jsonapi.annotations.Relationship;
import com.github.jasminb.jsonapi.annotations.Type;

@Type("dummy")
public class ComparisonModel extends BaseModel {

	private String dummyName;

	@Relationship("sub-dummy")
	private SubComparisonModel subDummy;

	public ComparisonModel() {
		// default constructor for JSON API parsing
	}

	public ComparisonModel(final String dummyName, final SubComparisonModel subDummy) {
		this.dummyName = dummyName;
		this.subDummy = subDummy;
	}

	public String getDummyName() {
		return dummyName;
	}

	public void setDummyName(final String dummyName) {
		this.dummyName = dummyName;
	}

	public SubComparisonModel getSubDummy() {
		return subDummy;
	}

	public void setSubDummy(final SubComparisonModel subDummy) {
		this.subDummy = subDummy;
	}

}
