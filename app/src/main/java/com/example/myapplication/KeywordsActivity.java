package com.example.myapplication;

import android.content.Intent;
import android.media.ExifInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.mydb.FileSystemDataStore;
import com.example.mydb.IDataStore;

public class KeywordsActivity extends AppCompatActivity {

    EditText mTextKeywords;
    String mPhotoPath;
    IDataStore storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keywords);
        storage = new FileSystemDataStore();

        mTextKeywords = findViewById(R.id.textKeywordEntry);
        Intent intent = getIntent();
        mPhotoPath = intent.getStringExtra("FILENAME");

        String keywords = storage.getKeywords(mPhotoPath);
        mTextKeywords.setText(keywords);
    }

    public void save (final View view) {
        String keywords = mTextKeywords.getText().toString();
        boolean saved = storage.savePicture(mPhotoPath, keywords);

        Intent i = new Intent();
        if (saved)
            setResult(RESULT_OK, i);

        finish();
    }

    public void cancel (final View v) { finish(); }
}
