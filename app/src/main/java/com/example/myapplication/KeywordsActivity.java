package com.example.myapplication;

import android.content.Intent;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class KeywordsActivity extends AppCompatActivity {

    EditText mTextKeywords;
    String mPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keywords);

        mTextKeywords = findViewById(R.id.textKeywordEntry);
        Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra("FILENAME");

        try {
            ExifInterface exifInterface = new ExifInterface(mPhotoPath);
            String keywords = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
            mTextKeywords.setText(keywords);
        } catch (Exception e) {
            Log.e("onCreate", "error getting exif data");
        } //ImageDescription
    }

    public void save() {
//        try {
//
//            ExifInterface exifInterface = new ExifInterface(mCurrentPhotoPath);
//            exifInterface.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "TestVal");
////                    exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Double.toString(mLastLocation.getLatitude()));
////                    exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, Double.toString(mLastLocation.getLongitude()));
//            exifInterface.saveAttributes();
//        } catch (Exception e) {
//            Log.e("onActivityResult", "error setting exif data");
//        } //ImageDescription
    }

    public void save (final View view) {
        try {
            ExifInterface exifInterface = new ExifInterface(mPhotoPath);
            exifInterface.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, mTextKeywords.getText().toString());
            exifInterface.saveAttributes();
        } catch (Exception e) {
            Log.e("save", "error saving exif image description");
        }
        Intent i = new Intent();
        setResult(RESULT_OK, i);
        finish();
    }

    public void cancel (final View v) { finish(); }
}
