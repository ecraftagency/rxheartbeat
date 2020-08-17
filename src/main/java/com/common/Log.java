package com.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import static com.common.Constant.*;

@SuppressWarnings("unused")
public class Log {
  private static final Logger GLOBAL_EXCEPTION = LoggerFactory.getLogger("global_exception");
  private static final String EXCEPTION_LINE_HEADER = "\n\t";

  public static void writeGlobalExceptionLog(Throwable cause) {
    if(GAME_FUNCTIONS.USE_GLOBAL_FILE_LOG) {
      StringBuilder builder = GlobalVariable.stringBuilder.get().append(cause.getMessage());
      for (StackTraceElement ste : cause.getStackTrace())
        builder.append(EXCEPTION_LINE_HEADER).append(ste.toString());
      GLOBAL_EXCEPTION.info(builder.toString());
    }
  }

  public static void writeGlobalExceptionLog(Object ... params) {
    if(GAME_FUNCTIONS.USE_GLOBAL_FILE_LOG) {
      if (params != null && params.length > 0) {
        StringBuilder logContent = GlobalVariable.stringBuilder.get().append(';');
        for (Object param : params)
          logContent.append(param).append(EXCEPTION_LINE_HEADER);
        GLOBAL_EXCEPTION.info(logContent.toString());
      }
      else {
        writeGlobalExceptionLog(new Throwable("ScribeReporter.writeGlobalExceptionLog(...) with no param!!!"));
      }
    }
  }
}
