package com.example.predatorx21.cebsmartmeter.utilities;

import android.util.Log;

import com.example.predatorx21.cebsmartmeter.dashboard.DashboardActivity;
import com.example.predatorx21.cebsmartmeter.db.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ThresholdSetup {

    private String thresholdType;
    private String thresholdValue;
    private String thresholdStatus;
    private String thresholdNotification;
    private double thresholdDetails[];

    public ThresholdSetup() {

        String query="SELECT * FROM Meter WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL +"'";
        ResultSet thresholdResult= DB.searchDB(query);
        try {
            if(thresholdResult.next()){
                thresholdType=thresholdResult.getString("ThresholdType");
                thresholdValue=thresholdResult.getString("ThresholdValue");
                thresholdStatus=thresholdResult.getString("ThresholdStatus");
                thresholdNotification=thresholdResult.getString("ThresholdNotifi");
            }
        } catch (SQLException e) {
            Log.e("ThresholdError","threshold Damn it");
        }

        thresholdDetails=new double[4];

        if(thresholdStatus.equals("1")){
            if(thresholdType.equals("Daily")){
                setupDailyDetail();
            }else if(thresholdType.equals("Monthly")){
                setupMonthlyDetail();
            }else{
                setCustomDetail();
            }
        }else{

        }
    }

    private void setCustomDetail() {

    }
    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void setupMonthlyDetail() {

        String dailyQuery="SELECT * FROM MeterReading WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY TIME DESC";
        ResultSet resultSet=DB.searchDB(dailyQuery);
        double initialReading=0.0,lastReading=0,unitsUpToNow=0,usedPercentage=0;

        try {

            int i=0;

            while (resultSet.next()){

                if(i==0) initialReading=Double.parseDouble(resultSet.getString("kWh"));
                i++;
                String date=resultSet.getString("TIME");
                String timeArray[]=new DateTrigger(date).getTime();
                String dateArray[]=new DateTrigger(date).getDate();

                //every month started from 20.
                if(timeArray[0].equals("00") && timeArray[1].equals("00") && dateArray[2].equals("20") ){

                    lastReading=Double.parseDouble(resultSet.getString("kWh"));
                    break;

                }
            }

        }catch (Exception e){

            Log.e("MonthError",e.getMessage());

        }

        unitsUpToNow=initialReading-lastReading;
        usedPercentage=(unitsUpToNow/Double.parseDouble(thresholdValue))*100;

        Log.d("usedPercentage",unitsUpToNow+"");
        Log.d("usedPercentage",initialReading+"");
        Log.d("usedPercentage",lastReading+"");

        thresholdDetails[0]=usedPercentage;
        thresholdDetails[1]=100-usedPercentage;
        thresholdDetails[2]=unitsUpToNow;
        thresholdDetails[3]=Double.parseDouble(thresholdValue)-unitsUpToNow;
    }


    //-------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
    //completed
    private void setupDailyDetail(){

        String dailyQuery="SELECT * FROM MeterReading WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY TIME DESC";
        ResultSet resultSet=DB.searchDB(dailyQuery);
        double initialReading=0.0,lastReading=90,unitsUpToNow=0,usedPercentage=0;

        try {

            int i=0;

            while (resultSet.next()){

                if(i==0) initialReading=Double.parseDouble(resultSet.getString("kWh"));
                i++;

                String date=resultSet.getString("TIME");
                String timeArray[]=new DateTrigger(date).getTime();
                String time=timeArray[0];

                //every month started from 20.
                if(timeArray[0].equals("00") && timeArray[1].equals("00")){
                    lastReading=Double.parseDouble(resultSet.getString("kWh"));
                    break;
                }

            }

        }catch (Exception e){

            Log.e("MonthError",e.getMessage());

        }

        unitsUpToNow=initialReading-lastReading;

        Log.d("usedPercentage",unitsUpToNow+"");
        Log.d("usedPercentage",initialReading+"");
        Log.d("usedPercentage",lastReading+"");

        usedPercentage=(unitsUpToNow/Double.parseDouble(thresholdValue))*100;

        thresholdDetails[0]=usedPercentage;
        thresholdDetails[1]=100-usedPercentage;
        thresholdDetails[2]=unitsUpToNow;
        thresholdDetails[3]=Double.parseDouble(thresholdValue)-unitsUpToNow;

    }

    //--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------

    public String getThresholdType() {
        return thresholdType;
    }

    public String getThresholdValue() {
        return thresholdValue;
    }

    public String getThresholdStatus() {
        return thresholdStatus;
    }

    public String getThresholdNotification() {
        return thresholdNotification;
    }

    public double[] getThresholdDetails() {
        return thresholdDetails;
    }

}
