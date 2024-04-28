package com.tansci.utils;

import java.util.Collection;

/**
 * @Author chenyong
 * @Date 2022/12/13 10:27
 * @Version 1.0
 */
public class CollectionUtil {

  public static boolean isEmpty(Collection c) {
    return (c != null && !c.isEmpty()) ? false : true;
  }
}
