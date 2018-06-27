package com.example.myapplication;

import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class PerformanceTests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void SearchPerformance() {
        // Test screen navigation
        Long t1 = System.currentTimeMillis();
        onView(withId(R.id.btnShowSearch)).perform(click());
        Long t2 = System.currentTimeMillis();
        assertTrue("Should take < 500ms to navigate to search", (t2 - t1) < 500);

        // Test gallery searching performance
        onView(withId(R.id.textStartDate)).perform(typeText("05/01/2018"), closeSoftKeyboard());
        onView(withId(R.id.textKeywordSearch)).perform(typeText("red"), closeSoftKeyboard());
        t1 = System.currentTimeMillis();
        onView(withId(R.id.btnSearch)).perform(click());
        t2 = System.currentTimeMillis();
        assertTrue("Should take < 1000ms to search", (t2 - t1) < 1000);

    }

    @Test
    public void KeywordPerformance() {
        for (int i = 0; i < 7; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }

        // Test screen navigation performance
        Long t1 = System.currentTimeMillis();
        onView(withId(R.id.imageView)).perform(click());
        Long t2 = System.currentTimeMillis();
        assertTrue("Should take <500ms to navigate to keyword screen", (t2 - t1) < 500);

        // Test saving keywords performance
        onView(withId(R.id.textKeywordEntry)).perform(replaceText("red42, autoinsertedKeyword, WOWTHISISATEST"), closeSoftKeyboard());
        t1 = System.currentTimeMillis();
        onView(withId(R.id.btnSave)).perform(click());
        t2 = System.currentTimeMillis();
        assertTrue("Should take <500 ms to save keywords", (t2 - t1) < 500);

        // Test cancelling keywords performance
        onView(withId(R.id.imageView)).perform(click());
        onView(withId(R.id.textKeywordEntry)).perform(replaceText("24r356egdf"), closeSoftKeyboard());
        t1 = System.currentTimeMillis();
        pressBack();
        t2 = System.currentTimeMillis();
        assertTrue("Should take <500 ms to cancel search", (t2 - t1) < 500);
    }
}
