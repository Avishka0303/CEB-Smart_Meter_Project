package com.example.predatorx21.cebsmartmeter.db;

import android.os.StrictMode;
import android.util.Log;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DB {
    public static final String DRIVER="net.sourceforge.jtds.jdbc.Driver";
    public static final String URL="jdbc:jtds:sqlserver://sql5041.site4now.net/DB_A471BF_smartmeter;user=DB_A471BF_smartmeter_admin;password=avishka1996";
    public static Connection connection;

    public static Connection createNewConnection(){
        StrictMode.ThreadPolicy policy=new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        try {
            Class.forName(DRIVER);
            connection=DriverManager.getConnection(URL);
        }catch (ClassNotFoundException e){
            Log.e("DBE",e.getMessage());
        }catch (SQLException e1){
            Log.e("DBE",e1.getMessage());
        }catch (Exception e2){
            Log.e("DBE",e2.getMessage());
        }
        return connection;
    }

    public static boolean updateDB(String updateQuery){
        boolean flag=false;
        if(connection==null){
            createNewConnection();
        }
        try {
            Statement statement=connection.createStatement();
            flag=statement.execute(updateQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DBE",e.getMessage());
        }
        return flag;
    }

    public static ResultSet searchDB(String searchQuery){
        ResultSet rs=null;
        if(connection==null){
            createNewConnection();
        }
        try {
            Statement statement=connection.createStatement();
            rs=statement.executeQuery(searchQuery);
        } catch (SQLException e) {
            e.printStackTrace();
            Log.e("DBE",e.getMessage());
        }
        return rs;
    }

}
