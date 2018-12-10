package com.binujayaram.coverassessment.view.fragment;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.binujayaram.coverassessment.R;
import com.binujayaram.coverassessment.dataservice.DBManager;
import com.binujayaram.coverassessment.interfaces.Response;
import com.binujayaram.coverassessment.models.Carriers;
import com.binujayaram.coverassessment.utils.Utility;
import com.binujayaram.coverassessment.view.adapters.CarrierListAdapter;

import java.util.Arrays;
import java.util.List;

public class CarrierSearchFragment extends Fragment implements CarrierListAdapter.ListenCarrierSelection {
    private RecyclerView recyclerView;
    private final int pageNo = 2;
    private ProgressDialog progressDialog;
    private List<String> carriersList;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.carriers_fragment, container, false);
        initViews(view);
        loadCarrierList();
        return view;
    }

    public int getPageNo() {
        return pageNo;
    }
    private void initViews(View view){
        recyclerView = (RecyclerView)view.findViewById(R.id.carriers_recyclerview);
    }

    private void showProgress(String message){
        if(progressDialog == null){
            progressDialog = new ProgressDialog(getContext());
        }
        progressDialog.setMessage(message);
        progressDialog.show();

    }

    private void hideProgress(){
        if(progressDialog != null){
            progressDialog.dismiss();
        }
    }

    private void loadCarrierList(){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgress(getString(R.string.progress_text));
            }
        });
        DBManager.loadJSONFromAsset(getContext(), new Response() {
            @Override
            public void onSuccess(final Carriers carriers) {
                carriersList = Arrays.asList(carriers.getInsurance_carriers());
              getActivity().runOnUiThread(new Runnable() {
                  @Override
                  public void run() {
                      loadRecyclerView(carriers);
                      hideProgress();
                  }
              });
            }

            @Override
            public void onFaliure(Carriers carriers) {
                hideProgress();
            }
        });
    }

    private void loadRecyclerView(Carriers carriers){
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                linearLayoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);

        List<String> carrierList = Arrays.asList(carriers.getInsurance_carriers());
        CarrierListAdapter carrierListAdapter = new CarrierListAdapter((CarrierListAdapter.ListenCarrierSelection)this, carrierList);
        recyclerView.setAdapter(carrierListAdapter);
    }

    @Override
    public void onCarrierSelected(int position) {
        DBManager.saveSelectedCarrier(getActivity().getApplicationContext(), carriersList.get(position) );
        Utility.showToast(getContext(), getString(R.string.selection_saved_toast_message));
    }
}
