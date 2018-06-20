package com.example.myapplication;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.hamcrest.Description;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageViewMatcher {

    public static BoundedMatcher<View, ImageView> hasTag() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("picture file includes datestamp");
            }

            @Override
            public boolean matchesSafely(ImageView imageView) {
                String testTag = imageView.getTag().toString().split("_")[1];
                Date testDate = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
                String testDateString = dateFormat.format(testDate);
//                Log.d("hasTag expected", testDateString);
//                Log.d("hasTag actual", testTag);
                return testDateString.equals(testTag);
            }
        };
    }

    public static BoundedMatcher<View, ImageView> hasDrawable() {
        return new BoundedMatcher<View, ImageView>(ImageView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("picture ImageView is displaying image");
            }

            @Override
            protected boolean matchesSafely(ImageView iv) {
//                Log.d("hasDrawable", iv.getDrawable().toString());
                return iv.getDrawable() != null;
            }
        };
    }
}