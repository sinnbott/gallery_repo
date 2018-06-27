package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.mypresenter.MultiPresenter;

import java.util.ArrayList;

public class MultiActivity extends AppCompatActivity {

    ScrollView mScrollView;
    TableLayout mTable;
    private TableRow mRow;

    EditText mEditText;
    TextView mTextView;

    ArrayList<String> mPaths;
    ArrayList<String> mSelectedPaths;

    MultiPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi);

        presenter = new MultiPresenter();

        mEditText = findViewById(R.id.txtMultipleKeywords);
        mTextView = findViewById(R.id.txtMultipleSelected);

        mTextView.setText("No pictures selected");

        mScrollView = findViewById(R.id.scrollMultiple);
        mTable = findViewById(R.id.tableMultiple);
        mRow = getRow();
        mTable.addView(mRow);

        mSelectedPaths = new ArrayList<String>();
        mPaths = presenter.getPictures();

        Bitmap img;

        int added = 0;

        for (int i = 0; i < mPaths.size(); i++) {
            if (added % 3 == 0 && added != 0) {
                mRow = getRow();
                mTable.addView(mRow);
            }

            String path = mPaths.get(i);
            ImageView iv = new ImageView(MultiActivity.this);
            iv.setPadding(12, 12, 12, 12);

            img = null;

            if (path != null && path != "") {
                img = BitmapFactory.decodeFile(path);
            }

            if (img != null) {
                added++;
                Bitmap copy = Bitmap.createScaledBitmap(img, 288, 384, false);
                iv.setImageBitmap(copy);
                iv.setTag(path);
                iv.setBackgroundColor(Color.argb(0, 0, 0, 0));
                iv.setOnClickListener(new ImageView.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mSelectedPaths.contains(v.getTag().toString())) {
                            mSelectedPaths.remove(v.getTag().toString());
                            v.setBackgroundColor(Color.argb(0, 0, 0, 0));

                        } else {
                            mSelectedPaths.add(v.getTag().toString());
                            v.setBackgroundColor(Color.argb(250, 0, 250, 0));
                        }
                        updateSelection();
                    }
                });

                mRow.addView(iv);
            }
        }
    }

    private TableRow getRow() {
        TableRow row = new TableRow(MultiActivity.this);
        row.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.FILL_PARENT, TableRow.LayoutParams.WRAP_CONTENT));
        return row;
    }

    public void save(View view) {
        String keywords = mEditText.getText().toString();
        Intent i = new Intent();

        if (presenter.save(keywords, mSelectedPaths)) {
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED, i);
        }

        finish();
    }

    public void cancel(View view) {
        finish();
    }

    private void updateSelection() {
        int count = mSelectedPaths.size();
        String strCount = "No pictures selected";
        if (count > 0) {
            strCount = Integer.toString(count) + " selected";
        }

        mTextView.setText(strCount);
    }
}
