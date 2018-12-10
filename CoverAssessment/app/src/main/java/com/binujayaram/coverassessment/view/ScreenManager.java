package com.binujayaram.coverassessment.view;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;

import com.binujayaram.coverassessment.Constants;
import com.binujayaram.coverassessment.R;
import com.binujayaram.coverassessment.dataservice.DBManager;
import com.binujayaram.coverassessment.utils.Utility;
import com.binujayaram.coverassessment.view.activity.MainActivity;
import com.binujayaram.coverassessment.view.fragment.AddressSearchFragment;
import com.binujayaram.coverassessment.view.fragment.CarrierSearchFragment;
import com.binujayaram.coverassessment.view.fragment.FinalFragment;

public class ScreenManager {
    private Context context;
    private ScreenManagerEvents screenManagerEvents;
    public ScreenManager(Context context, ScreenManagerEvents screenManagerEvents){
        this.context = context;
        this.screenManagerEvents = screenManagerEvents;
    }

    private void loadAddressSearchFragment(){
        AddressSearchFragment addressSearchFragment = new AddressSearchFragment();

        screenManagerEvents.broadcastFragmentChangeEvent(addressSearchFragment.getPageNo(),
                Constants.KEY_UPDATE_PROGRESS, Constants.KEY_INTENT_PAGE_NUMBER);
        DBManager.setCurrentFragment(context.getApplicationContext(), 1);
        screenManagerEvents.loadFragmentEvent(addressSearchFragment);
        screenManagerEvents.showToolbarUpIcon(false);
    }

    private void loadCarrierSearchFragment(){
        CarrierSearchFragment carrierSearchFragment = new CarrierSearchFragment();
        screenManagerEvents.broadcastFragmentChangeEvent(carrierSearchFragment.getPageNo(),
                Constants.KEY_UPDATE_PROGRESS, Constants.KEY_INTENT_PAGE_NUMBER);
        DBManager.setCurrentFragment(context.getApplicationContext(), 2);
        screenManagerEvents.loadFragmentEvent(carrierSearchFragment);
        screenManagerEvents.showToolbarUpIcon(true);
    }

    private void loadFinalFragment(){
        FinalFragment finalFragment = new FinalFragment();
        screenManagerEvents.broadcastFragmentChangeEvent(finalFragment.getPageNo(),
                Constants.KEY_UPDATE_PROGRESS, Constants.KEY_INTENT_PAGE_NUMBER);
        DBManager.setCurrentFragment(context.getApplicationContext(), 3);
        screenManagerEvents.loadFragmentEvent(finalFragment);
        screenManagerEvents.showToolbarUpIcon(false);
    }

    public void calculateAndLoadFragment(boolean isNext){
        int cf = DBManager.getCurrentFragment(context.getApplicationContext());
        switch (cf){
            case 0:
                if(isNext){
                    loadAddressSearchFragment();
                }
                break;
            case 1:
                if(isNext){
                    if(!DBManager.getSelectedPlace(context.getApplicationContext()).equals(null)
                            && !DBManager.getSelectedPlace(context.getApplicationContext()).equals("")){
                        loadCarrierSearchFragment();
                    }
                    else {
                        Utility.showAlertDialog(context, context.getString(R.string.no_selection_dialog_title),
                                context.getString(R.string.no_selection_dialog_message));
                    }
                }
                break;
            case 2:
                if(isNext){
                    if(!DBManager.getSelectedCarrier(context.getApplicationContext()).equals(null)
                            && !DBManager.getSelectedCarrier(context.getApplicationContext()).equals("")){
                        loadFinalFragment();
                    }
                    else {
                        Utility.showAlertDialog(context, context.getString(R.string.no_selection_dialog_title),
                                context.getString(R.string.no_selection_dialog_message));
                    }
                }
                else {
                    loadAddressSearchFragment();
                }
                break;
            case 3:
                screenManagerEvents.exitApp();
                break;
            default:
                loadFinalFragment();
                break;
        }
    }

    public interface ScreenManagerEvents{
        public void broadcastFragmentChangeEvent(int pageNo, String keyUpdateProgress, String keyPageNo);
        public void loadFragmentEvent(Fragment fragment);
        public void showToolbarUpIcon(boolean doShow);
        public void exitApp();
    }

}
