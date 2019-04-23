package com.example.predatorx21.cebsmartmeter.utilities;

import android.util.Log;

import com.example.predatorx21.cebsmartmeter.dashboard.DashboardActivity;
import com.example.predatorx21.cebsmartmeter.db.DB;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;

import java.sql.ResultSet;
import java.util.ArrayList;

public class GraphData {


    private ArrayList<Entry> monthlyUnits;
    private ArrayList<BarEntry> monthlyCharges;

    private ArrayList<Entry> dailyUnits;
    private ArrayList<BarEntry> dailyCharges;

    private String []xDLabels;
    private String []xMLabels;

    public GraphData() {
        setUpDailyDetails();
        setUpMonthlyDetail();
    }

    //getters.
    public ArrayList<Entry> getMonthlyUnits() {
        return monthlyUnits;
    }

    public ArrayList<BarEntry> getMonthlyCharges() {
        return monthlyCharges;
    }

    public ArrayList<Entry> getDailyUnits() { return dailyUnits; }

    public ArrayList<BarEntry> getDailyCharges() {
        return dailyCharges;
    }

    public String[] getxDLabels() {
        return xDLabels;
    }

    public String[] getxMLabels() { return xMLabels; }

    public void setUpMonthlyDetail(){

        //initilizations
        monthlyUnits=new ArrayList<Entry>();
        monthlyCharges=new ArrayList<BarEntry>();
        double tempArray[]=new double[]{0,0,0,0,0,0};
        xMLabels=new String[5];

        //SQL PART
        String monthlyQuery="SELECT * FROM MonthlyConsumptionValidateTable WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY Month DESC";
        ResultSet resultSet=DB.searchDB(monthlyQuery);

        //INITIALIZATIONS
        double unitsUpToNow=0,chargeUpToNow[]={0,0,0,0,0,0};

        try {

            int i=0;

            //for get LAST 5 READINGS.
            while (resultSet.next() && i!=6){

                //arrange XLabels.
                String date[]=resultSet.getString("Month").split("-");

                if(i<5)
                    xMLabels[i]=date[0]+"-"+new DateTrigger().getShortMonth(date[1]);

                //get the values of the db
                tempArray[i]=Double.parseDouble(resultSet.getString("kWh"));

                if(i!=0){

                    unitsUpToNow=tempArray[i-1]-tempArray[i];
                    chargeUpToNow=ConsumptionCharge.UsageInCharge(unitsUpToNow);

                    monthlyUnits.add(new Entry((i-1), (float) unitsUpToNow));
                    monthlyCharges.add(new BarEntry((i-1), (float) chargeUpToNow[2]));

                }
                i++;
            }

        }catch (Exception e){
            Log.e("GRAPH",e.getMessage());
        }

    }

    public void setUpDailyDetails(){

        //initilizations
        dailyUnits=new ArrayList<Entry>();
        dailyCharges=new ArrayList<BarEntry>();
        double tempArray[]=new double[]{0,0,0,0,0,0};
        xDLabels=new String[5];

        //SQL PART
        String dailyQuery="SELECT * FROM DailyEnergyConsumption WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY Date DESC";
        ResultSet resultSet=DB.searchDB(dailyQuery);

        //INITIALIZATIONS
        double unitsUpToNow=0,chargeUpToNow[]={0,0,0,0,0,0};

        try {

            int i=0;

            //for get LAST 5 READINGS.
            while (resultSet.next() && i!=6){

                //arrange XLabels.
                String date=resultSet.getString("Date");

                if(i<5)
                    xDLabels[i]=date;

                //get the values of the db
                tempArray[i]=Double.parseDouble(resultSet.getString("Consumption"));

                if(i!=0){

                    unitsUpToNow=tempArray[i-1]-tempArray[i];
                    chargeUpToNow=ConsumptionCharge.UsageInCharge(unitsUpToNow);

                    dailyUnits.add(new Entry((i-1), (float) unitsUpToNow));
                    dailyCharges.add(new BarEntry((i-1), (float) chargeUpToNow[2]));

                }
                i++;
            }

        }catch (Exception e){
            Log.e("GRAPH",e.getMessage());
        }
    }
}
