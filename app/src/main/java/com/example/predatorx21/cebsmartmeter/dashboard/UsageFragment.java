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
import com.example.predatorx21.cebsmartmeter.db.DB;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.text.DecimalFormat;

public class UsageFragment extends Fragment {

    private GraphView graphView;
    private TextView startTime;
    private TextView endTime;


    public UsageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_usage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        graphView=(GraphView)getView().findViewById(R.id.min15graph);
        startTime=(TextView)getView().findViewById(R.id.start_time);
        endTime=(TextView)getView().findViewById(R.id.end_time);
        plot15minGraph();
    }

    private void plot15minGraph() {

        DataPoint dataPoint[]=new DataPoint[20];

        String query1="SELECT * FROM [MeterReading] WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'  ORDER BY [TIME] DESC ";
        ResultSet resultSet1=DB.searchDB(query1);

        int i=0;
        String lastTimeStamp[]=new String[20];
        Double readings[]=new Double[20];
        double usage[]=new double[20];

        try{
            while(resultSet1.next() && i!=20){
                lastTimeStamp[i]=resultSet1.getString("TIME");
                readings[i]=resultSet1.getDouble("kWh");
                if(i>1)
                    usage[i]=readings[i-1]-readings[i];
                dataPoint[i]=new DataPoint(i,usage[i]);
                i++;
            }
        }catch (Exception e){
            Log.e("Us",e.getMessage());
        }

        LineGraphSeries<DataPoint> series=new LineGraphSeries<>(dataPoint);

        graphView.getViewport().setYAxisBoundsManual(false);

        graphView.getViewport().setXAxisBoundsManual(false);
        graphView.getViewport().setMaxX(20);
        graphView.getViewport().setMinX(1);

        // enable scaling and scrolling
        graphView.getViewport().setScalable(true);
        graphView.getViewport().setScalableY(true);
        graphView.setTitle("15 min Readings (kWh)");
        graphView.setTitleTextSize(70);
        graphView.setTitleColor(getResources().getColor(R.color.colorWhite,null));
        graphView.getLegendRenderer().setVisible(true);
        graphView.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
        graphView.addSeries(series);

        startTime.setText(lastTimeStamp[19]);
        endTime.setText(lastTimeStamp[0]);
    }
}
