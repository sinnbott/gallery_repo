package com.example.mydb;

import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileSystemDataStore implements IDataStore {
    private int pictureIndex;
    private String picture;
    private ArrayList<String> gallery = new ArrayList<String>();

    private Double mLocTop, mLocLeft, mLocBot, mLocRight;
    private String[] mKeywordSearch;
    private Date mStartDate, mEndDate;

    public String getPicture() {
        String picture = "";
        try {
            picture = gallery.get(pictureIndex);
            Log.d("getPicture", "total files: " +Double.toString(gallery.size()) +", current file: " +Integer.toString(pictureIndex));
            Log.d("getPicture", picture);
        } catch (Exception e) {
            Log.e("getPicture","no picture to display!");
        }
        return picture;
    }

    public void setSearchParams(Bundle extras) {
        String start = "";
        String end = "";
        String locStart = "";
        String locEnd = "";
        String keywords = "";

        if (extras != null) {
            start  = extras.getString("STARTDATE");
            end  = extras.getString("ENDDATE");
            locStart = extras.getString("LOCSTART");
            locEnd = extras.getString("LOCEND");
            keywords = extras.getString("KEYWORDS");
        }
        setSearchDates(start, end);
        setSearchLocations(locStart, locEnd);
        setSearchKeywords(keywords);
    }

    public void setSearchDates(String start, String end) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");

        try {
            mStartDate = dateFormat.parse(start);
        } catch (ParseException ex) {
            Log.e("search", "parse start date failed: [" +start +"]");
            Date tmpDate = new Date(0);
            String tmpDateStamp = dateFormat.format(tmpDate);
            try {
                mStartDate = dateFormat.parse(tmpDateStamp);
            } catch (Exception e) {
                mStartDate = new Date(0);
            }
        }

        try {
            mEndDate = dateFormat.parse(end);
        } catch (ParseException ex) {
            Log.e("search", "parse end date failed: [" +end +"]");
            mEndDate = new Date(Long.MAX_VALUE);
        }
    }

    public void setSearchLocations(String topLeft, String botRight) {
        topLeft = topLeft.replaceAll(" ", "");
        botRight = botRight.replaceAll(" ", "");

        try {
            String top = topLeft.split("/")[0];
            mLocTop = Double.parseDouble(top);
        } catch (Exception e) {
            mLocTop = null; // Double.MAX_VALUE;
        }
        try {
            String left = topLeft.split("/")[1];
            mLocLeft = Double.parseDouble(left);
        } catch (Exception e) {
            mLocLeft = null; // -Double.MAX_VALUE;
        }
        try {
            String bot = botRight.split("/")[0];
            mLocBot = Double.parseDouble(bot);
        } catch (Exception e) {
            mLocBot = null; // Double.MAX_VALUE;
        }
        try {
            String right = botRight.split("/")[1];
            mLocRight = Double.parseDouble(right);
        } catch (Exception e) {
            mLocRight = null; // -Double.MAX_VALUE;
        }
    }

    public void setSearchKeywords(String keywords) {
        mKeywordSearch = keywords.split(",");
        if (mKeywordSearch.length == 1 && mKeywordSearch[0].equals(""))
            mKeywordSearch = null;

        if (mKeywordSearch != null) {
            for (int i = 0; i < mKeywordSearch.length; i++) {
                mKeywordSearch[i] = mKeywordSearch[i].trim();
            }
        }
    }

    public String createNewPicture(Date now, Location location) {
        String timeStamp = "";
        String loc = "";

        if (now != null) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(now);
        } else {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }

        String picFileName = "JPEG_" + timeStamp + "_";

        try {
            if (location != null) {
                loc = "_loc" +Double.toString(location.getLatitude()) +"_" +Double.toString(location.getLongitude()) +"_";
            }
        } catch(Exception e) {
            loc = "";
        }

        picFileName += loc;

        return picFileName;
    }

    public boolean savePicture(String _picture, String keywords) {
        try {
            ExifInterface exifInterface = new ExifInterface(_picture);
            exifInterface.setAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION, keywords);
            exifInterface.saveAttributes();
            return true;
        } catch (Exception e) {
            Log.e("save", "error saving exif image description");
            return false;
        }
    }

    public String getKeywords(String _picture) {
        String keywords = "";

        try {
            ExifInterface exifInterface = new ExifInterface(_picture);
            keywords = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
        } catch (Exception e) {
            Log.e("onCreate", "error getting exif data");
        } //ImageDescription

        return keywords;
    }

    public void nextPicture() {
        if (pictureIndex < (gallery.size() - 1))
            ++pictureIndex;
    }

    public void previousPicture() {
        if (pictureIndex > 0)
            --pictureIndex;
    }

    public void populateGallery(Bundle params) {

//        Date picDate;
//        String picDateString;
//        Double[] picLoc;
//        Double locLat, locLong;
        String picPath = "";
        setSearchParams(params);
//
//        int i = 0;
//        Log.d("populateGallery dates", "[" +minDate +"]/[" +maxDate+"]");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Android/data/com.example.myapplication/files/Pictures");
        gallery = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for(File f: file.listFiles()) {
                picPath = f.getPath();
//                picDateString = f.getPath().split("_")[1];

//                Log.d("populateGallery", "min:[" + dateFormat.format(minDate) + "]  "
//                        + "max:[" + dateFormat.format(maxDate) + "]  "
//                        + "current:[" + picDateString + "]");
//                    picDate = dateFormat.parse(picDateString);
                    if ( searchDate(picPath) ) {
                        if ( searchLocation(picPath) )
                            if (searchKeywords(f.getPath())) {
                                gallery.add(f.getPath());
                            }
                    }
//                }
//                i++;
            }
        }
        Log.d("populateGallery", "finalCount" +Integer.toString(gallery.size()));

        if (gallery.size() > 0) {
            pictureIndex = gallery.size() - 1;
            picture = gallery.get(pictureIndex);
        }
    }

    public boolean searchDate(String picPath) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");

        String picDateString = picPath.split("_")[1];
        try {
            Date picDate = dateFormat.parse(picDateString);

            if ( mStartDate.before(picDate) || mStartDate.equals(picDate) ) {
                if ( mEndDate.after(picDate) || mEndDate.equals(picDate) ) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    public boolean searchLocation(String picPath) {
        Double picLat;
        Double picLong;

        if ( mLocTop == null &&
                mLocBot == null &&
                mLocLeft == null &&
                mLocRight == null ) {
            return true;
        } else {
            try {
                if (picPath.indexOf("_loc") > -1) {
                    picLat = Double.parseDouble(picPath.split("_loc")[1].split("_")[0]);
                    picLong = Double.parseDouble(picPath.split("_loc")[1].split("_")[1]);
                    Log.d("populateGallery loc", "locations:" +picLat.toString() +'/' +picLong.toString());

                    if ( (mLocBot != null && picLat >= mLocBot) && ( mLocTop != null && picLat <= mLocTop ) ) {
                        if ( ( mLocLeft != null && picLong >= mLocLeft ) && ( mLocRight != null && picLong <= mLocRight ) ) {
                            return true;
                        }
                    }

                } else {
                    return false;
                }
            } catch (Exception e) {
                return false;
            }

        }
//                if (i == 0) {
//        Log.d("populateGallery", "min:[" + dateFormat.format(minDate) + "]  "
//                + "max:[" + dateFormat.format(maxDate) + "]  "
//                + "current:[" + picDateString + "]");
//
//        if (locationSearch == null) {
//            return true;
//        }
//        else if (picLoc != null) {
//            if    (picLoc[0] >= locationSearch[0] && picLoc[0] <= locationSearch[2]) {
//                if (picLoc[1] >= locationSearch[1] && picLoc[1] <= locationSearch[3]) {
//                    return true;
//                }
//            }
//        }
        return false;
    }

    public boolean searchKeywords(String picPath) {
        if (mKeywordSearch == null || mKeywordSearch.length == 0) {
            return true;
        } else {
            String imageDesc = getKeywords(picPath);
            if (imageDesc != null && !imageDesc.equals("")) {
                for (int i = 0; i < mKeywordSearch.length; i++) {
                    if (imageDesc.contains(mKeywordSearch[i])) {
                        return true;
                    }
                }
            }

//            try {
//                ExifInterface exifInterface = new ExifInterface(picPath);
////                String imageDesc = exifInterface.getAttribute(ExifInterface.TAG_IMAGE_DESCRIPTION);
//                for (int i = 0; i < keywords.length; i++) {
//                    if (imageDesc.contains(keywords[i])) {
//                        return true;
//                    }
//                }
//            } catch (Exception e) {
//                return false;
//            }
        }

        return false;
    }

    public void filter() {

    }
}
