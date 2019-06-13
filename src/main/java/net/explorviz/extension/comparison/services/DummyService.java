package net.explorviz.extension.comparison.services;

import net.explorviz.shared.config.annotations.Config;

public class DummyService {

  @Config("dummy.hello")
  private String announcement;

  @Config("dummy.year")
  private int year;

  @Config("dummy.good")
  private boolean good;

  public void startMyDummyStuff() {
    // your potential work
  }

  public String getAnnouncement() {
    return announcement;
  }

  public int getYear() {
    return year;
  }

  public boolean isGood() {
    return good;
  }

}
