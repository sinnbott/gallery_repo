package com.example.mydb;

import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class IDataStoreTests {

    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void pictureIsDatestamped() {
        IDataStore storage = new FileSystemDataStore();
        String filename = storage.createNewPicture("");

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String testDateString = dateFormat.format(new Date());
        assertTrue(filename.contains(testDateString));
    }

    @Test
    public void searchByDate() {
        IDataStore storage = new FileSystemDataStore();

        Date startSearch = new Date(0);
        Date endSearch = new Date(Long.MAX_VALUE);
        Date pictureDate = new Date();

        // Test today's date in range between min and max
        assertTrue(storage.searchDate(pictureDate, startSearch, endSearch));

        // Test epoch before yesterday and today
        Calendar yesterday = Calendar.getInstance();
        yesterday.add(Calendar.DATE, -1);
        pictureDate = new Date(0);
        startSearch = new Date(yesterday.getTimeInMillis());
        endSearch = new Date();
        assertFalse(storage.searchDate(pictureDate, startSearch, endSearch));

        // Test tomorrow after epoch and today
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        Calendar today = Calendar.getInstance();
        pictureDate = new Date(tomorrow.getTimeInMillis());
        startSearch = new Date(0);
        endSearch = new Date(today.getTimeInMillis());
        assertFalse(storage.searchDate(pictureDate, startSearch, endSearch));
    }

    @Test
    public void searchByLocation() {
        IDataStore storage = new FileSystemDataStore();

        Double[] locationSearch = new Double[4];
        locationSearch[0] = -300.0;
        locationSearch[1] = -300.0;
        locationSearch[2] = 300.0;
        locationSearch[3] = 300.0;

        Double[] pictureLocation = new Double[2];
        pictureLocation[0] = 55.5;
        pictureLocation[1] = -180.87891;

        // Test in bounds
        assertTrue(storage.searchLocation(locationSearch, pictureLocation));

        // Test latitude out of bounds
        pictureLocation[0] = -439.0;
        pictureLocation[1] = 23.4325;
        assertFalse(storage.searchLocation(locationSearch, pictureLocation));

        // Test longitude out of bounds
        pictureLocation[0] = 0.0;
        pictureLocation[1] = 4548.253;
        assertFalse(storage.searchLocation(locationSearch, pictureLocation));
    }

    @Test public void searchByKeywords() {
        IDataStore storage = new FileSystemDataStore();

        // Test empty search keywords
        String[] keywords = new String[0];

        // Test requires further refactoring
    }
}
