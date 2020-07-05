package com.heartbeat.service.impl;

import com.heartbeat.common.Utilities;
import com.heartbeat.model.Session;
import com.heartbeat.service.SessionInjector;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class SimpleInjector implements SessionInjector {
  @Override
  public void inject(Session session, String path, String v) throws Exception {
    List<String> tokens   = Arrays.asList(path.split("\\."));
    Iterator<String> it   = tokens.iterator();

    Field     field       = Session.class.getField(it.next());
    Object    target      = field.get(session);
    Object    value;

    while (it.hasNext()) {
      field = field.getType().getField(it.next());
    }

    value = field.getType().equals(String.class) ? v : Utilities.gson.fromJson(v, field.getGenericType());
    field.set(target, value);
  }
}