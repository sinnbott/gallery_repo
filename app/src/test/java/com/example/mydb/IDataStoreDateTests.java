package com.example.mydb;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class IDataStoreDateTests {

    IDataStore storage;
    String dateStringMid, dateStringMin, dateStringMax, dateStringToday;
    String picPathMid, picPathMin, picPathMax, picPathNone;
    String dateSearchMid, dateSearchMin, dateSearchMax, dateSearchNone, dateSearchInvalid;

    @Before
    public void setup() {
        storage = new FileSystemDataStore();

        SimpleDateFormat dateStamp = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat dateEntry = new SimpleDateFormat("MM/dd/YYYY");
        dateStringMid = "20180512";
        dateStringMin = "19691231";
        dateStringMax = "2922789940816";
        dateStringToday = dateStamp.format(new Date());

        dateSearchMid = "05/12/2018";
        dateSearchMin = "12/31/1969";
        dateSearchMax = "06/11/30145";
        dateSearchNone = "";
        dateSearchInvalid = "98df4gs/s4d98bc/eds6g48";

        try {
            picPathMid = storage.createNewPicture(dateStamp.parse(dateStringMid), null);
        } catch(Exception e) {
            picPathMid = "";
        }
        picPathMin = storage.createNewPicture(new Date(0), null);
        picPathMax = storage.createNewPicture(new Date(Long.MAX_VALUE), null);
        picPathNone = storage.createNewPicture(null, null);
    }

    @Test
    public void pictureCorrectDatestamp() {
        assertTrue(picPathMid.contains(dateStringMid));
        assertTrue(picPathMin.contains(dateStringMin));
        assertTrue(picPathMax.contains(dateStringMax));
        assertTrue(picPathNone.contains(dateStringToday));
    }

    @Test
    public void searchDateMin() {
        storage.setSearchDates(dateSearchMin, dateSearchMin);
        assertTrue(storage.searchDate(picPathMin));
        assertFalse(storage.searchDate(picPathMid));
        assertFalse(storage.searchDate(picPathMax));
        assertFalse(storage.searchDate(picPathNone));
    }

    @Test
    public void searchDateMax() {
        storage.setSearchDates(dateSearchMax, dateSearchMax);
        assertFalse(storage.searchDate(picPathMin));
        assertFalse(storage.searchDate(picPathMid));
        assertTrue(storage.searchDate(picPathMax));
        assertFalse(storage.searchDate(picPathNone));
    }

    @Test
    public void searchDateMid() {
        storage.setSearchDates(dateSearchMid, dateSearchMid);
        assertFalse(storage.searchDate(picPathMin));
        assertTrue(storage.searchDate(picPathMid));
        assertFalse(storage.searchDate(picPathMax));
        assertFalse(storage.searchDate(picPathNone));
    }

    @Test
    public void searchDateInvalid() {
        storage.setSearchDates(dateSearchInvalid, dateSearchInvalid);
        assertTrue(storage.searchDate(picPathMin));
        assertTrue(storage.searchDate(picPathMid));
        assertTrue(storage.searchDate(picPathMax));
        assertTrue(storage.searchDate(picPathNone));
    }

    @Test
    public void searchDateNone() {
        storage.setSearchDates(dateSearchNone, dateSearchNone);
        assertTrue(storage.searchDate(picPathMin));
        assertTrue(storage.searchDate(picPathMid));
        assertTrue(storage.searchDate(picPathMax));
        assertTrue(storage.searchDate(picPathNone));
    }

    @After
    public void cleanUp() {
        storage = null;

        dateStringMid = null;
        dateStringMin = null;
        dateStringMax = null;
        dateStringToday = null;

        picPathMid = null;
        picPathMin = null;
        picPathMax = null;
        picPathNone = null;

        dateSearchMid = null;
        dateSearchMin = null;
        dateSearchMax = null;
        dateSearchNone = null;
        dateSearchInvalid = null;
    }

}
