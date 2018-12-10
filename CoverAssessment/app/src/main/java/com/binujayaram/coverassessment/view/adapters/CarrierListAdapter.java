package com.binujayaram.coverassessment.view.adapters;

import android.content.Context;
import android.graphics.Movie;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.binujayaram.coverassessment.R;

import java.util.List;

public class CarrierListAdapter extends RecyclerView.Adapter<CarrierListAdapter.CarrierViewHolder>{

private List<String> carrierList;
private ListenCarrierSelection listenCarrierSelection;

public class CarrierViewHolder extends RecyclerView.ViewHolder {
    private TextView title;
    private RelativeLayout itemContainer;

    public CarrierViewHolder(View view) {
        super(view);
        title = (TextView) view.findViewById(R.id.carrier_name_tv);
        itemContainer = (RelativeLayout)view.findViewById(R.id.list_item_container);
    }

}


    public CarrierListAdapter(ListenCarrierSelection listenCarrierSelection, List<String> carrierList) {
        this.carrierList = carrierList;
        this.listenCarrierSelection = listenCarrierSelection;
    }

    @Override
    public CarrierViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.carrier_list_item, parent, false);

        return new CarrierViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CarrierViewHolder holder, final int position) {
        holder.title.setText(carrierList.get(position));
        holder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenCarrierSelection.onCarrierSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return carrierList.size();
    }

    public interface ListenCarrierSelection{
        public void onCarrierSelected(int position);
    }
}