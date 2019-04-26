package com.example.predatorx21.cebsmartmeter.dashboard;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ScrollView;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.utilities.GraphData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class UsageFragment extends Fragment {

    private LineChart unitsUsage;
    private HorizontalBarChart chargeChart;
    private GraphData graphData;
    private Button dailyBtn;
    private Button monthlyBtn;
    private Button weeklyBtn;
    private Button scrollBottom;
    private ScrollView scrollView;

    private String CURRENT_GRAPH="DAILY";

    public UsageFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_usage, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        //initialize the realtime chart.
        unitsUsage=(LineChart)getView().findViewById(R.id.realTime_chart);
        chargeChart=(HorizontalBarChart)getView().findViewById(R.id.charg_chart);

        dailyBtn=(Button)getView().findViewById(R.id.daily_btn);
        monthlyBtn=(Button)getView().findViewById(R.id.monthly_btn);
        weeklyBtn=(Button)getView().findViewById(R.id.week_btn);
        scrollBottom=(Button)getView().findViewById(R.id.show_bottom_graph);

        scrollView=(ScrollView)getView().findViewById(R.id.scroll_view);
        scrollView.fullScroll(View.FOCUS_UP);

        graphData=new GraphData();

        //initialize button actions.
        giveButtonActions();
        plotDailyDetails();
        plotDailyChargesDetails();
        setSelectedBtn(dailyBtn);
        CURRENT_GRAPH="DAILY";

    }

//--------------------------------------------------------------------------DAILY DETAILS--------------------------------------------------------------------------------------------------------

    private void plotDailyDetails() {

        List<Entry> real_time_detail=graphData.getDailyUnits();
        String[] months= reverse(graphData.getxDLabels(),graphData.getxDLabels().length);

        LineDataSet lineDataSet=new LineDataSet(real_time_detail,"Daily Usage ( kWh )");
        lineDataSet.setDrawFilled(true);
        lineDataSet.setValueTextSize(5f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setCircleColor(getResources().getColor(R.color.colorBrown1,null));
        lineDataSet.setColor(getResources().getColor(R.color.colorBlue,null));
        lineDataSet.setLineWidth(3f);

        //set data for graph.
        List<ILineDataSet> dataSets=new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        LineData data=new LineData(dataSets);

        //axises
        XAxis xAxis=unitsUsage.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setLabelRotationAngle(300f);
        xAxis.setGranularityEnabled(true);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setTextSize(12f);
        xAxis.setGranularity(1f);
        xAxis.setDrawGridLines(false);


        //supply initialize settings for graph.
        // KWH GRAPH
        YAxis rightYAxis=unitsUsage.getAxisRight();
        YAxis leftYAxis=unitsUsage.getAxisLeft();
        leftYAxis.setAxisLineWidth(1.5f);
        leftYAxis.setTextSize(15);
        rightYAxis.setEnabled(false);
        unitsUsage.animateY(1500,Easing.EaseInOutSine);

        unitsUsage.setData(data);
        unitsUsage.invalidate();
    }

//==========================================================================DAILY CHARGES===============================================================================================
    private void plotDailyChargesDetails() {

        //GET THE DATA FOR PLOT THE GRAPH
        ArrayList<BarEntry> dailyEntries=graphData.getDailyCharges();
        String[] months= reverse(graphData.getxDLabels(),graphData.getxDLabels().length);

        //ARRANGE THE DATA SET AND GIVE PROPERTIES.
        BarDataSet barDataSet=new BarDataSet(dailyEntries,"Daily charges (Rs)");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        //cancel the VISIBILITY of the right side axis.
        YAxis rightYAxis=chargeChart.getAxisRight();
        rightYAxis.setEnabled(false);

        //SET ENABLE THE LEFT SIDE AXIS AND SET THE LINE WIDTH.
        YAxis leftYAxis=chargeChart.getAxisLeft();
        leftYAxis.setEnabled(true);
        leftYAxis.setAxisLineWidth(1.5f);

        //SET THE X- AXIS PROPERTIES. [ GRANUALITY ] [ POSITION ] [ LINE WIDTH]
        XAxis xAxis=chargeChart.getXAxis();
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));

        //BAR DATA INSERT AND ANIMATION
        BarData barData=new BarData(barDataSet);
        chargeChart.animateY(1500,Easing.EaseInSine);
        chargeChart.setData(barData);
        chargeChart.invalidate();

    }

//-----------------------------------------------------------------------------DAILY DETAILS FINISH-------------------------------------------------------------------------------


