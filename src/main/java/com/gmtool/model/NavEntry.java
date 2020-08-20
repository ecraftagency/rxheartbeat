package com.gmtool.model;

@SuppressWarnings("unused")
public class NavEntry {
  public String name;
  public String href;
  public String cssClasses;
  public boolean active;

  public static NavEntry ofDefault(String name, String href) {
    NavEntry navEntry = new NavEntry();
    navEntry.href = href;
    navEntry.name = name;
    navEntry.cssClasses = "nav-item nav-link";
    navEntry.active = false;
    return navEntry;
  }

  public static NavEntry ofActive(String name) {
    NavEntry navEntry = new NavEntry();
    navEntry.name = name;
    navEntry.href = "#";
    navEntry.cssClasses = "nav-item nav-link active";
    navEntry.active = true;
    return navEntry;
  }

  public boolean isActive() {
    return active;
  }

  public String getCssClasses() {
    return cssClasses;
  }

  public String getName() {
    return name;
  }

  public String getHref() {
    return href;
  }
}