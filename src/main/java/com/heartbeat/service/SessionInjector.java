package com.heartbeat.service;

import com.heartbeat.model.Session;

@FunctionalInterface
public interface SessionInjector {
  void inject(Session session, String path, String value) throws Exception;
}