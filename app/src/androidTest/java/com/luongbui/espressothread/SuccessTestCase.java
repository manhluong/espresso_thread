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
  public void registerIntentServiceIdlingResource() {
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
  public void unregisterIntentServiceIdlingResource() {
    Espresso.unregisterIdlingResources(idlingResource);
  }
}
