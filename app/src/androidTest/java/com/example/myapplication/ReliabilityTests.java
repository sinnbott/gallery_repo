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
import static android.support.test.espresso.action.ViewActions.typeText;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

import static org.junit.Assert.assertTrue;


/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ReliabilityTests {
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void ReliabilityTest() {

        int runs = 100;
        int random = 0;

        int ScrollCount = 0;
        int ScrollFails = 0;

        int SearchDatecount = 0;
        int SearchDateFails = 0;

        int SearchLocationCount = 0;
        int SearchLocationFails = 0;

        int SearchKeywordCount = 0;
        int SearchKeywordFails = 0;

        int SearchMultipleCount = 0;
        int SearchMultipleFails = 0;

        int ClearCount = 0;
        int Clearfails = 0;

        int SaveOneCount = 0;
        int SaveOneFails = 0;

        int SaveMultipleCount = 0;
        int SaveMultipleFails = 0;

        int CancelCount = 0;
        int CancelFails = 0;

        for (int i = 0; i < runs; i++) {
            random = (int)(Math.random() * 100) + 1;

            if (random <= 5) {
                try {
                    ScrollLeft();
                    ScrollCount++;
                } catch (Exception e) {
                    ScrollCount++;
                    ScrollFails++;
                }
            }
            else if (random <= 10) {
                try {
                    ScrollRight();
                    ScrollCount++;
                } catch (Exception e) {
                    ScrollCount++;
                    ScrollFails++;
                }
            } else if (random <= 25) {
                try {
                    FilterDate();
                    SearchDatecount++;
                } catch (Exception e) {
                    SearchDatecount++;
                    SearchDateFails++;
                }
            }
            else if (random <= 35) {
                try {
                    FilterLocation();
                    SearchLocationCount++;
                } catch (Exception e) {
                    SearchLocationCount++;
                    SearchLocationFails++;
                }
            }
            else if (random <= 50) {
                try {
                    FilterKeyword();
                    SearchKeywordCount++;
                } catch (Exception e) {
                    SearchKeywordCount++;
                    SearchKeywordFails++;
                }
            }
            else if (random <= 60) {
                try {
                    FilterMultiple();
                    SearchMultipleCount++;
                } catch (Exception e) {
                    SearchMultipleCount++;
                    SearchMultipleFails++;
                }
            }
            else if (random <= 70) {
                try {
                    FilterClear();
                    ClearCount++;
                } catch (Exception e) {
                    ClearCount++;
                    Clearfails++;
                }
            }
            else if (random <= 80) {
                try {
                    SaveOneKeyword();
                    SaveOneCount++;
                } catch (Exception e) {
                    SaveOneCount++;
                    SaveOneFails++;
                }
            }
            else if (random <= 90) {
                try {
                    SaveMultipleKeywords();
                    SaveMultipleCount++;
                } catch (Exception e) {
                    SaveMultipleCount++;
                    SaveMultipleFails++;
                }
            }
            else if (random <= 100) {
                try {
                    SaveCancel();
                    CancelCount++;
                } catch (Exception e) {
                    CancelCount++;
                    CancelFails++;
                }
            }
        }

        int totalCount =
                CancelCount +
                SaveMultipleCount +
                SaveOneCount +
                ScrollCount +
                SearchDatecount +
                SearchKeywordCount +
                SearchLocationCount +
                SearchMultipleCount +
                ClearCount;

        int totalFails =
                CancelFails +
                SaveMultipleFails +
                SaveOneFails +
                ScrollFails +
                SearchDateFails +
                SearchKeywordFails +
                SearchLocationFails +
                SearchMultipleFails +
                Clearfails;

        // TODO add assertions for action-specific counts and failure rates

        assertTrue("Failure rate should be less than 3 / 1000 user actions; actual["
                + totalFails + "/" +totalCount +"]"
                , (totalFails / runs) < (3.00f / 1000.00f));

        assertTrue("Ideally we have zero failures; actual["
                        +totalFails +"/" +totalCount +"]"
                , (totalFails == 0));
    }

    public void FilterDate() {
        onView(withId(R.id.btnShowSearch)).perform(click());
        onView(withId(R.id.textStartDate)).perform(typeText("05/01/2018"), closeSoftKeyboard());
        onView(withId(R.id.textEndDate)).perform(typeText("05/05/2018"), closeSoftKeyboard());
        onView(withId(R.id.btnSearch)).perform(click());
        for (int i = 0; i <= 2; i++) {
            onView(withId(R.id.btnLeft)).perform(click());
        }
    }

    public void FilterLocation() {
        onView(withId(R.id.btnShowSearch)).perform(click());
        onView(withId(R.id.textStartLoc)).perform(typeText("-300/-300"), closeSoftKeyboard());
        onView(withId(R.id.textEndLoc)).perform(typeText("300/300"), closeSoftKeyboard());
        onView(withId(R.id.btnSearch)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnRight)).perform(click());
        }
    }

    public void FilterKeyword() {
        String[] words = {
                "", "purple", "elephant", "zero", "multiple", "alpha", "omega", "bike", "truck",
                "test", "mine", "europe", "hearts", "explosions", "books", "dishes", "vacation", "hope", "last", "laugh",
                "music", "hashtagyolo", "typing", "help", "ouch", "red", "green", "yellow", "blue", "orange"
        };

        int index = (int)(Math.random() * 30);
        String filter = words[index];

        onView(withId(R.id.btnShowSearch)).perform(click());
        onView(withId(R.id.textKeywordSearch)).perform(typeText(filter), closeSoftKeyboard());
        onView(withId(R.id.btnSearch)).perform(click());
        for (int i = 0; i <= 5; i++) {
            onView(withId(R.id.btnLeft)).perform(click());
        }
    }

    public void FilterMultiple() {
        onView(withId(R.id.btnShowSearch)).perform(click());

        onView(withId(R.id.textStartLoc)).perform(typeText("01/01/2009"), closeSoftKeyboard());

        onView(withId(R.id.textKeywordSearch)).perform(typeText("green"), closeSoftKeyboard());

        onView(withId(R.id.btnSearch)).perform(click());

        onView(withId(R.id.btnRight)).perform(click());
    }

    public void FilterClear() {
        onView(withId(R.id.btnShowSearch)).perform(click());
        onView(withId(R.id.btnSearch)).perform(click());
    }

    public void SaveOneKeyword() {
        String[] words = {
            "", "purple", "elephant", "zero", "multiple", "alpha", "omega", "bike", "truck",
            "test", "mine", "europe", "hearts", "explosions", "books", "dishes", "vacation", "hope", "last", "laugh",
            "music", "hashtagyolo", "typing", "help", "ouch"
        };

        int index = (int)(Math.random() * 25);
        String keyword = words[index];

        onView(withId(R.id.imageView)).perform(click());
        onView(withId(R.id.textKeywordEntry)).perform(replaceText(keyword), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());
    }

    public void SaveMultipleKeywords() {
        String[] words = {"", "purple", "elephant", "zero", "multiple", "alpha", "omega", "bike", "truck",
                "test", "mine", "europe", "hearts", "explosions", "books", "dishes", "vacation", "hope", "last", "laugh",
                "music", "hashtagyolo", "typing", "help", "ouch"
        };
        int index1 = (int)(Math.random() * 25);
        int index2 = (int)(Math.random() * 25);
        int index3 = (int)(Math.random() * 25);

        String keywords = words[index1] + ", " +words[index2] + ", " + words[index3];

        onView(withId(R.id.imageView)).perform(click());
        onView(withId(R.id.textKeywordEntry)).perform(replaceText(keywords), closeSoftKeyboard());
        onView(withId(R.id.btnSave)).perform(click());
    }

    public void SaveCancel() {
        onView(withId(R.id.imageView)).perform(click());
        onView(withId(R.id.textKeywordEntry)).perform(replaceText("asdfasdfasdF"), closeSoftKeyboard());
        pressBack();
    }

    public void ScrollLeft() {
        onView(withId(R.id.btnLeft)).perform(click());
    }

    public void ScrollRight() {
        onView(withId(R.id.btnRight)).perform(click());
    }
}
