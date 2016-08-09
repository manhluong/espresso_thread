/*******************************************************************************
 * Copyright 2016 Manh Luong   Bui.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

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