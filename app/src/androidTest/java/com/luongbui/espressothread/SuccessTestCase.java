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

import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Created by luongbui on 09/08/16.
 */
public class SuccessTestCase {

  @Rule public ActivityTestRule<MainActivity> mainActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  private IdlingResource idlingResource;

  @Before
  public void setUp() {
    idlingResource = new ThreadIdlingResourceMonitor(
        ExecutorFactory.getInstance().getCachedThreadPoolExecutor());
    Espresso.registerIdlingResources(idlingResource);
  }

  @Test public void successTest() {
    // Check buttons are in place.
    onView(withText("Async Task")).check(matches(notNullValue()));
    onView(withText("Thread")).check(matches(notNullValue()));

    // Press the async task button and check.
    onView(withText("Async Task")).perform(click());
    onView(withText("Launched with AsyncTask")).check(matches(notNullValue()));
    pressBack();

    // Press the thread button and check.
    onView(withText("Thread")).perform(click());
    onView(withText("Launched with Thread")).check(matches(notNullValue()));
  }

  @After
  public void tearDown() {
    Espresso.unregisterIdlingResources(idlingResource);
  }
}
