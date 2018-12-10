package com.binujayaram.coverassessment.view.adapters;

/*
 * Copyright (C) 2015 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */


import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.binujayaram.coverassessment.R;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.common.data.DataBufferUtils;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Adapter that handles Autocomplete requests from the Places Geo Data API.
 * {@link AutocompletePrediction} results from the API are frozen and stored directly in this
 * adapter. (See {@link AutocompletePrediction#freeze()}.)
 * <p>
 * Note that this adapter requires a valid {@link com.google.android.gms.common.api.GoogleApiClient}.
 * The API client must be maintained in the encapsulating Activity, including all lifecycle and
 * connection states. The API client must be connected with the {@link Places#GEO_DATA_API} API.
 */
public class PlaceAutocompleteAdapter extends RecyclerView.Adapter<PlaceAutocompleteAdapter.PlaceViewHolder> implements Filterable{

    public interface PlaceAutoCompleteInterface{
        public void onPlaceClick(ArrayList<PlaceAutocomplete> mResultList, int position);
    }

    Context mContext;
  //  PlaceAutoCompleteInterface mListener;
    private static final String TAG = PlaceAutocompleteAdapter.class.getSimpleName();
    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
    public ArrayList<AutocompletePrediction> mResultList;

    private GoogleApiClient mGoogleApiClient;

    private LatLngBounds mBounds;



    private AutocompleteFilter mPlaceFilter;

    private ListenPlaceSelection listenPlaceSelection;



    public PlaceAutocompleteAdapter(Context context, GoogleApiClient googleApiClient,
                                    LatLngBounds bounds, AutocompleteFilter filter, ListenPlaceSelection listenPlaceSelection){
        this.mContext = context;
        this.listenPlaceSelection = listenPlaceSelection;
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
     //   this.mListener = (PlaceAutoCompleteInterface)getA;
    }


    /**
     * Sets the bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    //notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient.isConnected()) {
            Log.i(TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at most 60s
            // for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(mContext, "Error contacting API: " + status.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Freeze the results immutable representation that can be stored safely.
            return DataBufferUtils.freezeAndClose(autocompletePredictions);
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }

    @Override
    public PlaceViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        LayoutInflater layoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View convertView = layoutInflater.inflate(R.layout.address_list_item, viewGroup, false);
        PlaceViewHolder mPredictionHolder = new PlaceViewHolder(convertView);
        return mPredictionHolder;
    }


    @Override
    public void onBindViewHolder(PlaceViewHolder mPredictionHolder, final int i) {
        mPredictionHolder.textView1.setText(mResultList.get(i).getPrimaryText(STYLE_BOLD));

        mPredictionHolder.textView2.setText(mResultList.get(i).getSecondaryText(STYLE_BOLD));
        mPredictionHolder.itemContainer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listenPlaceSelection.onPlaceSelected(i);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(mResultList != null)
            return mResultList.size();
        else
            return 0;
    }

    public AutocompletePrediction getItem(int position) {
        return mResultList.get(position);
    }

    /*
    View Holder For Trip History
     */
    public class PlaceViewHolder extends RecyclerView.ViewHolder {
        //        CardView mCardView;
        public TextView textView1;
        public TextView textView2;
        public RelativeLayout itemContainer;

        public PlaceViewHolder(View itemView) {
            super(itemView);
            textView1 = (TextView) itemView.findViewById(R.id.text1);
            textView2 = (TextView)itemView.findViewById(R.id.text2);
            itemContainer = (RelativeLayout)itemView.findViewById(R.id.item_container);
        }

    }

    /**
     * Holder for Places Geo Data Autocomplete API results.
     */
    public class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }

    public interface ListenPlaceSelection{
        public void onPlaceSelected(int position);
    }
}