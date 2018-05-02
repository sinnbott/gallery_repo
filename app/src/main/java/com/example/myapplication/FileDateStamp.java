package com.example.myapplication;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FileDateStamp {
    public String getFileName() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        return imageFileName;
    }
}
