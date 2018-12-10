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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.QuickContactBadge;

import com.binujayaram.coverassessment.Constants;
import com.binujayaram.coverassessment.R;
import com.binujayaram.coverassessment.view.activity.MainActivity;

public class NavigationButtonFragment extends Fragment {
    private ListenNavigation listenNavigation;
    private Button nextButton;
    BroadcastReceiver listener;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        listenNavigation = (ListenNavigation)context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.naviagtion_fragment, container, false);
        initViews(view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        registerForBoradcastReceiver();
    }

    private void registerForBoradcastReceiver(){
        listener = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent ) {
                int pageNo = intent.getIntExtra(Constants.KEY_INTENT_PAGE_NUMBER, 0);
               nextButton.setText(determineButtonText(pageNo));
            }
        };
        IntentFilter intentFilter = new IntentFilter(Constants.KEY_UPDATE_PROGRESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(listener, intentFilter);
    }

    private void initViews(View view){
        nextButton = (Button)view.findViewById(R.id.next_button);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenNavigation.onNavigate();
            }
        });
    }


    public interface ListenNavigation{
        public void onNavigate();
    }

    @Override
    public void onPause() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(listener);
        super.onPause();
    }

    private String determineButtonText(int pageNo){
        switch (pageNo){
            case 1:
                return getActivity().getString(R.string.text_next);
            case 2:
                return getActivity().getString(R.string.text_next);
            case 3:
                return getActivity().getString(R.string.finish_next);
        }
        return getActivity().getString(R.string.finish_next);
    }

}
