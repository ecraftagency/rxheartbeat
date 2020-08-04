package com.heartbeat.service;

public interface ConstantInjector {
  void inject(String path, String value) throws Exception;
}