package com.example.mydb;

import android.media.ExifInterface;
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

    public String createNewPicture(String location) {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

            String picFileName = "JPEG_" + timeStamp + "_";
            picFileName += location;

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

    public void populateGallery(Date minDate, Date maxDate, Double[] locations, String[] keywords) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        Date picDate;
        String picDateString;
        Double[] picLoc;
        Double locLat, locLong;

        int i = 0;
        Log.d("populateGallery dates", "[" +minDate +"]/[" +maxDate+"]");
        File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath(),
                "/Android/data/com.example.myapplication/files/Pictures");
        gallery = new ArrayList<String>();
        File[] fList = file.listFiles();
        if (fList != null) {
            for(File f: file.listFiles()) {
                picDateString = f.getPath().split("_")[1];
                picLoc = null;
                if (locations != null) {
                    try {
                        if (f.getPath().indexOf("_loc") > -1) {
                            locLat = Double.parseDouble(f.getPath().split("_loc")[1].split("_")[0]);
                            locLong = Double.parseDouble(f.getPath().split("_loc")[1].split("_")[1]);
                            picLoc = new Double[2];
                            picLoc[0] = locLat;
                            picLoc[1] = locLong;
                            Log.d("populateGallery loc", "locations:" +picLoc[0].toString() +'/' +picLoc[1].toString());
                        } else {
                            Log.d("populateGallery loc", "no photo location");
                        }
                    } catch (Exception e) {
                        Log.d("populateGallery loc", "photo location parse failed");
                        picLoc = null;
                    }

                }
//                if (i == 0) {
                Log.d("populateGallery", "min:[" + dateFormat.format(minDate) + "]  "
                        + "max:[" + dateFormat.format(maxDate) + "]  "
                        + "current:[" + picDateString + "]");
                try {
                    picDate = dateFormat.parse(picDateString);
                    if ( searchDate(picDate, minDate, maxDate) ) {
                        if ( locations == null || ( locations != null && picLoc != null && searchLocation(locations, picLoc) ))
                            if (searchKeywords(keywords, f.getPath())) {
                                gallery.add(f.getPath());
                            }
                    }
                } catch (ParseException ex) {
                    Log.e("populateGallery", "DATE PARSE FAILED: " +f.getPath());
                }
//                }
                i++;
            }
        }
        Log.d("populateGallery", "finalCount" +Integer.toString(gallery.size()));

        if (gallery.size() > 0) {
            pictureIndex = gallery.size() - 1;
            picture = gallery.get(pictureIndex);
        }
    }

    public boolean searchDate(Date picDate, Date minDate, Date maxDate) {
        if ( minDate.before(picDate) || minDate.equals(picDate) ) {
            if ( maxDate.after(picDate) || maxDate.equals(picDate) ) {
                return true;
            }
        }
        return false;
    }

    public boolean searchLocation(Double[] locationSearch, Double[] picLoc) {
        if (locationSearch == null) {
            return true;
        }
        else if (picLoc != null) {
            if    (picLoc[0] >= locationSearch[0] && picLoc[0] <= locationSearch[2]) {
                if (picLoc[1] >= locationSearch[1] && picLoc[1] <= locationSearch[3]) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean searchKeywords(String[] keywords, String picPath) {
        if (keywords == null || keywords.length == 0) {
            return true;
        } else {
            String imageDesc = getKeywords(picPath);
            if (imageDesc != null && !imageDesc.equals("")) {
                for (int i = 0; i < keywords.length; i++) {
                    if (imageDesc.contains(keywords[i])) {
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
