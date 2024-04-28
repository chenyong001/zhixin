package com.tansci.utils;

import java.io.Closeable;
import java.util.Objects;

public abstract class ResourcesUtil {
  public ResourcesUtil() {
  }

  private static <T extends Closeable> void close(T resource) {
    if (Objects.nonNull(resource)) {
      try {
        resource.close();
      } catch (Exception var2) {
        var2.printStackTrace();
      }
    }

  }

  @SafeVarargs
  public static <T extends Closeable> void multiClose(T... resources) {
    Closeable[] var1 = resources;
    int var2 = resources.length;

    for (int var3 = 0; var3 < var2; ++var3) {
      T resource = (T) var1[var3];
      close(resource);
    }

  }
}