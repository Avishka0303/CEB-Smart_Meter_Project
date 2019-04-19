package com.example.predatorx21.cebsmartmeter.login;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.db.DB;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class RegistrationActivity extends AppCompatActivity {

    private Spinner spinner;
    private EditText fname;
    private EditText lname;
    private EditText email;
    private EditText cno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        spinner=(Spinner)findViewById(R.id.location_spinner);
        fname=(EditText) findViewById(R.id.fname_txt);
        lname=(EditText)findViewById(R.id.lname_txt);
        email=(EditText)findViewById(R.id.email_txt);
        cno=(EditText)findViewById(R.id.cno_txt);

        initializeTheSpinner();
    }

    private void initializeTheSpinner() {
        List<String> locations=new ArrayList<String>();
        String query="SELECT * FROM [Contacts]";
        ResultSet resultSet= DB.searchDB(query);
        try{
            while(resultSet.next()){
                locations.add(resultSet.getString("Location"));
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, locations);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    public void registerNew(View view){
        Log.e("Buttoncheck","woring.");

        boolean flag=false;
        String query="INSERT INTO [AspNetUsers] ([Id],[AccessFailedCount],[Email],[EmailConfirmed],[LockoutEnabled],[PhoneNumber],[PhoneNumberConfirmed],[TwoFactorEnabled],[FirstName],[LastName],[Location]) VALUES ('1234','0','0','"+email.getText()+"','0','"+cno.getText()+"','0','0','"+email.getText()+"','"+fname.getText()+"','"+lname.getText()+"','"+spinner.getSelectedItem().toString()+"')";
        flag=DB.updateDB(query);
        if(flag){
            Toast.makeText(RegistrationActivity.this,"Registration Completed",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(RegistrationActivity.this,"Registration failure",Toast.LENGTH_SHORT).show();
        }
    }
}
