package com.example.mydb;

import android.location.Location;
import android.os.Bundle;

import java.util.Date;

public interface IDataStore {
    String getPicture();
    boolean savePicture(String _picture, String keywords);
    String createNewPicture(Date now, Location location);
    void nextPicture();
    void previousPicture();
    void populateGallery(Bundle params);
    void setSearchParams(Bundle params);
    void setSearchLocations(String topLeft, String botRight);
    void setSearchKeywords(String keywords);
    void setSearchDates(String start, String end);
    String getKeywords(String _picture);
    boolean searchDate(String picPath);
    boolean searchLocation(String picPath);
    boolean searchKeywords(String picPath);
}
