package com.example.predatorx21.cebsmartmeter.login;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
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
    private Connection connection;

    private SharedPreferences mPrefers;
    private CheckBox mCheckBox;
    private static final String PREFS_NAME="RememberMe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mPrefers=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        bindWidget();
        getPreferencesData();
    }

    private void bindWidget(){
        uname=(EditText)findViewById(R.id.username);
        accountno=(EditText)findViewById(R.id.password);
        mCheckBox=(CheckBox)findViewById(R.id.remember_me_cb);
    }

    private boolean checkConnection() {

        if( isNetworkAvailable() ){

            connection=DB.createNewConnection();

            if(connection==null){

                Toast.makeText(LoginActivity.this,"Cannot connect with Server",Toast.LENGTH_SHORT).show();
                return false;

            }else{

                Toast.makeText(LoginActivity.this,"Database connected",Toast.LENGTH_SHORT).show();
                return true;

            }

        }else {

            Toast.makeText(LoginActivity.this,"Turn on data connection or WIFI",Toast.LENGTH_SHORT).show();
            return false;

        }

    }

    public void loginToDashboard(View view){

        if(checkConnection()){

            String username=uname.getText().toString();
            String acno=accountno.getText().toString();

            String query="SELECT * FROM [AspNetUsers] WHERE UserName='"+username+"'";
            ResultSet rs=DB.searchDB(query);

            try {
                if(rs.next()){
                    rememberMe();
                    Intent intent=new Intent(LoginActivity.this,DashboardActivity.class);
                    startActivity(intent);
                    DashboardActivity.USER_TAG=username;
                    DashboardActivity.USER_ACCNO=acno;
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"Username or Password Incorrect",Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException e) {
                Log.e("DBE",e.getMessage());
            }
        }
    }

    private void rememberMe() {
        if(mCheckBox.isChecked()){
            boolean boolIsChecked=mCheckBox.isChecked();
            SharedPreferences.Editor editor=mPrefers.edit();
            editor.putString("pref_name",uname.getText().toString());
            editor.putString("pref_pass",accountno.getText().toString());
            editor.putBoolean("pref_check",boolIsChecked);
            editor.apply();
        }else{
            mPrefers.edit().clear().apply();
        }
    }

    private void getPreferencesData(){

        SharedPreferences sp=getSharedPreferences(PREFS_NAME,MODE_PRIVATE);

        if(sp.contains("pref_name")){
            String u=sp.getString("pref_name","not found");
            uname.setText(u);
        }

        if(sp.contains("pref_pass")){
            String p=sp.getString("pref_pass","not_found");
            accountno.setText(p);
        }

        if(sp.contains("pref_check")){
            boolean b=sp.getBoolean("pref_check",false);
            mCheckBox.setChecked(b);
        }

    }

    //check the connection
    public boolean isNetworkAvailable() {

        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        return isConnected;

    }

    public void registerNewUser(View view){
        Intent intent=new Intent(LoginActivity.this,RegistrationActivity.class);
        startActivity(intent);
    }

}