//-----------------------------------------------------------------------------MONTHLY DETAILS------------------------------------------------------------------------------------

    private void plotMonthlyDetails() {

        //GET THE DATA FOR THE MONTHLY DETAILS.
        List<Entry> real_time_detail=graphData.getMonthlyUnits();
        String[] months= reverse(graphData.getxMLabels(),graphData.getxMLabels().length);

        //SETUP THE DATA SET AND THEIR PROPERTIES.
        LineDataSet lineDataSet=new LineDataSet(real_time_detail,"Monthly Usage ( kWh )");
        lineDataSet.setDrawFilled(true);
        lineDataSet.setValueTextSize(5f);
        lineDataSet.setCircleRadius(5f);
        lineDataSet.setCircleColor(getResources().getColor(R.color.colorBrown1,null));
        lineDataSet.setColor(getResources().getColor(R.color.colorBlue,null));
        lineDataSet.setLineWidth(3f);

        //SET THE DATA SET FOT LINE DATA
        List<ILineDataSet> dataSets=new ArrayList<ILineDataSet>();
        dataSets.add(lineDataSet);
        LineData data=new LineData(dataSets);

        //SETUP THE X-AXIS [ POSITION ] [ LINE WIDTH ] [ OFF THE VERTICAL GRID LINE]
        XAxis xAxis=unitsUsage.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months));
        xAxis.setLabelRotationAngle(270f);
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setTextSize(15f);
        xAxis.setDrawGridLines(false);

        //supply initialize settings for graph.
        YAxis rightYAxis=unitsUsage.getAxisRight();
        YAxis leftYAxis=unitsUsage.getAxisLeft();
        leftYAxis.setAxisLineWidth(1.5f);
        leftYAxis.setTextSize(15);
        rightYAxis.setEnabled(false);
        unitsUsage.animateY(1500,Easing.EaseInOutSine);

        //SET THE GRAPH TO PLOT
        unitsUsage.setData(data);
        unitsUsage.invalidate();

    }

//==================================================================================MONTHLY CHARGES=====================================================================================

    private void plotMonthlyChargesDetails() {

        //GET THE MONTHLY CHARGES DATA SET
        ArrayList<BarEntry> dailyEntries=graphData.getMonthlyCharges();
        String[] months= reverse(graphData.getxMLabels(),graphData.getxMLabels().length);

        //SET THE DATA SET AS BAR DATA SET AND ANIMATION
        BarDataSet barDataSet=new BarDataSet(dailyEntries,"Monthly charges (Rs)");
        barDataSet.setColors(ColorTemplate.JOYFUL_COLORS);
        barDataSet.setValueTextSize(10);

        //SET THE X- AXIS PROPERTIES. [ GRANUALITY ] [ POSITION ] [ LINE WIDTH]
        XAxis xAxis=chargeChart.getXAxis();
        xAxis.setGranularityEnabled(true);
        xAxis.setGranularity(1f);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setAxisLineWidth(1.5f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(months){
            @Override
            public String getFormattedValue(float value) {
                return super.getFormattedValue(value);
            }
        });
        xAxis.setTextSize(15f);

        BarData barData=new BarData(barDataSet);
        chargeChart.animateY(1500,Easing.EaseInBounce);
        chargeChart.setData(barData);
        chargeChart.invalidate();

    }

//--------------------------------------------------------------------------------MONTHLY DETAILS FINISH---------------------------------------------------------------------------


    private void giveButtonActions() {

        dailyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBtn(dailyBtn);
                CURRENT_GRAPH="DAILY";
                plotDailyDetails();
                plotDailyChargesDetails();
            }
        });

        monthlyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBtn(monthlyBtn);
                CURRENT_GRAPH="MONTHLY";
                plotMonthlyDetails();
                plotMonthlyChargesDetails();
            }
        });

        weeklyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setSelectedBtn(weeklyBtn);
                CURRENT_GRAPH="WEEKLY";
            }
        });

        scrollBottom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollView.fullScroll(View.FOCUS_DOWN);
                if(CURRENT_GRAPH.equals("DAILY"))
                    plotDailyChargesDetails();
                else if(CURRENT_GRAPH.equals("MONTHLY"))
                    plotMonthlyChargesDetails();

            }
        });
    }

    private void setSelectedBtn(Button btn) {
        //reset all buttons

        dailyBtn.setBackground(getResources().getDrawable(R.drawable.not_selected_btn,null));
        dailyBtn.setTextColor(getResources().getColor(R.color.colorWhite,null));

        monthlyBtn.setBackground(getResources().getDrawable(R.drawable.not_selected_btn,null));
        monthlyBtn.setTextColor(getResources().getColor(R.color.colorWhite,null));

        weeklyBtn.setBackground(getResources().getDrawable(R.drawable.not_selected_btn,null));
        weeklyBtn.setTextColor(getResources().getColor(R.color.colorWhite,null));

        btn.setBackground(getResources().getDrawable(R.drawable.selected_btn,null));
        btn.setTextColor(getResources().getColor(R.color.colorBlue,null));
        btn.setTextSize(15f);

    }

    //----------------------------------------------------------------------------------UTILITIES---------------------------------------------------------------------------------------
    static String[] reverse(String a[], int n) {
        String k, t;
        int i;
        for (i = 0; i < n / 2; i++) {
            t = a[i];
            a[i] = a[n - i - 1];
            a[n - i - 1] = t;
        }
        return a;
    }
}
