package com.example.mydb;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class IDataStoreKeywordTests {

    String mSearchNone, mSearchOne, mSearchMultiple, mSearchInvalid, mSearchBadFormatting;

    @Before
    public void setup() {
        mSearchNone = "";
        mSearchOne = "red";
        mSearchMultiple = "red,blue,green";
        mSearchInvalid = ",,,,,";
        mSearchBadFormatting = "red,,blue  green";
    }

    @Test public void searchByKeywords() {
        // Test empty search keywords
        String[] keywords = new String[0];

        // Test requires further refactoring
    }

    @After
    public void cleanUp() {
        mSearchNone = null;
        mSearchOne = null;
        mSearchMultiple = null;
        mSearchInvalid = null;
        mSearchBadFormatting = null;
    }

}
