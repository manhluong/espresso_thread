/*
 * Copyright (C) 2014 The Android Open Source Project
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
 */

package com.luongbui.espressothread;

import android.support.test.espresso.IdlingResource;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static android.support.test.espresso.core.deps.guava.base.Preconditions.checkNotNull;

/**
 * Created by luongbui on 09/08/16.
 *
 * Adapted from here:
 * https://android.googlesource.com/platform/frameworks/testing/+/android-support-test/espresso/core/src/main/java/android/support/test/espresso/base/AsyncTaskPoolMonitor.java
 */
public class ThreadIdlingResourceMonitor implements IdlingResource {

  private static final String TAG = "ThreadIdlingResourceMonitor";

  private final AtomicReference<IdleMonitor> monitor = new AtomicReference<IdleMonitor>(null);
  private final ThreadPoolExecutor executor;
  private final AtomicInteger activeBarrierChecks = new AtomicInteger(0);

  private ResourceCallback resourceCallback;

  public ThreadIdlingResourceMonitor(ExecutorService executor) {
    if (!(executor instanceof ThreadPoolExecutor)) {
      throw new IllegalArgumentException(TAG + " support ThreadPoolExecutor.");
    }

    this.executor = (ThreadPoolExecutor) checkNotNull(executor);
  }

  @Override public String getName() {
    return TAG;
  }

  @Override public boolean isIdleNow() {
    if (!executor.getQueue().isEmpty()) {
      return false;
    } else {
      int activeCount = executor.getActiveCount();
      if (0 != activeCount) {
        if (monitor.get() == null) {
          // if there's no idle monitor scheduled and there are still barrier
          // checks running, they are about to exit, ignore them.
          activeCount = activeCount - activeBarrierChecks.get();
        }
      }
      boolean isIdle = 0 == activeCount;

      if (isIdle) {
        resourceCallback.onTransitionToIdle();
      }
      return isIdle;
    }
  }

  @Override public void registerIdleTransitionCallback(ResourceCallback callback) {
    this.resourceCallback = callback;
  }

  private class IdleMonitor {
    private final Runnable onIdle;
    private final AtomicInteger barrierGeneration = new AtomicInteger(0);
    private final CyclicBarrier barrier;
    // written by main, read by all.
    private volatile boolean poisoned;

    private IdleMonitor(final Runnable onIdle) {
      this.onIdle = checkNotNull(onIdle);
      this.barrier = new CyclicBarrier(getThreadsNumber(), new Runnable() {
        @Override public void run() {
          if (executor.getQueue().isEmpty()) {
            // no one is behind us, so the queue is idle!
            monitor.compareAndSet(IdleMonitor.this, null);
            onIdle.run();
          } else {
            // work is waiting behind us, enqueue another block of tasks and
            // hopefully when they're all running, the queue will be empty.
            monitorForIdle();
          }
        }
      });
    }

    /**
     * ThreadPoolExecutor#getCorePoolSize() can return 0. In that case return the number of
     * available processors.
     */
    private int getThreadsNumber() {
      int poolSize = executor.getCorePoolSize();
      return poolSize > 0 ? poolSize : Runtime.getRuntime().availableProcessors();
    }

    private void monitorForIdle() {
      if (poisoned) {
        return;
      }
      if (isIdleNow()) {
        monitor.compareAndSet(this, null);
        onIdle.run();
      } else {
        // Submit N tasks that will block until they are all running on the thread pool.
        // at this point we can check the pool's queue and verify that there are no new
        // tasks behind us and deem the queue idle.
        int poolSize = executor.getCorePoolSize();
        final BarrierRestarter restarter = new BarrierRestarter(barrier, barrierGeneration);
        for (int i = 0; i < poolSize; i++) {
          executor.execute(new Runnable() {
            @Override public void run() {
              while (!poisoned) {
                activeBarrierChecks.incrementAndGet();
                int myGeneration = barrierGeneration.get();
                try {
                  barrier.await();
                  return;
                } catch (InterruptedException ie) {
                  // sorry - I cant let you interrupt me!
                  restarter.restart(myGeneration);
                } catch (BrokenBarrierException bbe) {
                  restarter.restart(myGeneration);
                } finally {
                  activeBarrierChecks.decrementAndGet();
                }
              }
            }
          });
        }
      }
    }
  }

  private static class BarrierRestarter {
    private final CyclicBarrier barrier;
    private final AtomicInteger barrierGeneration;

    BarrierRestarter(CyclicBarrier barrier, AtomicInteger barrierGeneration) {
      this.barrier = barrier;
      this.barrierGeneration = barrierGeneration;
    }

    /**
     * restarts the barrier.
     *
     * After the calling this function it is guaranteed that barrier generation has been
     * incremented
     * and the barrier can be awaited on again.
     *
     * @param fromGeneration the generation that encountered the breaking exception.
     */
    synchronized void restart(int fromGeneration) {
      // must be synchronized. T1 could pass the if check, be suspended before calling reset, T2
      // sails thru - and awaits on the barrier again before T1 has awoken and reset it.
      int nextGen = fromGeneration + 1;
      if (barrierGeneration.compareAndSet(fromGeneration, nextGen)) {
        // first time we've seen fromGeneration request a reset. lets reset the barrier.
        barrier.reset();
      } else {
        // some other thread has already reset the barrier - this request is a no op.
      }
    }
  }
}
