package com.example.predatorx21.cebsmartmeter.splash_screen;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.predatorx21.cebsmartmeter.R;
import com.example.predatorx21.cebsmartmeter.db.DB;
import com.example.predatorx21.cebsmartmeter.login.LoginActivity;

import java.sql.Connection;

public class SplashScreen extends AppCompatActivity {

    //initialize splash items
    private ImageView logo;
    private static int SPLASH_TIME_OUT=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        logo=(ImageView) findViewById(R.id.splash_im_view);

        //initialize the animations
        Animation animation=AnimationUtils.loadAnimation(this,R.anim.slide);
        logo.startAnimation(animation);

        Connection connection=DB.createNewConnection();
        if(connection==null){
            Toast.makeText(SplashScreen.this,"On the internet",Toast.LENGTH_SHORT);
        }

        //handle the splash thread.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent=new Intent(SplashScreen.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_TIME_OUT);
    }
}
