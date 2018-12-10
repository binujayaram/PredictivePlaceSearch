package com.binujayaram.coverassessment.view.activity;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.binujayaram.coverassessment.R;
import com.binujayaram.coverassessment.dataservice.DBManager;
import com.binujayaram.coverassessment.interfaces.Response;
import com.binujayaram.coverassessment.models.Carriers;
import com.binujayaram.coverassessment.utils.Utility;


public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // remove title
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);
        initViews();
        DBManager.clearAll(getApplicationContext(), new Response() {
            @Override
            public void onSuccess(Carriers carriers) {
                gotToHomeScreen();
            }

            @Override
            public void onFaliure(Carriers carriers) {
                gotToHomeScreen();
            }
        });

    }

    private void initViews(){
        TextView splashText = (TextView)findViewById(R.id.splash_text);
        splashText.setTypeface(Utility.getBoldTypeFace(getApplicationContext()));
    }


    private void gotToHomeScreen(){
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
            }
        }, 2000);

    }
}
