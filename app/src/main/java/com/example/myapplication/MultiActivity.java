package com.example.myapplication;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;

import com.example.mydb.FileSystemDataStore;
import com.example.mydb.IDataStore;

import java.util.ArrayList;

public class MultiActivity extends AppCompatActivity {

    IDataStore storage;

    ScrollView mScrollView;
    TableLayout mTable;
    private TableRow mRow;

    ArrayList<String> mPaths;
    ArrayList<String> mSelectedPaths;
    String mKeywords;

    Button mButtonSave;
    Button mButtonCancel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);

        storage = new FileSystemDataStore();
        storage.populateGallery(null);

        mScrollView = findViewById(R.id.scrollMultiple);
        mTable = findViewById(R.id.tableMultiple);
        mRow = getRow();
        mTable.addView(mRow);

        mPaths = storage.getPictures();

        for (int i = 0; i < mPaths.size(); i++) {
            if (i % 3 == 0 && i != 0) {
                mRow = getRow();
                mTable.addView(mRow);
            }

            Log.d("multiSelect", mPaths.get(i));
            ImageView iv = new ImageView(MultiActivity.this);
            Bitmap img = BitmapFactory.decodeFile(mPaths.get(i));
            if (img != null) {
                Bitmap copy = Bitmap.createScaledBitmap(img, 288, 384, false);
                iv.setImageBitmap(copy);
                mRow.addView(iv);
            }

        }


        mKeywords = "";
    }

    private TableRow getRow() {
        TableRow row = new TableRow(MultiActivity.this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        return row;
    }
}
