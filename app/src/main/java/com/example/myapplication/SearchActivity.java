package com.example.myapplication;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class SearchActivity extends AppCompatActivity {

    private EditText mStartDate;
    private EditText mEndDate;
    private EditText mStartLoc;
    private EditText mEndLoc;
    private EditText mKeywords;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        mStartDate = findViewById(R.id.textStartDate);
        mEndDate = findViewById(R.id.textEndDate);
        mStartLoc = findViewById(R.id.textStartLoc);
        mEndLoc = findViewById(R.id.textEndLoc);
        mKeywords = findViewById(R.id.textKeywordSearch);
    }

    public void search (final View view) {
        Intent i = new Intent();
        i.putExtra("STARTDATE", mStartDate.getText().toString());
        i.putExtra("ENDDATE", mEndDate.getText().toString());
        i.putExtra("LOCSTART", mStartLoc.getText().toString());
        i.putExtra("LOCEND", mEndLoc.getText().toString());
        i.putExtra("KEYWORDS", mKeywords.getText().toString());
        setResult(RESULT_OK, i);
        finish();
    }

    public void cancel(final View v) { finish(); }
}
