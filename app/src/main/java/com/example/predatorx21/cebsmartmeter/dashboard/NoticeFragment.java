package com.example.predatorx21.cebsmartmeter.dashboard;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.utilities.DateTrigger;
import com.example.predatorx21.cebsmartmeter.utilities.OverviewUsage;

import java.math.RoundingMode;
import java.text.DecimalFormat;

public class NoticeFragment extends Fragment {

    //TEXT VIEWS
    private TextView mmonth;
    private TextView mreading;
    private TextView mconsumed;
    private TextView mcharge;

    private DecimalFormat decimalFormat;


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

        decimalFormat=new DecimalFormat("##.##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        setLastMonthDetail();

    }

     private void setLastMonthDetail() {

        OverviewUsage ovu=new OverviewUsage("");
        String date[]=ovu.getDateFormat2().split("-");
        String dateShow=" 20 - "+new DateTrigger().getShortMonth(date[1])+" "+" 2019 ";
        mmonth.setText(dateShow);
        mreading.setText(decimalFormat.format(ovu.getLastMonthReading())+" kWh");
        mconsumed.setText(decimalFormat.format(ovu.getLastMonthConsumption())+" kWh");
        mcharge.setText(decimalFormat.format(ovu.getLastMonthCharge()[2])+" Rs");

    }
}
