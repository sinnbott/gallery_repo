package com.example.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Instrumentation.ActivityResult;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.clearText;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static com.example.myapplication.ImageViewMatcher.hasTag;
import static com.example.myapplication.ImageViewMatcher.hasDrawable;
import static org.hamcrest.Matchers.not;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class TakePictureTests {

    @Rule
    public IntentsTestRule<MainActivity> mIntentsRule = new IntentsTestRule<>(
            MainActivity.class);

    @Before
    public void stubCameraIntent() {
        ActivityResult result = createImageCaptureStub();

        // Stub the Intent.
        intending(hasAction(MediaStore.ACTION_IMAGE_CAPTURE)).respondWith(result);
    }

    @Test
    public void testTakePhoto() {
        // Click on the button that will trigger the stubbed intent.
        onView(withId(R.id.btnShowSnap)).perform(click());

        // With no user interaction, the ImageView will have a drawable.
        onView(withId(R.id.imageView)).check(matches(hasTag()));

        onView(withId(R.id.imageView)).check(matches(hasDrawable()));
    }

    @Test
    public void testAddKeywords() {
        onView(withId(R.id.imageView)).perform(click());

        onView(withId(R.id.textKeywordEntry)).perform(typeText("testkeyword1"), closeSoftKeyboard());

        onView(withId(R.id.btnSave)).perform(click());
    }

    @Test
    public void testRemoveKeywords() {
        onView(withId(R.id.imageView)).perform(click());

        onView(withId(R.id.textKeywordEntry)).perform(clearText(), closeSoftKeyboard());

        onView(withId(R.id.btnSave)).perform(click());
    }

    @Test
    public void testCancelKeywords() {
        onView(withId(R.id.imageView)).perform(click());

        onView(withId(R.id.textKeywordEntry)).perform(typeText("thesekeywordswillnotbesaved"), closeSoftKeyboard());

        onView(withId(R.id.btnCancel)).perform(click());
    }

    private ActivityResult createImageCaptureStub() {
        // Put the drawable in a bundle.
        Bundle bundle = new Bundle();
        bundle.putParcelable(MediaStore.ACTION_IMAGE_CAPTURE, BitmapFactory.decodeResource(
                mIntentsRule.getActivity().getResources(), R.drawable.test_drawable));

        // Create the Intent that will include the bundle.
        Intent resultData = new Intent();
        resultData.putExtras(bundle);

        // Create the ActivityResult with the Intent.
        return new ActivityResult(Activity.RESULT_OK, resultData);
    }
}