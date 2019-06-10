package com.example.predatorx21.cebsmartmeter.utilities;

import android.util.Log;

import com.example.predatorx21.cebsmartmeter.dashboard.DashboardActivity;
import com.example.predatorx21.cebsmartmeter.db.DB;
import com.example.predatorx21.cebsmartmeter.machine_learning.LinearRegressionClassifier;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;

public class PredictionUnit {

    private double nextMonthValue,nextDayValue,nextMonthConsumption,nextDayConsumption,nextDayCharge,nextMonthCharge;

    public PredictionUnit() {
        try {
            monthlyPrediction();
        } catch (SQLException e) {
            Log.d("Prediction","Monthly Prediction is in danger");
        }
        try {
            dailyPredictions();
        } catch (SQLException e) {
            Log.d("Prediction","Daily Prediction is in danger");
        }
    }

    public double getNextMonthValue() {
        return nextMonthValue;
    }

    public double getNextDayValue() {
        return nextDayValue;
    }

    public double getNextMonthConsumption() {
        return nextMonthConsumption;
    }

    public double getNextDayConsumption() {
        return nextDayConsumption;
    }

    public double getNextDayCharge() { return nextDayCharge; }

    public double getNextMonthCharge() { return nextMonthCharge; }

    //----------------------------------------------------------------------------------- ASSOCIATIVE METHODS ------------------------------------------------------------------------
    private void monthlyPrediction() throws SQLException {

        String queryMP="SELECT * FROM MonthlyConsumptionValidateTable  WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY kWh DESC";
        ResultSet monthlyPredictionResult=DB.searchDB(queryMP);

        ArrayList<Float> monthlyX=new ArrayList<>();
        ArrayList<Float> monthlyY=new ArrayList<>();

        float counter=1;
        while (monthlyPredictionResult.next()) {
            monthlyX.add((float) counter);
            monthlyY.add(monthlyPredictionResult.getFloat("kWh"));
            counter++;
        }

        Collections.reverse(monthlyY);

        LinearRegressionClassifier linearRegressionClassifier=new LinearRegressionClassifier(monthlyX,monthlyY);
        nextMonthValue=linearRegressionClassifier.predictValue(counter);
        nextMonthConsumption=nextMonthValue-monthlyY.get((int) (counter-2));
        nextMonthCharge=ConsumptionCharge.UsageInCharge(nextMonthConsumption)[2];

        Log.d("Prediction",monthlyY+" ---Month--- "+monthlyX+" predict "+nextMonthConsumption+" "+nextMonthCharge);

    }

    private void dailyPredictions() throws SQLException {

        String queryDP="SELECT * FROM DailyEnergyConsumption WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL +"'";
        ResultSet dailyPredictionResult= DB.searchDB(queryDP);

        ArrayList<Float> dailyX=new ArrayList<>();
        ArrayList<Float> dailyY=new ArrayList<>();

        float counter=1;
        while(dailyPredictionResult.next()){
            dailyX.add((float) counter);
            dailyY.add(dailyPredictionResult.getFloat("Consumption"));
            counter++;
        }

        //daily predition with linear regressions
        LinearRegressionClassifier linearRegressionClassifier=new LinearRegressionClassifier(dailyX,dailyY);
        nextDayValue=linearRegressionClassifier.predictValue(counter);
        nextDayConsumption=nextDayValue-dailyY.get((int) (counter-2));
        nextDayCharge=ConsumptionCharge.UsageInCharge(nextDayConsumption)[0];

        Log.d("Prediction",dailyY+" --- Day --- "+dailyX+" predic "+nextDayConsumption+" "+nextDayCharge);

    }
    //----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
