package net.explorviz.extension.comparison.model;

import com.github.jasminb.jsonapi.annotations.Type;

@Type("sub-dummy")
public class SubDummyModel extends BaseModel {

  private int year;
  private String value;
  private boolean precise;

  public SubDummyModel() {
    // default constructor for JSON API parsing
  }

  public int getYear() {
    return year;
  }

  public void setYear(int year) {
    this.year = year;
  }

  public String getValue() {
    return value;
  }

  public void setValue(String value) {
    this.value = value;
  }

  public boolean isPrecise() {
    return precise;
  }

  public void setPrecise(boolean precise) {
    this.precise = precise;
  }


}
