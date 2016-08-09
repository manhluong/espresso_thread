package com.luongbui.espressothread;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.notNullValue;

/**
 * Created by luongbui on 09/08/16.
 */
@RunWith(AndroidJUnit4.class) public class FailedTestCase {

  @Rule public ActivityTestRule<MainActivity> mainActivityRule =
      new ActivityTestRule<>(MainActivity.class);

  @Test public void failedTest() {
    // Check buttons are in place.
    onView(withText("Async Task")).check(matches(notNullValue()));
    onView(withText("Thread")).check(matches(notNullValue()));

    // Press the async task button and check.
    onView(withText("Async Task")).perform(click());
    onView(withText("Launched with AsyncTask")).check(matches(notNullValue()));
    pressBack();

    // Press the thread button and check. This will fail.
    onView(withText("Thread")).perform(click());
    onView(withText("Launched with Thread")).check(matches(notNullValue()));
  }
}
