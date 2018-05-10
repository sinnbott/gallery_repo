package com.example.mydb;

import java.util.Date;

public interface IDataStore {
    String getPicture();
    boolean savePicture(String _picture, String keywords);
    String createNewPicture(String location);
    void nextPicture();
    void previousPicture();
    void populateGallery(Date minDate, Date maxDate, Double[] locations, String[] keywords);
    String getKeywords(String _picture);
    boolean searchDate(Date picDate, Date minDate, Date maxDate);
    boolean searchLocation(Double[] locationSearch, Double[] picLoc);
    boolean searchKeywords(String[] keywords, String picPath);
}
