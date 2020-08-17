package com.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.common.Constant.*;

@SuppressWarnings("unused")
public class LOG {
  private static final Logger GLOBAL_EXCEPTION  = LoggerFactory.getLogger("global_exception");
  private static final Logger AUTH_EXCEPTION    = LoggerFactory.getLogger("auth_exception");
  private static final Logger POOL_EXCEPTION    = LoggerFactory.getLogger("auth_exception");

  private static final String EXCEPTION_LINE_HEADER = "\n\t";

  public static void globalException(Throwable cause) {
    if(SYSTEM_INFO.USE_GLOBAL_FILE_LOG) {
      StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
      for (StackTraceElement ste : cause.getStackTrace())
        builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
      GLOBAL_EXCEPTION.info(builder.toString());
    }
  }

  public static void globalException(Object ... params) {
    if(SYSTEM_INFO.USE_GLOBAL_FILE_LOG) {
      if (params != null && params.length > 0) {
        StringBuilder logContent = GlobalVariable.stringBuilder.get().append(';');
        for (Object param : params)
          logContent.append(param).append(EXCEPTION_LINE_HEADER);
        GLOBAL_EXCEPTION.info(logContent.toString());
      }
      else {
        globalException(new Throwable("ScribeReporter.writeGlobalExceptionLog(...) with no param!!!"));
      }
    }
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
        globalException(new Throwable("ScribeReporter.console(...) with no param!!!"));
      }
    }
  }

  public static void authException(Object ... params) {
    if (params != null && params.length > 0) {
      StringBuilder logContent = GlobalVariable.stringBuilder.get();
      for (Object param : params)
        logContent.append(param).append(EXCEPTION_LINE_HEADER);
      AUTH_EXCEPTION.info(logContent.toString());
    }
    else {
      globalException(new Throwable("ScribeReporter.writePaymentException(...) with no param!!!"));
    }
  }

  public static void authException(Throwable cause) {
    StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
    for (StackTraceElement ste : cause.getStackTrace())
      builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
    AUTH_EXCEPTION.info(builder.toString());
  }

  public static void poolException(Throwable cause) {
    if(SYSTEM_INFO.USE_POOL_LOG) {
      StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
      for (StackTraceElement ste : cause.getStackTrace())
        builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
      POOL_EXCEPTION.info(builder.toString());
    }
  }

  public static void poolException(Object ... params) {
    if(SYSTEM_INFO.USE_POOL_LOG) {
      if (params != null && params.length > 0) {
        StringBuilder logContent = GlobalVariable.stringBuilder.get().append(';');
        for (Object param : params)
          logContent.append(param).append(EXCEPTION_LINE_HEADER);
        POOL_EXCEPTION.info(logContent.toString());
      }
      else {
        globalException(new Throwable("ScribeReporter.writeGlobalExceptionLog(...) with no param!!!"));
      }
    }
  }
}