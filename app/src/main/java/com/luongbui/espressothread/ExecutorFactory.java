package com.luongbui.espressothread;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luongbui on 09/08/16.
 *
 * Simple factory pattern to get the ExecutorService.
 */
public class ExecutorFactory {
  private static ExecutorFactory instance = null;

  private ExecutorService executor;

  private ExecutorFactory() {
    executor = Executors.newCachedThreadPool();
  }

  /**
   * @return a ThreadPoolExecutor.
   */
  public ExecutorService getCachedThreadPoolExecutor() {
    return executor;
  }

  public static synchronized ExecutorFactory getInstance() {
    if (instance == null) {
      instance = new ExecutorFactory();
    }
    return instance;
  }
}