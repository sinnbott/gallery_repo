package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.example.myapplication.FileDateStamp;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ACTIVITY_SEARCH = 0;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    static final int REQUEST_ACTIVITY_KEYWORDS = 2;

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
        mPhotoGallery = populateGallery(mStartDate, mEndDate, mLocationSearch, null);

        mLocationClient = LocationServices.getFusedLocationProviderClient(this);

        getFileLocation();

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

    public void clickPhoto(View view) {
        Intent keywordIntent = new Intent(this, KeywordsActivity.class);
        keywordIntent.putExtra("FILENAME", mCurrentPhotoPath);
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
                mPhotoGallery = populateGallery(mStartDate, mEndDate, mLocationSearch, null);
                Log.d("onActivityResult, size", Integer.toString(mPhotoGallery.size()));
                int size = mPhotoGallery.size();
                Log.d("createImageFile", "Picture Taken");
                mCurrentPhotoIndex = size - 1;

//                try {

//                    ExifInterface exifInterface = new ExifInterface(mCurrentPhotoPath);
//                    exifInterface.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, "TestVal");
//                    exifInterface.setAttribute(ExifInterface.TAG_GPS_LATITUDE, Double.toString(mLastLocation.getLatitude()));
//                    exifInterface.setAttribute(ExifInterface.TAG_GPS_LONGITUDE, Double.toString(mLastLocation.getLongitude()));
//                    exifInterface.saveAttributes();
//                } catch (Exception e) {
//                    Log.e("onActivityResult", "error setting exif data");
//                } //ImageDescription
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
                for (int i = 0; i < mKeywordSearch.length; i++) {
                    mKeywordSearch[i] = mKeywordSearch[i].trim();
                    Log.d("Keywords...", mKeywordSearch[i]);
                }

                Log.d("onActivityResult", "strings: [" +start +"]/[" +end +"]");
                Log.d("onActivityResult", "dates: [" +dateFormat.format(mStartDate) +"]/[" +dateFormat.format(mEndDate) +"]");

                populateGallery(mStartDate, mEndDate, mLocationSearch, mKeywordSearch);
                mCurrentPhotoIndex = (mPhotoGallery.size() > 0) ? mPhotoGallery.size() - 1 : 0;
                mCurrentPhotoPath = mPhotoGallery.get(mCurrentPhotoIndex);
                displayPhoto(mCurrentPhotoPath);
            }
        }

        if (requestCode == REQUEST_ACTIVITY_KEYWORDS) {
            if (resultCode == RESULT_OK) {
                Toast toast = Toast.makeText(getApplicationContext(), "Keywords saved...", Toast.LENGTH_SHORT);
                toast.show();
            }
        }
    }

    private void displayPhoto(String path) {
        String exif = "";
        Log.d("displayPhoto:", path);
        mImageView.setImageBitmap(BitmapFactory.decodeFile(path));
        mImageView.setTag(path);
    }

    private ArrayList<String> populateGallery(Date minDate, Date maxDate, Double[] locations, String[] keywords) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date photoDate;
        String photoDateString;
        Double[] photoLoc;
        Double locLat, locLong;

        int i = 0;
        Log.d("populateGallery dates", "[" +mStartDate +"]/[" +mEndDate +"]");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Android/data/com.example.myapplication/files/Pictures");
        mPhotoGallery = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for(File f: file.listFiles()) {
                photoDateString = f.getPath().split("_")[1];
                photoLoc = null;
                if (locations != null) {
                    try {
                        if (f.getPath().indexOf("_loc") > -1) {
                            locLat = Double.parseDouble(f.getPath().split("_loc")[1].split("_")[0]);
                            locLong = Double.parseDouble(f.getPath().split("_loc")[1].split("_")[1]);
                            photoLoc = new Double[2];
                            photoLoc[0] = locLat;
                            photoLoc[1] = locLong;
                            Log.d("populateGallery loc", "locations:" +photoLoc[0].toString() +'/' +photoLoc[1].toString());
                        } else {
                            Log.d("populateGallery loc", "no photo location");
                        }
                    } catch (Exception e) {
                        Log.d("populateGallery loc", "photo location parse failed");
                        photoLoc = null;
                    }

                }
//                if (i == 0) {
                    Log.d("populateGallery", "min:[" + dateFormat.format(minDate) + "]  "
                            + "max:[" + dateFormat.format(maxDate) + "]  "
                            + "current:[" + photoDateString + "]");
                    try {
                        photoDate = dateFormat.parse(photoDateString);
                        if ( dateFits(photoDate, minDate, maxDate) ) {
                            if ( locations == null || ( locations != null && photoLoc != null && locationFits(locations, photoLoc) ))
                                if (keywordFits(keywords, f.getPath())) {
                                    mPhotoGallery.add(f.getPath());
                            }
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

    public File createImageFile() throws IOException {
        getFileLocation();
        String imageFileName = new FileDateStamp().getFileName();
        imageFileName += getLocationString();

//        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
//        String imageFileName = "JPEG_" + timeStamp + "_";
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

    public boolean dateFits(Date photoDate, Date minDate, Date maxDate) {
        if ( minDate.before(photoDate) || minDate.equals(photoDate) ) {
            if ( maxDate.after(photoDate) || maxDate.equals(photoDate) ) {
                return true;
            }
        }
        return false;
    }

    public boolean locationFits(Double[] locationSearch, Double[] photoLocation) {
        if (locationSearch == null) {
            return true;
        }
        else if (photoLocation != null) {
            if    (photoLocation[0] >= locationSearch[2] && photoLocation[0] <= locationSearch[0]) {
                if (photoLocation[1] >= locationSearch[1] && photoLocation[1] <= locationSearch[3]) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean keywordFits(String[] keywords, String photoPath) {
        if (keywords == null || keywords.length == 0) {
            return true;
        } else {
            try {
                ExifInterface exifInterface = new ExifInterface(photoPath);
                String imageDesc = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
                Log.e("keywordFits desc", imageDesc);
                for (int i = 0; i < keywords.length; i++) {
                    if (imageDesc.contains(keywords[i])) {
                        return true;
                    }
                }
            } catch (Exception e) {
                Log.e("keywordFits", "error getting image exif data");
                return false;
            }
        }

        return false;
    }

}
