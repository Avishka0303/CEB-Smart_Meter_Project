package com.example.predatorx21.cebsmartmeter.utilities;

import android.util.Log;

import com.example.predatorx21.cebsmartmeter.dashboard.DashboardActivity;
import com.example.predatorx21.cebsmartmeter.db.DB;

import java.sql.ResultSet;

public class OverviewUsage {

    private double monthlyDetail[];
    private double dailyDetail[];
    private double lastMonthConsumption;
    private double lastMonthReading;
    private double lastMonthCharge[];
    private String voltage;
    private String power;
    private String date;
    private String dateFormat2;

    public OverviewUsage(String type) {
        if(type.equals("Daily"))
            setupDailyDetail();
        else if(type.equals("Monthly"))
            setupMonthlyDetail();
        else
            lastMonthDetail();
    }

    public String getDate() { return date; }

    public double[] getMonthlyDetail() {
        return monthlyDetail;
    }

    public double[] getDailyDetail() {
        return dailyDetail;
    }

    public double getLastMonthConsumption() {
        return lastMonthConsumption;
    }

    public String getVoltage() { return voltage; }

    public String getPower() { return power; }

    public double getLastMonthReading() { return lastMonthReading; }

    public String getDateFormat2() { return dateFormat2; }

    public double[] getLastMonthCharge() { return lastMonthCharge; }

    //------------------------------------------------------------------------custom methods-------------------------------------------------------
    private void setupMonthlyDetail() {

        monthlyDetail=new double[2];

        String dailyQuery="SELECT * FROM MeterReading WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY TIME DESC";
        ResultSet resultSet=DB.searchDB(dailyQuery);
        double initialReading=0.0,lastReading=0,unitsUpToNow=0,chargeUpToNow[];

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
        chargeUpToNow=ConsumptionCharge.UsageInCharge(unitsUpToNow);
        lastMonthReading=initialReading;
        monthlyDetail[0]=unitsUpToNow;
        monthlyDetail[1]=chargeUpToNow[2];
    }

    //------------------------------------------------------------------daily usage calculator-----------------------------------------------------------------------------------
    private void setupDailyDetail(){

        dailyDetail=new double[2];

        String dailyQuery="SELECT * FROM MeterReading WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY TIME DESC";
        ResultSet resultSet=DB.searchDB(dailyQuery);
        double initialReading=0.0,lastReading=90,unitsUpToNow=0,chargeUpToNow[];

        try {
            int i=0;

            while (resultSet.next()){

                if(i==0){
                    initialReading=Double.parseDouble(resultSet.getString("kWh"));
                    voltage=resultSet.getString("V");
                    power=resultSet.getString("w");
                    date=resultSet.getString("TIME");
                }

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
        chargeUpToNow=ConsumptionCharge.UsageInCharge(unitsUpToNow);
        dailyDetail[0]=unitsUpToNow;
        dailyDetail[1]=chargeUpToNow[2];
    }

    //---------------------------------------------------------------------------------------------------------------------------------------------------------------

    private void lastMonthDetail() {

        String query="SELECT * FROM MonthlyConsumptionValidateTable WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY MONTH DESC";
        ResultSet resultSet=DB.searchDB(query);
        double initialReading=0.0,lastReading=0.0;

        try{
            int i=0;
            while (resultSet.next()){
                if(i==0){
                    initialReading=Double.parseDouble(resultSet.getString("kWh"));
                    dateFormat2=resultSet.getString("Month");}
                i++;
                if(i==2){
                    lastReading=Double.parseDouble(resultSet.getString("kWh"));
                }
            }
        }catch(Exception e){
            Log.e("MonthError",e.getMessage());
        }
        lastMonthReading=initialReading;
        lastMonthConsumption=initialReading-lastReading;
        lastMonthCharge=ConsumptionCharge.UsageInCharge(lastMonthConsumption);
    }

}
