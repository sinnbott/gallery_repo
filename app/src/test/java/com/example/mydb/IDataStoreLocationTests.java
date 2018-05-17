package com.example.mydb;

import android.location.Location;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.Date;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

@RunWith(MockitoJUnitRunner.class)
public class IDataStoreLocationTests {


    IDataStore storage;

    String fileMin, fileMax, fileMid, fileLocation, fileLatOutside, fileLongOutside, fileNone;

    @Mock
    Location locMin, locMax, locMid;

    Location locNull;

    String searchEmpty, searchInvalid, searchMax, searchMin, searchStartMid, searchEndMid;


    @Before
    public void setPictureFilenames() {

        storage = new FileSystemDataStore();

        locMin = Mockito.mock(Location.class);
        Mockito.when(locMin.getLatitude())
                .thenReturn(90.00000d);
        Mockito.when(locMin.getLongitude())
                .thenReturn(-180.00000d);

        locMax = Mockito.mock(Location.class);
        Mockito.when(locMax.getLatitude())
                .thenReturn(-90.00000d);
        Mockito.when(locMax.getLongitude())
                .thenReturn(180.00000d);

        locMid = Mockito.mock(Location.class);
        Mockito.when(locMid.getLatitude())
                .thenReturn(-15.87982d);
        Mockito.when(locMid.getLongitude())
                .thenReturn(21.53648d);

        locNull = null;

        fileMin = "__loc"
                +Double.toString(90.00000d)
                +"_"
                +Double.toString(-180.00000d);

        fileMax = "__loc"
                +Double.toString(-90.00000d)
                +"_"
                +Double.toString(180.00000d);

        fileMid = "__loc"
                +Double.toString(-15.87982d)
                +"_"
                +Double.toString(21.53648d);

        fileLatOutside = "__loc"
                +Double.toString(-55.87982d)
                +"_"
                +Double.toString(21.53648d);

        fileLongOutside = "__loc"
                +Double.toString(-15.87982d)
                +"_"
                +Double.toString(121.53648d);

        fileLocation = "_loc";

        searchEmpty = "";
        searchInvalid = "asf029hdg/2-84234ga";
        searchMax = "-90/180";
        searchMin  = "90/-180";
        searchStartMid = "40.48948/-32.19784";
        searchEndMid = "-51.78917/65.48108";
    }

    @Test public void pictureLocationSet() {
        assertTrue(storage.createNewPicture(new Date(), locMin).contains(fileMin));
    }

    @Test public void pictureLocMaxSet() {
        String test = storage.createNewPicture(new Date(), locMax);
//        assertTrue(storage.createNewPicture(locMax).contains(fileMax));
        assertTrue(test.contains(fileMax));
    }

    @Test public void pictureLocMidSet() {
        assertTrue(storage.createNewPicture(new Date(), locMid).contains(fileMid));
    }

    @Test public void pictureLocNullNotSet() {
        assertFalse(storage.createNewPicture(new Date(), locNull).contains(fileLocation));
    }

    @Test
    public void searchLocationMin() {
        storage.setSearchLocations(searchMin, searchMin);
        assertTrue(storage.searchLocation(fileMin));
        assertFalse(storage.searchLocation(fileMax));
        assertFalse(storage.searchLocation(fileMid));
        assertFalse(storage.searchLocation(fileNone));
        assertFalse(storage.searchLocation(fileLatOutside));
        assertFalse(storage.searchLocation(fileLongOutside));
    }

    @Test
    public void searchLocationMax() {
        storage.setSearchLocations(searchMax, searchMax);
        assertFalse(storage.searchLocation(fileMin));
        assertTrue(storage.searchLocation(fileMax));
        assertFalse(storage.searchLocation(fileMid));
        assertFalse(storage.searchLocation(fileNone));
        assertFalse(storage.searchLocation(fileLatOutside));
        assertFalse(storage.searchLocation(fileLongOutside));
    }

    @Test
    public void searchLocationFull() {
        storage.setSearchLocations(searchMin, searchMax);
        assertTrue(storage.searchLocation(fileMin));
        assertTrue(storage.searchLocation(fileMax));
        assertTrue(storage.searchLocation(fileMid));
        assertFalse(storage.searchLocation(fileNone));
        assertTrue(storage.searchLocation(fileLatOutside));
        assertTrue(storage.searchLocation(fileLongOutside));
    }

    @Test public void searchLocationMid() {
        storage.setSearchLocations(searchStartMid, searchEndMid);
        assertFalse(storage.searchLocation(fileMin));
        assertFalse(storage.searchLocation(fileMax));
        assertTrue(storage.searchLocation(fileMid));
        assertFalse(storage.searchLocation(fileNone));
        assertFalse(storage.searchLocation(fileLatOutside));
        assertFalse(storage.searchLocation(fileLongOutside));
    }

    @Test public void searchLocationEmpty() {
        storage.setSearchLocations(searchEmpty, searchEmpty);
        assertTrue(storage.searchLocation(fileMin));
        assertTrue(storage.searchLocation(fileMax));
        assertTrue(storage.searchLocation(fileMid));
        assertTrue(storage.searchLocation(fileNone));
        assertTrue(storage.searchLocation(fileLatOutside));
        assertTrue(storage.searchLocation(fileLongOutside));
    }

    @Test public void searchLocationInvalid() {
        storage.setSearchLocations(searchInvalid, searchInvalid);
        assertTrue(storage.searchLocation(fileMin));
        assertTrue(storage.searchLocation(fileMax));
        assertTrue(storage.searchLocation(fileMid));
        assertTrue(storage.searchLocation(fileNone));
        assertTrue(storage.searchLocation(fileLatOutside));
        assertTrue(storage.searchLocation(fileLongOutside));
    }

    @After
    public void cleanUp() {
        storage = null;
        locMin = null;
        locMax = null;
        locMid = null;

        searchInvalid = null;
        searchEmpty = null;
        searchMin = null;
        searchMax = null;
        searchStartMid = null;
        searchEndMid = null;


        fileLatOutside = null;
        fileLongOutside = null;
        fileMin = null;
        fileMax = null;
        fileLocation = null;
        fileMid = null;
        fileNone = null;
    }
}
