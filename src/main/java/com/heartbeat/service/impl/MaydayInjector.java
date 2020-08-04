package com.heartbeat.service.impl;

import com.heartbeat.service.ConstantInjector;

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

public class MaydayInjector implements ConstantInjector {
  @Override
  public void inject(String path, String value) throws Exception {
    MaydayExecutor mayday = new MaydayExecutor();

    String source = String.format(
            "import static com.heartbeat.common.Constant.*;\n" +
            "public class Injector {\n" +
            "  public static void inject() {\n" +
            "    %s\n" +
            "  }\n" +
            "}", value);

    Path javaFile   = mayday.saveSource(source);
    Path classFile  = mayday.compileSource(javaFile);
    Class<?> clazz  = mayday.getClass(classFile);
    Method inject   = clazz.getMethod("inject");
    inject.invoke(null);
  }

  public static class MaydayExecutor {
    private Path saveSource(String source) throws IOException {
      String tmpProperty  = System.getProperty("java.io.tmpdir");
      Path sourcePath     = Paths.get(tmpProperty, "Injector.java");
      Files.write(sourcePath, source.getBytes(StandardCharsets.UTF_8));
      return sourcePath;
    }

    private Path compileSource(Path javaFile) {
      JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
      compiler.run(null, null, null, javaFile.toFile().getAbsolutePath());
      return javaFile.getParent().resolve("Injector.class");
    }

    private Class<?> getClass(Path javaClass)
            throws MalformedURLException, ClassNotFoundException {
      URL classUrl = javaClass.getParent().toFile().toURI().toURL();
      URLClassLoader classLoader = URLClassLoader.newInstance(new URL[]{classUrl});
      return Class.forName("Injector", true, classLoader);
    }
  }
}