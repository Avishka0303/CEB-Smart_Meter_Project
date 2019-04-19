package com.example.predatorx21.cebsmartmeter.dashboard;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.db.DB;

import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;

public class HomeFragment extends Fragment {

    private Button powerButton;
    private TextView timeStampView;
    private TextView last15minView;
    private TextView last15minUsageView;
    private TextView lastDayUsageView;
    private TextView lastMonthUsageView;

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
        timeStampView=(TextView) getView().findViewById(R.id.last_time_stamp);
        last15minView=(TextView) getView().findViewById(R.id.last_15min_read);
        last15minUsageView=(TextView) getView().findViewById(R.id.last_15min_usage);
        lastDayUsageView=(TextView) getView().findViewById(R.id.last_day_con);
        lastMonthUsageView=(TextView) getView().findViewById(R.id.last_month_consumption);

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
        setUsageDetails();
        setDailyUsageDetails();
        setMonthlyUsageDetails();
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

    private void setUsageDetails() {

        String query1="SELECT * FROM [MeterReading] WHERE MSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'  ORDER BY [TIME] DESC ";
        ResultSet resultSet1=DB.searchDB(query1);

        int i=0;
        String lastTimeStamp[]=new String[2];
        Double readings[]=new Double[2];
        double usage=0;

        try{
            while(resultSet1.next() && i!=2){
                lastTimeStamp[i]=resultSet1.getString("TIME");
                readings[i++]=resultSet1.getDouble("kWh");
            }
        }catch (Exception e){

            Log.e("Us",e.getMessage());

        }

        usage=readings[0]-readings[1];
        DecimalFormat decimalFormat=new DecimalFormat("#.###");
        decimalFormat.setRoundingMode(RoundingMode.CEILING);

        timeStampView.setText(lastTimeStamp[0]);
        last15minView.setText(readings[0]+" kWh");
        last15minUsageView.setText(decimalFormat.format(usage)+" kWh");

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
}
