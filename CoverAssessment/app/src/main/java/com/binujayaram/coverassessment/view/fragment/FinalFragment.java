package com.binujayaram.coverassessment.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.binujayaram.coverassessment.R;
import com.binujayaram.coverassessment.dataservice.DBManager;

public class FinalFragment extends Fragment {

    private final int pageNo = 3;
    private TextView placeTV, carrierTV;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.final_fragment, container, false);
        initViews(view);
        setSelectedValues();
        return view;
    }

    private void initViews(View view) {
        placeTV = (TextView)view.findViewById(R.id.selected_place_tv);
        carrierTV = (TextView)view.findViewById(R.id.selected_carrier_tv);
    }

    private void setSelectedValues(){
        placeTV.setText(DBManager.getSelectedPlace(getContext().getApplicationContext()));
        carrierTV.setText(DBManager.getSelectedCarrier(getContext().getApplicationContext()));
    }

    public int getPageNo() {
        return pageNo;
    }


}
