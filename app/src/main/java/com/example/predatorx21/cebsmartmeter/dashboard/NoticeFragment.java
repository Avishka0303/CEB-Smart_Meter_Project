package com.example.predatorx21.cebsmartmeter.dashboard;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.utilities.DateTrigger;
import com.example.predatorx21.cebsmartmeter.utilities.OverviewUsage;
import com.example.predatorx21.cebsmartmeter.utilities.PredictionUnit;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Date;

public class NoticeFragment extends Fragment {

    //TEXT VIEWS
    private TextView mmonth;
    private TextView mreading;
    private TextView mconsumed;
    private TextView mcharge;
    private TextView mn_dReading;
    private TextView mn_mReading;
    private TextView mn_mdate;
    private TextView mn_ddate;
    private TextView mn_dcharge;
    private TextView mn_mcharge;

    private DecimalFormat decimalFormat;
    private PredictionUnit predictionUnit;

    public NoticeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_notice, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        mmonth=(TextView)getView().findViewById(R.id.month_txt);
        mreading=(TextView)getView().findViewById(R.id.reading_txt);
        mconsumed=(TextView)getView().findViewById(R.id.consumption_txt);
        mcharge=(TextView)getView().findViewById(R.id.charge_txt);

        mn_dReading=(TextView)getView().findViewById(R.id.next_dreading_txt);
        mn_mReading=(TextView)getView().findViewById(R.id.next_mreading_txt);
        mn_mdate=(TextView)getView().findViewById(R.id.date_next_month);
        mn_ddate = (TextView) getView().findViewById(R.id.date_tommorrow);
        mn_dcharge=(TextView)getView().findViewById(R.id.next_charge_txt);
        mn_mcharge=(TextView)getView().findViewById(R.id.next_mcharge_txt);

        decimalFormat=new DecimalFormat("##.##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        //INITIALIZE THE PREDICTION UNIT
        predictionUnit=new PredictionUnit();

        setLastMonthDetail();
        setNextDayReading();
        setNextMonthReading();

    }

    private void setNextDayReading() {
        mn_ddate.setText(getNextDay());
        mn_dReading.setText(decimalFormat.format(predictionUnit.getNextDayValue())+" kWh");
        mn_dcharge.setText(decimalFormat.format(predictionUnit.getNextDayCharge())+" Rs");
    }

    private void setNextMonthReading(){
        mn_mdate.setText(getNextMonth());
        mn_mReading.setText(decimalFormat.format(predictionUnit.getNextMonthValue())+" kWh");
        mn_mcharge.setText(decimalFormat.format(predictionUnit.getNextMonthCharge())+" Rs");
    }

    //---------------------------------------------------------------------- TAKE LAST MONTH DETAILS FOR APP -------------------------------------------------
    private void setLastMonthDetail() {

        OverviewUsage ovu=new OverviewUsage("");
        String date[]=ovu.getDateFormat2().split("-");
        String dateShow=" 20 - "+new DateTrigger().getShortMonth(date[1])+" "+" 2019 ";
        mmonth.setText(dateShow);
        mreading.setText(decimalFormat.format(ovu.getLastMonthReading())+" kWh");
        mconsumed.setText(decimalFormat.format(ovu.getLastMonthConsumption())+" kWh");
        mcharge.setText(decimalFormat.format(ovu.getLastMonthCharge()[2])+" Rs");

    }

    //--------------------------------------------------------------------------------  GET DATE DETAILS ------------------------------------------------------
    private String getNextDay() {

        Date date=new Date();
        Calendar calendar1=Calendar.getInstance();
        calendar1.setTime(date);
        calendar1.add(Calendar.DATE,1);
        date=calendar1.getTime();

        String dateA[]=date.toString().split(" ");
        return dateA[2]+"-"+dateA[1]+" "+dateA[0];

    }

    private String getNextMonth() {

        Date date=new Date();
        Calendar calendar=Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MONTH,1);
        date=calendar.getTime();
        String month[]=date.toString().split(" ");
        return month[1];

    }

}
