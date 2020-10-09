package com.developpeuseoc.go4lunch.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DatesAndHours {

    public static void getTodayDate(){
        Calendar cal = Calendar.getInstance();
        Date currentDate = cal.getTime();
        @SuppressLint("SimpleDateFormat")
        DateFormat date = new SimpleDateFormat("dd-MM-yyy z");
        String dayDate = date.format(currentDate);
        Log.d("TestDate", dayDate);
    }

    public static int getCurrentTime() {
        Calendar calendar = Calendar.getInstance();
        Date currentLocalTime = calendar.getTime();
        @SuppressLint("SimpleDateFormat")
        DateFormat date = new SimpleDateFormat("HHmm");
        String localTime = date.format(currentLocalTime);
        Log.d("TestHour", localTime);
        return Integer.parseInt(localTime);
    }

    // convert string to hours
    public static String convertStringToHours(String hour){
        String hour1 = hour.substring(0,2);
        String hour2 = hour.substring(2,4);
        return hour1 + ":" + hour2;
    }

    public static String convertDateToHour(Date date){
        @SuppressLint("SimpleDateFormat") DateFormat dfTime = new SimpleDateFormat("HH:mm");
        return dfTime.format(date);
    }
}
