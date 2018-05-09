package com.example.myapplication;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import android.support.test.rule.ActivityTestRule;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class UITests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void TestFilterDate() {
        onView(withId(R.id.btnShowSearch)).perform(click());
        onView(withId(R.id.textStartDate)).perform(typeText("05/01/2018"), closeSoftKeyboard());
        onView(withId(R.id.textEndDate)).perform(typeText("05/05/2018"), closeSoftKeyboard());
        onView(withId(R.id.btnSearch)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnLeft)).perform(click());
        }
    }

    @Test public void TestFilterLocation() {
        onView(withId(R.id.btnShowSearch)).perform(click());
        onView(withId(R.id.textStartLoc)).perform(typeText("-300/-300"), closeSoftKeyboard());
        onView(withId(R.id.textEndLoc)).perform(typeText("300/300"), closeSoftKeyboard());
        onView(withId(R.id.btnSearch)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }
}
