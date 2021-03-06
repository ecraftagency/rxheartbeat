package com.heartbeat.service.impl;

import com.heartbeat.model.Session;
import com.heartbeat.service.SessionInjector;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class EvilInjector implements SessionInjector {
  @Override
  public void inject(Session session, String path, String value) throws Exception {
    EvilExecutor evil = new EvilExecutor();
    long id           = System.currentTimeMillis();
    String source     = String.format("import com.heartbeat.model.Session;\n" +
            "public class SessionInjector {\n" +
            "  public static void inject%d(Session session) {\n" +
            "    %s\n" +
            "  }\n" +
            "}", id, value);


    Path javaFile   = evil.saveSource(source);
    Path classFile  = evil.compileSource(javaFile);
    Class<?> clazz  = evil.getClass(classFile);
    Method inject   = clazz.getMethod(String.format("inject%d", id), Session.class);
    inject.invoke(null, session);
  }

  public static class EvilExecutor {
    private Path saveSource(String source) throws IOException {
      String tmpProperty = System.getProperty("java.io.tmpdir");
      Path sourcePath = Paths.get(tmpProperty, "SessionInjector.java");
      Files.write(sourcePath, source.getBytes(StandardCharsets.UTF_8));
      return sourcePath;
    }

    private Path compileSource(Path javaFile) {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
      return javaFile.getParent().resolve("SessionInjector.class");
    }

    private Class<?> getClass(Path javaClass)
            throws MalformedURLException, ClassNotFoundException {
      URL classUrl = javaClass.getParent().toFile().toURI().toURL();
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
      return Class.forName("SessionInjector", true, classLoader);
    }
  }
}
