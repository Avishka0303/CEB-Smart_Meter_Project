package com.example.predatorx21.cebsmartmeter.independent;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.dashboard.DashboardActivity;
import com.example.predatorx21.cebsmartmeter.db.DB;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ProfileActivity extends AppCompatActivity {

    private TextView fullname;
    private TextView email;
    private TextView meterSerial;
    private TextView addDate;
    private TextView relayStatus;
    private TextView relayStatusDate;
    private TextView relay_Status_txt;
    private TextView cno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        fullname=(TextView)findViewById(R.id.name_table_txt);
        email=(TextView)findViewById(R.id.emailtxt);
        cno=(TextView)findViewById(R.id.contxt);
        meterSerial=(TextView)findViewById(R.id.meter_id);
        addDate=(TextView)findViewById(R.id.added_date);
        relayStatus=(TextView)findViewById(R.id.rel_stat);
        relayStatusDate=(TextView)findViewById(R.id.rel_stat_date);
        relay_Status_txt=(TextView)findViewById(R.id.rel_stat_txt);


        refreshGUI();
    }

    private void refreshGUI() {

        String query1="SELECT Email,PhoneNumber,UserName,FirstName,LastName FROM [AspNetUsers] WHERE UserName='"+DashboardActivity.USER_TAG +"'";
        String name = null,em=null,cn=null;
        ResultSet resultSet= DB.searchDB(query1);
        try {
            if(resultSet.next()){
                em=resultSet.getString("Email");
                cn=resultSet.getString("PhoneNumber");
                name=resultSet.getString("FirstName")+" "+resultSet.getString("LastName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        fullname.setText(name);
        email.setText(em);
        cno.setText(cn);

        //Meter Details.
        String query2="SELECT AddedDate,MeterSerial,RelayStatus,RelayStatusDate FROM [Meter] WHERE MeterSerial='"+DashboardActivity.CURRENT_METER_SERIAL+"'";
        ResultSet resultSet2= DB.searchDB(query2);

        try {
            if(resultSet2.next()){
                meterSerial.setText(resultSet2.getString("MeterSerial"));
                addDate.setText(resultSet2.getString("AddedDate"));
                if(resultSet2.getString("RelayStatus").equals("1")){
                    relayStatus.setText("Power Online");
                    relay_Status_txt.setText("Last Offline Date");
                }else{
                    relayStatus.setText("Power Offline");
                    relay_Status_txt.setText("Last Online Date");
                }
                relayStatusDate.setText(resultSet2.getString("RelayStatusDate"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
