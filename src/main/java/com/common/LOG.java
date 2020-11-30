package com.common;

import io.vertx.core.json.JsonObject;
import org.fluentd.logger.FluentLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.common.Constant.*;

@SuppressWarnings("unused")
public class LOG {
  private static final Logger GLOBAL_EXCEPTION  = LoggerFactory.getLogger("global_exception");
  private static final Logger AUTH_EXCEPTION    = LoggerFactory.getLogger("auth_exception");
  private static final Logger POOL_EXCEPTION    = LoggerFactory.getLogger("pool_exception");
  private static final Logger PAYMENT_EXCEPTION = LoggerFactory.getLogger("payment_exception");
  private static FluentLogger FLUENT            = FluentLogger.getLogger("sbiz");

  private static final String EXCEPTION_LINE_HEADER = "\n\t";

  public static void globalException(String source, String action, Throwable cause) {
    if(SYSTEM_INFO.USE_GLOBAL_FILE_LOG) {
      StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
      for (StackTraceElement ste : cause.getStackTrace())
        builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
      GLOBAL_EXCEPTION.info(builder.toString());
    }

    Map<String, Object> data = new HashMap<>();
    List<String> trace       = Arrays.stream(cause.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
    trace.add(0, cause.getMessage());
    data.put("type", "global_exception");
    data.put("msg", trace);
    data.put("source", source);
    data.put("action", action);
    FLUENT.log("exception", data);
  }

  public static void globalException(String source, String action, Object ... params) {
    StringBuilder logContent = GlobalVariable.stringBuilder.get().append(';');

    if(SYSTEM_INFO.USE_GLOBAL_FILE_LOG) {
      if (params != null && params.length > 0) {
        for (Object param : params)
          logContent.append(param).append(EXCEPTION_LINE_HEADER);
        GLOBAL_EXCEPTION.info(logContent.toString());
      }
    }

    Map<String, Object> data = new HashMap<>();
    data.put("type", "global_exception");
    data.put("msg", logContent.toString());
    data.put("source", source);
    data.put("action", action);
    FLUENT.log("exception", data);
  }

  public static void console(Throwable cause) {
    if(SYSTEM_INFO.USE_CONSOLE_LOG)
      cause.printStackTrace();
  }

  public static void console(Object ... params) {
    if(SYSTEM_INFO.USE_CONSOLE_LOG) {
      if (params != null && params.length > 0) {
        StringBuilder logContent = GlobalVariable.stringBuilder.get();
        for (Object param : params)
          logContent.append(param).append(EXCEPTION_LINE_HEADER);
        System.out.println(logContent);
      }
      else {
        globalException("","",new Throwable("ScribeReporter.console(...) with no param!!!"));
      }
    }
  }

  public static void authException(Object ... params) {
    if (params != null && params.length > 0) {
      StringBuilder logContent = GlobalVariable.stringBuilder.get();
      for (Object param : params)
        logContent.append(param).append(EXCEPTION_LINE_HEADER);
      AUTH_EXCEPTION.info(logContent.toString());

      //fluent LOG
      Map<String, Object> data = new HashMap<>();
      data.put("type", "auth_exception");
      data.put("msg", logContent.toString());
      FLUENT.log("exception", data);
    }
    else {
      globalException("", "", new Throwable("ScribeReporter.writePaymentException(...) with no param!!!"));
    }
  }

  public static void authException(Throwable cause) {
    StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
    for (StackTraceElement ste : cause.getStackTrace())
      builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
    AUTH_EXCEPTION.info(builder.toString());

    //fluent LOG
    Map<String, Object> data = new HashMap<>();
    List<String> trace       = Arrays.stream(cause.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
    trace.add(0, cause.getMessage());
    data.put("type", "auth_exception");
    data.put("msg", trace);
    FLUENT.log("exception", data);
  }

  public static void poolException(Throwable cause) {
    if(SYSTEM_INFO.USE_POOL_LOG) {
      StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
      for (StackTraceElement ste : cause.getStackTrace())
        builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
      POOL_EXCEPTION.info(builder.toString());
    }

    //fluent LOG
    Map<String, Object> data = new HashMap<>();
    List<String> trace       = Arrays.stream(cause.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
    trace.add(0, cause.getMessage());
    data.put("type", "pool_exception");
    data.put("msg", trace);
    FLUENT.log("exception", data);
  }

  public static void poolException(Object ... params) {
    if(SYSTEM_INFO.USE_POOL_LOG) {
      if (params != null && params.length > 0) {
        StringBuilder logContent = GlobalVariable.stringBuilder.get().append(';');
        for (Object param : params)
          logContent.append(param).append(EXCEPTION_LINE_HEADER);
        POOL_EXCEPTION.info(logContent.toString());

        //fluent LOG
        Map<String, Object> data = new HashMap<>();
        data.put("type", "pool_exception");
        data.put("msg", logContent.toString());
        FLUENT.log("exception", data);
      }
      else {
        globalException("", "", new Throwable("ScribeReporter.writeGlobalExceptionLog(...) with no param!!!"));
      }
    }
  }

  public static void paymentException(String source, String action, Throwable cause) {
    if(SYSTEM_INFO.USE_PAYMENT_LOG) {
      StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
      for (StackTraceElement ste : cause.getStackTrace())
        builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
      PAYMENT_EXCEPTION.info(builder.toString());

      //fluent LOG
      Map<String, Object> data = new HashMap<>();
      List<String> trace       = Arrays.stream(cause.getStackTrace()).map(StackTraceElement::toString).collect(Collectors.toList());
      trace.add(0, cause.getMessage());
      data.put("type", "payment_exception");
      data.put("msg", trace);
      data.put("source", source);
      data.put("action", action);
      FLUENT.log("exception", data);
    }
  }

  public static void paymentException(String source, String action, Object ... params) {
    if(SYSTEM_INFO.USE_PAYMENT_LOG) {
      if (params != null && params.length > 0) {
        StringBuilder logContent = GlobalVariable.stringBuilder.get().append(';');
        for (Object param : params)
          logContent.append(param).append(EXCEPTION_LINE_HEADER);
        PAYMENT_EXCEPTION.info(logContent.toString());

        //fluent LOG
        Map<String, Object> data = new HashMap<>();

        data.put("type", "payment_exception");
        data.put("msg", logContent.toString());
        data.put("source", source);
        data.put("action", action);
        FLUENT.log("exception", data);
      }
      else {
        globalException("", "", new Throwable("ScribeReporter.writeGlobalExceptionLog(...) with no param!!!"));
      }
    }
  }

  public static void info(String source, String action, JsonObject param) {
    //fluent LOG
    Map<String, Object> data = new HashMap<>();
    data.put("type", "info");
    data.put("msg", param);
    data.put("source", source);
    data.put("action", action);
    FLUENT.log("info", data);
  }

  public static void info(String source, String action, String info) {
    //fluent LOG
    Map<String, Object> data = new HashMap<>();
    data.put("type", "info");
    data.put("msg", info);
    data.put("source", source);
    data.put("action", action);
    FLUENT.log("info", data);
  }
}