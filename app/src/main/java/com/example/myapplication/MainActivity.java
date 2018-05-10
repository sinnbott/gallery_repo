package com.example.myapplication;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.mydb.FileSystemDataStore;
import com.example.mydb.IDataStore;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ACTIVITY_SEARCH = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ACTIVITY_KEYWORDS = 2;

    IDataStore storage;

    ImageView mImageView;

    String mCurrentPhotoPath;
    Date mStartDate;
    Date mEndDate;
    Double[] mLocationSearch = null;
    String[] mKeywordSearch = null;
    int mCurrentPhotoIndex;


    FusedLocationProviderClient mLocationClient;
    public Location mLastLocation;

    ArrayList<String> mPhotoGallery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageView = findViewById(R.id.imageView);

        mStartDate = new Date(0);
        mEndDate = new Date();

        storage = new FileSystemDataStore();
        mKeywordSearch = null;
        storage.populateGallery(mStartDate, mEndDate, mLocationSearch, mKeywordSearch);

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getFileLocation();

        showPicture();
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

    public void clickPhoto(View view) {
        Intent keywordIntent = new Intent(this, KeywordsActivity.class);
        keywordIntent.putExtra("FILENAME", storage.getPicture());
        startActivityForResult(keywordIntent, REQUEST_ACTIVITY_KEYWORDS);
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
                storage.previousPicture();
                break;
            case R.id.btnRight:
                storage.nextPicture();
                break;
            default:
                break;
        }
        showPicture();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (resultCode == RESULT_OK) {
                mStartDate = new Date(0);
                mEndDate = new Date();
                mLocationSearch = null;
                mKeywordSearch = null;
                storage.populateGallery(mStartDate, mEndDate, mLocationSearch, mKeywordSearch);
                showPicture();
                Toast toast = Toast.makeText(getApplicationContext(), "Tap picture to add caption", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
        if (requestCode == REQUEST_ACTIVITY_SEARCH) {
            Log.d("onActivityResult", "REQUEST_ACTIVITY_SEARCH");
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();

                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

                String start  = extras.getString("STARTDATE");
                String end  = extras.getString("ENDDATE");
                String locStart = extras.getString("LOCSTART");
                String locEnd = extras.getString("LOCEND");
                String keywords = extras.getString("KEYWORDS");

                try {
                    mStartDate = dateFormat.parse(start);
                } catch (ParseException ex) {
                    Log.e("search", "parse start date failed: [" +start +"]");
                    mStartDate = new Date(0);
                }

                try {
                    mEndDate = dateFormat.parse(end);
                } catch (ParseException ex) {
                    Log.e("search", "parse end date failed: [" +end +"]");
                    mEndDate = new Date();
                }

                try {
                    locStart = locStart.replaceAll(" ", "");
                    String locStartLat = locStart.split("/")[0];
                    String locStartLong = locStart.split("/")[1];
                    Double dLocTop = Double.parseDouble(locStartLat);
                    Double dLocLeft = Double.parseDouble(locStartLong);

                    locEnd = locEnd.replaceAll(" ", "");
                    String locEndLat = locEnd.split("/")[0];
                    String locEndLong = locEnd.split("/")[1];
                    Double dLocBot = Double.parseDouble(locEndLat);
                    Double dLocRight = Double.parseDouble(locEndLong);

                    mLocationSearch = new Double[4];
                    mLocationSearch[0] = dLocTop;
                    mLocationSearch[1] = dLocLeft;
                    mLocationSearch[2] = dLocBot;
                    mLocationSearch[3] = dLocRight;

                } catch (Exception e) {
                    Log.e("onActivityResult", "Error getting location search");
                    mLocationSearch = null;
                }

                mKeywordSearch = keywords.split(",");
                if (mKeywordSearch.length == 1 && mKeywordSearch[0].equals(""))
                    mKeywordSearch = new String[0];
                for (int i = 0; i < mKeywordSearch.length; i++) {
                    mKeywordSearch[i] = mKeywordSearch[i].trim();
                    Log.d("Keywords...", mKeywordSearch[i]);
                }

                Log.d("onActivityResult", "strings: [" +start +"]/[" +end +"]");
                Log.d("onActivityResult", "dates: [" +dateFormat.format(mStartDate) +"]/[" +dateFormat.format(mEndDate) +"]");

//                populateGallery(mStartDate, mEndDate, mLocationSearch, mKeywordSearch);
                storage.populateGallery(mStartDate, mEndDate, mLocationSearch, mKeywordSearch);
                showPicture();
            }
        }

        if (requestCode == REQUEST_ACTIVITY_KEYWORDS) {
            if (resultCode == RESULT_OK) {
                Toast toast = Toast.makeText(getApplicationContext(), "Keywords saved...", Toast.LENGTH_SHORT);
                toast.show();
            } else if (resultCode != RESULT_CANCELED) {
                Toast toast = Toast.makeText(getApplicationContext(), "Error saving keywords...", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void showPicture() {
        String picture = storage.getPicture();
        mImageView.setImageBitmap(BitmapFactory.decodeFile(picture));
        mImageView.setTag(picture);

        Log.d("showPicture:", picture);
        if (!picture.equals("")) {
            mImageView.setImageBitmap(BitmapFactory.decodeFile(picture));
            mImageView.setTag(picture);
        }
    }

    public File createImageFile() throws IOException {
        getFileLocation();
        String location = getLocationString();
        String filename = storage.createNewPicture(location);

        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                filename,
                ".jpg",
                storageDir
        );
        return image;
    }

    public void getFileLocation() {
        try {
            mLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location.  In some rare situations this can be nullllll...
                            if (location != null) {
                                //Logic to handle location object
                                Log.d("getFileLocation lat", Double.toString(location.getLatitude()));
                                Log.d("getFileLocation long", Double.toString(location.getLongitude()));
                                mLastLocation = location;
                            } else {
                                Log.d("getFileLocation", "LOCATION NULL");
                            }
                        }
                    });
        } catch (SecurityException e) {
            Log.e("getFileLocation", "ACCESS_COARSE_LOCATION NOT GRANTED");
        }
    }

    public String getLocationString() {
        String loc = "";

        if (mLastLocation != null) {
            loc += "_loc" +Double.toString(mLastLocation.getLatitude()) +"_" +Double.toString(mLastLocation.getLongitude()) +"_";
        }

        Log.d("getLocationString", loc);

        return loc;
    }
}
