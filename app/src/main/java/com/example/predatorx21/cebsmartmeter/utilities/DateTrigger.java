package com.example.predatorx21.cebsmartmeter.utilities;

import android.util.Log;

public class DateTrigger {

    private String date[];
    private String time[];
    private String day;
    private String month;
    private final String DAY[]={"Sunday","Monday","Tuesday","Wednesday","Thursday","Friday","Saturday"};
    private final String MONTH[]={"January","February","March","April","May","June","July","August","September","October","November","December"};


    public DateTrigger(String dateAndTime) {
        String s1[]=dateAndTime.split(" ");
        date=s1[0].split("-");
        time=s1[1].split(":");
    }

    //date[] ->>>>>>  date[0] -year    date[1] -month  date[2] -date
    public String[] getDate() {
        return date;
    }

    //time  ->>>>>>>  time[0] hours   time[1] -minute  time[2] -seconds
    public String[] getTime() {
        return time;
    }

    public String getDay() {
        return day;
    }

    public String getMonth() {
        switch (date[1]){
            case "01":return MONTH[0];
            case "02":return MONTH[1];
            case "03":return MONTH[2];
            case "04":return MONTH[3];
            case "05":return MONTH[4];
            case "06":return MONTH[5];
            case "07":return MONTH[6];
            case "08":return MONTH[7];
            case "09":return MONTH[8];
            case "10":return MONTH[9];
            case "11":return MONTH[10];
            case "12":return MONTH[11];
        }
        return month;
    }
}
