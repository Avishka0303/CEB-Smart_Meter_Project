package com.example.predatorx21.cebsmartmeter.dashboard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.db.DB;
import com.example.predatorx21.cebsmartmeter.utilities.ThresholdSetup;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import static android.support.v4.content.ContextCompat.getSystemService;

public class HomeFragment extends Fragment {

    private static final String CHANNEL1_ID="channel1";
    private String centerString;


    private Button powerButton;
    private TextView last15minView;
    private TextView last15minUsageView;
    private TextView lastDayUsageView;
    private TextView lastMonthUsageView;
    private PieChart pie_threshold;

    private boolean PowerStatus=false;

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        powerButton=(Button)getView().findViewById(R.id.power_button);
        lastDayUsageView=(TextView) getView().findViewById(R.id.last_day_con);
        lastMonthUsageView=(TextView) getView().findViewById(R.id.last_month_consumption);
        pie_threshold=(PieChart)getView().findViewById(R.id.threshold_pie_chart);
        pie_threshold.setUsePercentValues(true);

        initializeHomeGUI();
        powerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                giveAlert();
            }
        });
    }

    private void initializeHomeGUI() {
        //check for the power status.
        boolean currentStatus=checkPower();
        if(!currentStatus){
            powerButton.setBackground(getResources().getDrawable(R.drawable.power_btn_off,null));
            powerButton.setText("SYSTEM OFFLINE");
        }else{
            powerButton.setBackground(getResources().getDrawable(R.drawable.power_btn_theme,null));
            powerButton.setText("SYSTEM ONLINE");
        }

        //setUsageDetails();
        //setDailyUsageDetails();
        setMonthlyUsageDetails();
        setThresholdDetails();

    }

    private void setThresholdDetails() {

        DecimalFormat decimalFormat=new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        ArrayList<PieEntry> yValues=new ArrayList<>();
        ThresholdSetup thresholdSetup=new ThresholdSetup();

        if(thresholdSetup.getThresholdStatus().equals("1")){
            double usedUnits[]=thresholdSetup.getThresholdDetails();
            if (usedUnits[0]>=100){
                yValues.add(new PieEntry(100f,"Used %"));
                if(thresholdSetup.getThresholdNotification().equals("1"))
                    showThresholdNotification(usedUnits[3]);
            }else{
                yValues.add(new PieEntry((float) usedUnits[0],"Used %"));
                yValues.add(new PieEntry((float) usedUnits[1],"NotUsed %"));
            }
            centerString=thresholdSetup.getThresholdType().toUpperCase()+ " LIMIT ACTIVATED\n\nused units : "+decimalFormat.format(usedUnits[2])+" kWh\nremaining units : "+decimalFormat.format(usedUnits[3])+" kWh";
        }else{
            yValues.add(new PieEntry(100,"Not limited"));
            centerString="NO ANY LIMIT ACTIVATED\nGO TO CONTROL";
        }

        setupPieChartForThreshold();
        PieDataSet dataSet=new PieDataSet(yValues,"Threshold Usage");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        int colors[]=new int[2];
        colors[0]=getResources().getColor(R.color.colorPrimary,null);
        colors[1]=getResources().getColor(R.color.colorYellow,null);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data=new PieData(dataSet);
        data.setValueTextSize(20f);
        data.setValueTextColor(Color.YELLOW);

        pie_threshold.setData(data);
    }

    private void setupPieChartForThreshold() {

        pie_threshold.getDescription().setEnabled(false);
        pie_threshold.setDragDecelerationEnabled(true);

        pie_threshold.setDrawHoleEnabled(true);
        pie_threshold.setHoleColor(getResources().getColor(R.color.colorWhite,null));
        pie_threshold.setTransparentCircleRadius(80f);
        pie_threshold.setHoleRadius(70f);

        pie_threshold.setCenterText(centerString);
        pie_threshold.setCenterTextSize(15f);
        pie_threshold.setCenterTextColor(getResources().getColor(R.color.colorPrimary,null));

        pie_threshold.animateY(1500,Easing.EaseInOutCubic);

        Legend legend=pie_threshold.getLegend();
        legend.setEnabled(false);
    }

    private void setMonthlyUsageDetails() {

        String query1="SELECT * FROM [MonthlyConsumptionValidateTable] WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY [MSerial] ASC,[Month] DESC ";
        ResultSet resultSet1= DB.searchDB(query1);
        String date[]=new String[2];
        double consumption[]=new double[2];
        int i=0;
        double usage=0;

        try {
            while (resultSet1.next() && i!=2){
                date[i]=resultSet1.getString("Timestamp");
                consumption[i++]=resultSet1.getDouble("kWh");
            }
        }catch (Exception e){
            Log.e("us",e.getMessage());
        }

        usage=consumption[0]-consumption[1];
        DecimalFormat decimalFormat=new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        lastMonthUsageView.setText(decimalFormat.format(usage)+" kWh");

    }


    private void setDailyUsageDetails() {

        //daily usage
        String query1="SELECT * FROM [DailyEnergyConsumption] WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"' ORDER BY [Date] DESC";
        ResultSet resultSet1= DB.searchDB(query1);
        String date[]=new String[2];
        double consumption[]=new double[2];
        int i=0;
        double usage=0;

        try {
            while (resultSet1.next() && i!=2){
                date[i]=resultSet1.getString("Date");
                consumption[i++]=resultSet1.getDouble("Consumption");
            }
        }catch (Exception e){
            Log.e("us",e.getMessage());
        }

        usage=consumption[0]-consumption[1];
        DecimalFormat decimalFormat=new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        lastDayUsageView.setText(decimalFormat.format(usage)+" kWh");
    }


    private boolean checkPower() {
        boolean flag=false;
        String query="SELECT RelayStatus,[Meter].MeterSerial FROM [CustomerMeterRelation],[Meter] WHERE [CustomerMeterRelation].MSerial=[Meter].MeterSerial AND ConsumerAccountNo='"+DashboardActivity.USER_ACCNO+"'";
        ResultSet rs=DB.searchDB(query);
        try {
            if(rs.next()){
                String status=rs.getString("RelayStatus");
                if(status.equals("1")){
                    flag=true;
                    PowerStatus=true;
                }else {
                    PowerStatus=false;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("rs",e.getMessage());
        }
        return flag;
    }

    private void setSystemToggle() {
        int status=1;
        if(PowerStatus){
            PowerStatus=false;
            status=0;
        }else{
            PowerStatus=true;
            status=1;
        }
        String query="UPDATE Meter SET RelayStatus='"+status+"' WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
        DB.updateDB(query);
        initializeHomeGUI();
    }

    private void giveAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        if (PowerStatus)
            builder.setMessage(R.string.powerbtn_message_off).setTitle(R.string.powerbtn_title);
        else
            builder.setMessage(R.string.powerbtn_message_on).setTitle(R.string.powerbtn_title);

        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                setSystemToggle();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void showThresholdNotification(double overUsage) {
        //create notification channel
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){
            NotificationChannel channel=new NotificationChannel(CHANNEL1_ID,"Channel 1",NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Overusage Warning");
            NotificationManager manager=getSystemService(getContext(),NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        DecimalFormat decimalFormat=new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getContext(),CHANNEL1_ID)
                .setSmallIcon(R.drawable.ic_warning)
                .setContentTitle("Overusage Warning")
                .setColorized(true)
                .setColor(Color.RED)
                .setLights(Color.WHITE,1000,1000)
                .setContentText("You have overusage of "+decimalFormat.format(-overUsage)+" kWh ")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        NotificationManagerCompat notificationManager=NotificationManagerCompat.from(getContext());
        notificationManager.notify(1,builder.build());

    }
}
