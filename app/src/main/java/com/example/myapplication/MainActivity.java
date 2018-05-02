package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ACTIVITY_SEARCH = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;

    ImageView mImageView;

    String mCurrentPhotoPath;
    Date mStartDate;
    Date mEndDate;
    int mCurrentPhotoIndex;

    ArrayList<String> mPhotoGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImageView = findViewById(R.id.imageView);

        mStartDate = new Date(0);
        mEndDate = new Date();
        mPhotoGallery = populateGallery(mStartDate, mEndDate);

        Log.d("onCreate, size", Integer.toString(mPhotoGallery.size()));
        if (mPhotoGallery.size() > 0) {
            mCurrentPhotoIndex = mPhotoGallery.size() - 1;
            mCurrentPhotoPath = mPhotoGallery.get(mCurrentPhotoIndex);
            displayPhoto(mCurrentPhotoPath);
        }
    }

    public void clickSnap(View view) {
        dispatchTakePictureIntent();
    }

    public void clickSettings(View view) {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        startActivity(settingsIntent);
    }

    public void clickSearch(View view) {
        Intent searchIntent = new Intent(this, SearchActivity.class);
        startActivityForResult(searchIntent, REQUEST_ACTIVITY_SEARCH);
    }

    public void clickUpload(View view) {
        Intent uploadIntent = new Intent(this, UploadActivity.class);
        startActivity(uploadIntent);
    }

    private void dispatchTakePictureIntent() {
//        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
//            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
//        }

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure a camera activity can handle this
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the file
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                Log.e("File Creation", "Failed");
            }

//            Log.d("createImageFile f2:", photoFile.getAbsolutePath());
            if (photoFile != null) {
                Uri photoUri = FileProvider.getUriForFile(this,
                        "com.example.myapplication.fileprovider",
                        photoFile);
//                Log.d("fileprovider...", photoUri.toString());
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
//                Log.d("createImageFile f3: ", takePictureIntent.getExtras().toString());
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    public void scrollClick(View view) {
        switch (view.getId()) {
            case R.id.btnLeft:
                if (mCurrentPhotoIndex > 0)
                    --mCurrentPhotoIndex;
                break;
            case R.id.btnRight:
                if (mCurrentPhotoIndex < (mPhotoGallery.size() - 1))
                    ++mCurrentPhotoIndex;
                break;
            default:
                break;
        }
        if (mPhotoGallery.size() > 0) {
            mCurrentPhotoPath = mPhotoGallery.get(mCurrentPhotoIndex);
            Log.d("scroll, size", Integer.toString(mPhotoGallery.size()));
            Log.d("scroll, current", Integer.toString(mCurrentPhotoIndex));
            displayPhoto(mCurrentPhotoPath);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mPhotoGallery = populateGallery(mStartDate, mEndDate);
                Log.d("onActivityResult, size", Integer.toString(mPhotoGallery.size()));
                int size = mPhotoGallery.size();
                Log.d("createImageFile", "Picture Taken");
                mCurrentPhotoIndex = size - 1;
//                Log.d("galleryFiles2", mPhotoGallery.toString());
//                mCurrentPhotoPath = mPhotoGallery.get(mCurrentPhotoIndex);
//                Log.d("galleryFilesCurrent", mPhotoGallery.get(mCurrentPhotoIndex));
//                Log.d("path:2", mCurrentPhotoPath);
                displayPhoto(mCurrentPhotoPath);
            }
        }
        if (requestCode == REQUEST_ACTIVITY_SEARCH) {
            Log.d("onActivityResult", "REQUEST_ACTIVITY_SEARCH");
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                String start  = extras.getString("STARTDATE");
                String end  = extras.getString("ENDDATE");

                try {
                    mStartDate = dateFormat.parse(start);
                } catch (ParseException ex) {
                    Log.e("search", "parse start failed: [" +start +"]");
                    mStartDate = new Date(0);
                }

                try {
                    mEndDate = dateFormat.parse(end);
                } catch (ParseException ex) {
                    Log.e("search", "parse end failed: [" +end +"]");
                    mEndDate = new Date();
                }

                Log.d("onActivityResult", "strings: [" +start +"]/[" +end +"]");
                Log.d("onActivityResult", "dates: [" +dateFormat.format(mStartDate) +"]/[" +dateFormat.format(mEndDate) +"]");
                populateGallery(mStartDate, mEndDate);
                mCurrentPhotoIndex = (mPhotoGallery.size() > 0) ? mPhotoGallery.size() - 1 : 0;
            }
        }
    }

    private void displayPhoto(String path) {
        Log.d("displayPhoto:", path);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(path));
        mImageView.setTag(path);
//        mImageView.setTag("badtag_20150808");  // adding a tag with the wrong date, like this one, will cause TakePictureTests to fail
    }

    private ArrayList<String> populateGallery(Date minDate, Date maxDate) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date photoDate;
        String photoDateString;

        int i = 0;
        Log.d("populateGallery dates", "[" +mStartDate +"]/[" +mEndDate +"]");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Android/data/com.example.myapplication/files/Pictures");
        mPhotoGallery = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for(File f: file.listFiles()) {
                photoDateString = f.getPath().split("_")[1];
//                if (i == 0) {
                    Log.d("populateGallery", "min:[" + dateFormat.format(minDate) + "]  "
                            + "max:[" + dateFormat.format(maxDate) + "]  "
                            + "current:[" + photoDateString + "]");
                    try {
                        photoDate = dateFormat.parse(photoDateString);
                        if ( minDate.before(photoDate) || minDate.equals(photoDate) ) {
                            if ( maxDate.after(photoDate) || maxDate.equals(photoDate) ) {
                                mPhotoGallery.add(f.getPath());
//                                Log.d("populateGallery", "dateFits:[" +photoDateString +"]");
                            } else {
//                              Log.d("populateGallery", "photo:[" +photoDateString +"] after [" +dateFormat.format(maxDate) +"]");
                            }
                        } else {
//                            Log.d("populateGallery", "photo:[" +photoDateString +"] before [" +dateFormat.format(minDate) +"]");
                        }

                    } catch (ParseException ex) {
                        Log.e("populateGallery", "DATE PARSE FAILED: " +f.getPath());
                    }
//                }
                i++;
            }
        }
        Log.d("populateGallery", "finalCount" +Integer.toString(mPhotoGallery.size()));
        return mPhotoGallery;
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        Log.d("createImageFile f1", imageFileName);
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );
        mCurrentPhotoPath = image.getAbsolutePath();
        Log.d("createImageFile path:", mCurrentPhotoPath);
        return image;
    }
}
