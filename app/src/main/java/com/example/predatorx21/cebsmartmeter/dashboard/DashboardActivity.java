package com.example.predatorx21.cebsmartmeter.dashboard;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.db.DB;
import com.example.predatorx21.cebsmartmeter.independent.ProfileActivity;
import com.example.predatorx21.cebsmartmeter.login.LoginActivity;
import com.example.predatorx21.cebsmartmeter.specialdata.Data;

import java.sql.ResultSet;
import java.sql.SQLException;

public class DashboardActivity extends AppCompatActivity {

    public static String USER_TAG;
    public static String USER_ACCNO;
    public static String CURRENT_METER_SERIAL;

    //initialize the navigations bars.
    private BottomNavigationView bottomNavigationView;
    private FrameLayout frameLayout;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private TextView usernameOut;
    private TextView emailOut;
    private LinearLayout navigationHeader;
    private NavigationView navigationView;

    //Initialize the fragments
    private HomeFragment homeFragment;
    private NoticeFragment noticeFragment;
    private UsageFragment usageFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        //initialize bottom navigation bars and the framelayout
        drawerLayout=(DrawerLayout)findViewById(R.id.drawer_lay);
        navigationView=(NavigationView)findViewById(R.id.nav_drawer_menu);
        View headerView = navigationView.getHeaderView(0);
        navigationHeader=(LinearLayout) findViewById(R.id.nav_header);

        bottomNavigationView=(BottomNavigationView)findViewById(R.id.bottom_nav);
        frameLayout=(FrameLayout)findViewById(R.id.main_frame);
        toolbar=(Toolbar) findViewById(R.id.toolbar);

        usernameOut=(TextView)headerView.findViewById(R.id.usernametxt);
        emailOut=(TextView)headerView.findViewById(R.id.emailtxt);

        actionBarDrawerToggle=new ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close);
        actionBarDrawerToggle.setDrawerSlideAnimationEnabled(true);

        //initialize the fragments.
        homeFragment=new HomeFragment();
        noticeFragment=new NoticeFragment();
        usageFragment=new UsageFragment();

        //set toolbar. and initial settings.
        setToolBar(toolbar,"");
        setFragment(homeFragment);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setDashboardToOwner();

        //change the fragment using bottom navigation bar
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch(menuItem.getItemId()){
                    case R.id.nav_home:
                        setFragment(homeFragment);
                        navigationView.setCheckedItem(R.id.nd_home);
                        return true;
                    case R.id.nav_notice:
                        setFragment(noticeFragment);
                        return true;
                    case R.id.nav_usage:
                        setFragment(usageFragment);
                        navigationView.setCheckedItem(R.id.nd_usage);
                        return true;
                    default:
                        return false;
                }
            }
        });

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);
                drawerLayout.closeDrawers();
                switch (menuItem.getItemId()){
                    case R.id.nd_profile:
                        Toast.makeText(DashboardActivity.this,"Profile",Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(DashboardActivity.this,ProfileActivity.class);
                        startActivity(intent);
                        return true;
                    case R.id.nd_home:
                        Toast.makeText(DashboardActivity.this,"Home",Toast.LENGTH_SHORT).show();
                        bottomNavigationView.setSelectedItemId(R.id.nav_home);
                        setFragment(homeFragment);
                        return true;
                    case R.id.nd_usage:
                        Toast.makeText(DashboardActivity.this,"Usage",Toast.LENGTH_SHORT).show();
                        bottomNavigationView.setSelectedItemId(R.id.nav_usage);
                        setFragment(usageFragment);
                        return true;
                    case R.id.nd_bill:
                        return true;
                    case R.id.nd_contact_info:
                        Intent callIntent=new Intent(Intent.ACTION_DIAL);
                        callIntent.setData(Uri.parse(Data.TEL_FOR_CEB));
                        startActivity(callIntent);
                        return true;
                    case R.id.nd_go_to_site:
                        Intent browserIntent=new Intent(Intent.ACTION_VIEW,Uri.parse(Data.URL_FOR_CEB_SITE));
                        startActivity(browserIntent);
                        return true;
                    default:
                        return true;
                }
            }
        });
    }



    private void setDashboardToOwner() {

        String firstName="",lastName="";
        //navigation drawer completions
        String query="SELECT UserName,FirstName,LastName FROM [AspNetUsers] WHERE UserName='"+USER_TAG+"'";
        ResultSet resultSet=DB.searchDB(query);

        try {
            if(resultSet.next()){
                firstName=resultSet.getString("FirstName");
                lastName=resultSet.getString("LastName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Toast.makeText(DashboardActivity.this,"Welcome "+firstName+" "+lastName,Toast.LENGTH_SHORT).show();
        usernameOut.setText(firstName+" "+lastName);
        emailOut.setText(USER_TAG);

        //second query.
        String query2="SELECT RelayStatus,[Meter].MeterSerial FROM [CustomerMeterRelation],[Meter] WHERE [CustomerMeterRelation].MSerial=[Meter].MeterSerial AND ConsumerAccountNo='"+DashboardActivity.USER_ACCNO+"'";
        ResultSet resultSet2=DB.searchDB(query2);
        try {
            if(resultSet2.next()){
                CURRENT_METER_SERIAL=resultSet2.getString("MeterSerial");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        setToolBar(toolbar,CURRENT_METER_SERIAL);
    }

    private void setToolBar(Toolbar toolbar,String meterId) {
        toolbar.setTitle("CEB Smart Meter");
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorWhite,null));
        setSupportActionBar(toolbar);
        toolbar.setSubtitle("Meter ID : "+meterId);
        toolbar.setSubtitleTextColor(getResources().getColor(R.color.colorWhite,null));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String msg="";
        switch (item.getItemId()){
            case R.id.meter_list:
                Toast.makeText(getBaseContext(),"Meter List ",Toast.LENGTH_SHORT).show();
                return true;
            case R.id.logout_tool:
                Toast.makeText(this,"Log Out",Toast.LENGTH_SHORT).show();
                Intent loginIntent=new Intent(DashboardActivity.this,LoginActivity.class);
                startActivity(loginIntent);
                finish();
                return true;
        }

        if (actionBarDrawerToggle.onOptionsItemSelected(item)){
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //set the fragment transactions.
    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_frame,fragment);
        fragmentTransaction.commit();
    }
}
