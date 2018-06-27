package com.example.mypresenter;

import android.view.View;
import android.widget.EditText;

import com.example.mydb.IDataStore;
import com.example.mydb.FileSystemDataStore;

import java.util.ArrayList;

public class MultiPresenter {

    ArrayList<String> mPaths;
    ArrayList<String> mSelectedPaths;
    IDataStore storage;

    public MultiPresenter() {
        storage = new FileSystemDataStore();
        storage.populateGallery(null);
    }

    public ArrayList<String> getPictures(){
        return storage.getPictures();
    }

    public boolean save(String keywords, ArrayList<String> pictures) {
        if (keywords.length() > 0) {
            return storage.saveMultiple(pictures, keywords);
        }

        return true;
    }

}
