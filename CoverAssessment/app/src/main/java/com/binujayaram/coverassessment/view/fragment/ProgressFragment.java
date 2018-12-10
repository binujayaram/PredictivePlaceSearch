package com.binujayaram.coverassessment.view.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.binujayaram.coverassessment.Constants;
import com.binujayaram.coverassessment.R;

public class ProgressFragment extends Fragment {
    private ProgressBar progressBar;
    private String TAG = ProgressFragment.class.getSimpleName();
    BroadcastReceiver listener;
    LocalBroadcastManager localBroadcastManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.progress_fragment, container, false);
        initViews(view);
        return view;
    }


    private void registerForBoradcastReceiver(){
        listener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent ) {
                int pageNo = intent.getIntExtra(Constants.KEY_INTENT_PAGE_NUMBER, 0);
                updateProgress(determineProgress(pageNo));
            }
        };
        IntentFilter intentFilter = new IntentFilter(Constants.KEY_UPDATE_PROGRESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, intentFilter);
    }

    private void initViews(View view){
        progressBar = (ProgressBar)view.findViewById(R.id.progressbar);
    }

    private void updateProgress(int progress){
        if(progressBar!=null){
            progressBar.setProgress(progress);
        }
    }

    private int determineProgress(int pageNo){
        switch (pageNo){
            case 1:
                return 32;
            case 2:
                return 68;
            case 3:
                return 100;
        }
        return 0;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerForBoradcastReceiver();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }
}
