package com.example.predatorx21.cebsmartmeter.login;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.dashboard.DashboardActivity;
import com.example.predatorx21.cebsmartmeter.db.DB;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginActivity extends AppCompatActivity {

    private EditText uname;
    private EditText accountno;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //initialize the components.
        uname=(EditText)findViewById(R.id.username);
        accountno=(EditText)findViewById(R.id.password);

        checkConnection();
    }

    private void checkConnection() {
        Connection connection=DB.createNewConnection();
        if(connection==null){
            Toast.makeText(LoginActivity.this,"On the internet",Toast.LENGTH_SHORT).show();
        }else{
            Toast.makeText(LoginActivity.this,"Database Connected",Toast.LENGTH_SHORT).show();
        }
    }

    public void loginToDashboard(View view){

        String username=uname.getText().toString();
        String acno=accountno.getText().toString();

        String query="SELECT * FROM [AspNetUsers] WHERE UserName='"+username+"'";
        ResultSet rs=DB.searchDB(query);

        try {
            if(rs.next()){
                Intent intent=new Intent(LoginActivity.this,DashboardActivity.class);
                startActivity(intent);
                DashboardActivity.USER_TAG=username;
                DashboardActivity.USER_ACCNO=acno;
                finish();
            }else{
                Toast.makeText(LoginActivity.this,"Username or Password Incorrect",Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DBE",e.getMessage());
        }
    }

    public void registerNewUser(View view){
        Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
        startActivity(intent);
    }
}
