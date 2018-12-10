package com.binujayaram.coverassessment.view.activity;

import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.WindowManager;

import com.binujayaram.coverassessment.Constants;
import com.binujayaram.coverassessment.R;
import com.binujayaram.coverassessment.dataservice.DBManager;
import com.binujayaram.coverassessment.interfaces.Response;
import com.binujayaram.coverassessment.models.Carriers;
import com.binujayaram.coverassessment.utils.Utility;
import com.binujayaram.coverassessment.view.ScreenManager;
import com.binujayaram.coverassessment.view.fragment.AddressSearchFragment;
import com.binujayaram.coverassessment.view.fragment.CarrierSearchFragment;
import com.binujayaram.coverassessment.view.fragment.FinalFragment;
import com.binujayaram.coverassessment.view.fragment.NavigationButtonFragment;

public class MainActivity extends AppCompatActivity implements NavigationButtonFragment.ListenNavigation,
        ScreenManager.ScreenManagerEvents {

    private ScreenManager screenManager;
    private boolean clearAll = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        screenManager = new ScreenManager(this, (ScreenManager.ScreenManagerEvents)this);
        screenManager.calculateAndLoadFragment(true);

    }


    private void loadFragment(Fragment fragment){
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment).commit();
    }

    private void broadcastFragmentSwitchEvent(final int pageNo, final String broadcastKey, final String intentKey){

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
                // Create intent with action
                Intent localIntent = new Intent(broadcastKey);
                localIntent.putExtra(intentKey, pageNo);
                // Send local broadcast
                localBroadcastManager.sendBroadcast(localIntent);
            }
        }, 100);
    }


    @Override
    public void onNavigate() {
       screenManager.calculateAndLoadFragment(true);
    }





    @Override
    protected void onPause() {
        if(clearAll){
            DBManager.clearAll(getApplicationContext(), new Response() {
                @Override
                public void onSuccess(Carriers carriers) {

                }

                @Override
                public void onFaliure(Carriers carriers) {

                }
            });
        }
        super.onPause();
    }

    private void showToolbarUp(boolean show){
        // add back arrow to toolbar
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(show);
            getSupportActionBar().setDisplayShowHomeEnabled(show);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        screenManager.calculateAndLoadFragment(false);
        return true;
    }

    @Override
    public void broadcastFragmentChangeEvent(int pageNo, String keyUpdateProgress, String keyPageNo) {
        broadcastFragmentSwitchEvent(pageNo, keyUpdateProgress, keyPageNo);
    }

    @Override
    public void loadFragmentEvent(Fragment fragment) {
        loadFragment(fragment);
    }

    @Override
    public void showToolbarUpIcon(boolean doShow) {
        showToolbarUp(doShow);
    }

    @Override
    public void exitApp() {
        clearAll = true;
        finish();
    }
}
