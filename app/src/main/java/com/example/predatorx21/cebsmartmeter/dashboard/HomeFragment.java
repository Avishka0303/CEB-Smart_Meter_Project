package com.example.predatorx21.cebsmartmeter.dashboard;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.SoundPool;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.example.predatorx21.cebsmartmeter.utilities.DateTrigger;
import com.example.predatorx21.cebsmartmeter.utilities.OverviewUsage;
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
    private TextView chargeUptoNow;
    private TextView lastDayUsageView;
    private TextView lastMonthUsageView;
    private TextView voltage,power;
    private TextView lastUpdateTime,powerStatus;
    private PieChart pie_threshold;

    private boolean PowerStatus=false;

    private DecimalFormat decimalFormat;

    //runnable for check the power status.
    Handler powerHandler=new Handler();
    private Runnable checkPowerRunnable=new Runnable() {
        @Override
        public void run() {
            initializePowerStatus();
            powerHandler.postDelayed(checkPowerRunnable,5000);
        }
    };

    Handler guiHandler=new Handler();
    private Runnable guiUpdateRunnable=new Runnable() {
        @Override
        public void run() {
            initializeHomeGUI();
            guiHandler.postDelayed(guiUpdateRunnable,900000);
        }
    };

    public HomeFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {

        lastDayUsageView=(TextView) getView().findViewById(R.id.last_day_con);
        lastMonthUsageView=(TextView) getView().findViewById(R.id.last_month_consumption);
        pie_threshold=(PieChart)getView().findViewById(R.id.threshold_pie_chart);
        chargeUptoNow=(TextView)getView().findViewById(R.id.charge_up_now);
        voltage=(TextView)getView().findViewById(R.id.voltage_txt);
        power=(TextView)getView().findViewById(R.id.power_txt);
        lastUpdateTime=(TextView)getView().findViewById(R.id.last_update_time_txt);
        powerStatus=(TextView)getView().findViewById(R.id.power_status_txt);

        decimalFormat=new DecimalFormat("#.##");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        //initializeHomeGUI();
        //initializePowerStatus();
        powerHandler.post(checkPowerRunnable);
        guiHandler.post(guiUpdateRunnable);
    }

    private void initializePowerStatus() {
        boolean currentStatus=checkPower();
        if(!currentStatus){
            powerStatus.setBackground(getResources().getDrawable(R.drawable.power_off_bg,null));
            powerStatus.setText("OFFLINE");
        }else{
            powerStatus.setBackground(getResources().getDrawable(R.drawable.power_on_bg,null));
            powerStatus.setText("ONLINE ");
        }
    }

    private void initializeHomeGUI() {
        //check for the power status.
        lastMonthUsageDetail();
        setDailyUsageDetails();
        setMonthlyUsageDetails();
        setThresholdDetails();
    }

//======================================================================================= SET THRESHOLD DETAILS =======================================================================================
    private void setThresholdDetails() {

        //setup an arraylist for entries
        ArrayList<PieEntry> yValues=new ArrayList<>();

        //create class for threshold setup
        ThresholdSetup thresholdSetup=new ThresholdSetup();

        //check the threshold status.
        if(thresholdSetup.getThresholdStatus().equals("1")){

            //get threshold details.
            double usedUnits[]=thresholdSetup.getThresholdDetails();

            if (usedUnits[0]>=100){

                yValues.add(new PieEntry(100f,"Used %"));
                if(thresholdSetup.getThresholdNotification().equals("1"))
                    showThresholdNotification(usedUnits[3]);
                centerString=thresholdSetup.getThresholdType().toUpperCase()+ " LIMIT ACTIVATED\n\nused units : "+decimalFormat.format(usedUnits[2])+" kWh\nOver usage units : "+decimalFormat.format(-usedUnits[3])+" kWh";

            }else{

                yValues.add(new PieEntry((float) usedUnits[0],"Used %"));
                yValues.add(new PieEntry((float) usedUnits[1],"NotUsed %"));
                centerString=thresholdSetup.getThresholdType().toUpperCase()+ " LIMIT ACTIVATED\n\nused units : "+decimalFormat.format(usedUnits[2])+" kWh\nremaining units : "+decimalFormat.format(usedUnits[3])+" kWh";

            }

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
        colors[1]=getResources().getColor(R.color.colorBlackGray,null);
        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        PieData data=new PieData(dataSet);
        data.setValueTextSize(15f);
        data.setValueTextColor(getResources().getColor(R.color.colorBlackGray,null));
        pie_threshold.setData(data);

    }

    //--------------------------------------------------------------------------------- set properties of the pie graphs. ----------------------------------------------------------------------------------

    private void setupPieChartForThreshold() {

        pie_threshold.setUsePercentValues(true);
        pie_threshold.getDescription().setEnabled(false);
        pie_threshold.setDragDecelerationEnabled(true);

        pie_threshold.setDrawHoleEnabled(true);
        pie_threshold.setHoleColor(getResources().getColor(R.color.colorBlackGray,null));
        pie_threshold.setTransparentCircleRadius(75f);
        pie_threshold.setHoleRadius(70f);

        pie_threshold.setCenterText(centerString);
        pie_threshold.setCenterTextSize(15f);
        pie_threshold.setCenterTextColor(getResources().getColor(R.color.colorWhite,null));

        pie_threshold.animateY(1500,Easing.EaseInOutCubic);

        Legend legend=pie_threshold.getLegend();
        legend.setEnabled(false);
    }

//---------------------------to set up the monthly details-----------------------------------------------------------------------------------
    private void setMonthlyUsageDetails() {
        double monthUsage[]=new OverviewUsage("Monthly").getMonthlyDetail();
        chargeUptoNow.setText(decimalFormat.format(monthUsage[1])+" Rs");
    }

//-------------------------------------- daily usage details.---------------------------------------------------------------------------------
    private void setDailyUsageDetails() {
        OverviewUsage ovu=new OverviewUsage("Daily");
        double dailyUsage[]=ovu.getDailyDetail();
        String validateDate[]=new DateTrigger(ovu.getDate()).getTime();
        lastDayUsageView.setText(decimalFormat.format(dailyUsage[1])+" Rs");
        voltage.setText(ovu.getVoltage()+" V");
        power.setText(ovu.getPower()+" W");
        lastUpdateTime.setText(validateDate[0]+":"+validateDate[1]);
    }

//--------------------------------------last month usage details-------------------------------------------------------------------------------

    private void lastMonthUsageDetail() {
        double monthUsage=new OverviewUsage("mon").getLastMonthConsumption();
        lastMonthUsageView.setText(decimalFormat.format(monthUsage)+" kWh");
    }

//----------------------------------------------check power-------------------------------------------------------------------------------------

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

    @Override
    public void onDestroy() {
        powerHandler.removeCallbacks(checkPowerRunnable);
        guiHandler.removeCallbacks(guiUpdateRunnable);
        super.onDestroy();
    }

}
