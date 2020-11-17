package com.transport;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Identity {
  public String         id; //phoenix id
  public Set<String>    links;
  public String         upLink;
  public Set<Long>      profiles;
  public boolean        claimLinkReward;

  public static Identity ofDefault(String id) {
    Identity i          = new Identity();
    i.id                = id;
    i.links             = new HashSet<>();
    i.upLink            = "";
    i.profiles          = new HashSet<>();
    i.claimLinkReward   = false;
    return i;
  }
}