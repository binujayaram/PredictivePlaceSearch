package com.binujayaram.coverassessment.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.binujayaram.coverassessment.R;
import com.binujayaram.coverassessment.dataservice.DBManager;
import com.binujayaram.coverassessment.models.PlaceInfo;
import com.binujayaram.coverassessment.utils.Utility;
import com.binujayaram.coverassessment.view.adapters.PlaceAutocompleteAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

public class AddressSearchFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener, PlaceAutocompleteAdapter.ListenPlaceSelection {
    public int getPageNo() {
        return pageNo;
    }

    private final int pageNo = 1;
    private String TAG = AddressSearchFragment.class.getSimpleName();
    private GoogleApiClient mGoogleApiClient;
    private AutoCompleteTextView mSearchText;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private PlaceInfo mPlace;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 136));


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.address_search_fragment, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        init();
    }

    private void initViews(View view){
        mSearchText = (AutoCompleteTextView)view.findViewById(R.id.input_search);

        recyclerView = (RecyclerView)view.findViewById(R.id.address_recyclerview);
        recyclerView.setHasFixedSize(true);
        linearLayoutManager = new LinearLayoutManager(getContext());
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
//                linearLayoutManager.getOrientation());
//        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.setLayoutManager(linearLayoutManager);

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    private void init(){
        Log.d(TAG, "init: initializing");

        mGoogleApiClient = new GoogleApiClient
                .Builder(getContext())
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .enableAutoManage(getActivity(),this)
                .build();

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(getContext(), mGoogleApiClient,
                LAT_LNG_BOUNDS, null,(PlaceAutocompleteAdapter.ListenPlaceSelection)this);

        recyclerView.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (count > 0) {
                    if (mPlaceAutocompleteAdapter != null) {
                        recyclerView.setAdapter(mPlaceAutocompleteAdapter);
                    }
                }
                if (!s.toString().equals("") && mGoogleApiClient.isConnected()) {
                    mPlaceAutocompleteAdapter.getFilter().filter(s.toString());
                } else if (!mGoogleApiClient.isConnected()) {
//                    Toast.makeText(getApplicationContext(), Constants.API_NOT_CONNECTED, Toast.LENGTH_SHORT).show();
                    Log.e("", "NOT CONNECTED");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        hideSoftKeyboard();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    }

    private void hideSoftKeyboard(){
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

        /*
        --------------------------- google places API autocomplete suggestions -----------------
     */

    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            PendingResult<PlaceBuffer> placeResult = Places.GeoDataApi
                    .getPlaceById(mGoogleApiClient, placeId);
            placeResult.setResultCallback(mUpdatePlaceDetailsCallback);
        }
    };

    private ResultCallback<PlaceBuffer> mUpdatePlaceDetailsCallback = new ResultCallback<PlaceBuffer>() {
        @Override
        public void onResult(@NonNull PlaceBuffer places) {
            if(!places.getStatus().isSuccess()){
                Log.d(TAG, "onResult: Place query did not complete successfully: " + places.getStatus().toString());
                places.release();
                return;
            }
            final Place place = places.get(0);

            try{
                mPlace = new PlaceInfo();
                mPlace.setName(place.getName().toString());
                Log.d(TAG, "onResult: name: " + place.getName());
                mPlace.setAddress(place.getAddress().toString());
                Log.d(TAG, "onResult: address: " + place.getAddress());
//                mPlace.setAttributions(place.getAttributions().toString());
//                Log.d(TAG, "onResult: attributions: " + place.getAttributions());
                mPlace.setId(place.getId());
                Log.d(TAG, "onResult: id:" + place.getId());
                mPlace.setLatlng(place.getLatLng());
                Log.d(TAG, "onResult: latlng: " + place.getLatLng());
                mPlace.setRating(place.getRating());
                Log.d(TAG, "onResult: rating: " + place.getRating());
                mPlace.setPhoneNumber(place.getPhoneNumber().toString());
                Log.d(TAG, "onResult: phone number: " + place.getPhoneNumber());
                mPlace.setWebsiteUri(place.getWebsiteUri());
                Log.d(TAG, "onResult: website uri: " + place.getWebsiteUri());

                Log.d(TAG, "onResult: place: " + mPlace.toString());
            }catch (NullPointerException e){
                Log.e(TAG, "onResult: NullPointerException: " + e.getMessage() );
            }

//            moveCamera(new LatLng(place.getViewport().getCenter().latitude,
//                    place.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace);

            places.release();
        }
    };


    @Override
    public void onPause() {
        super.onPause();
        mGoogleApiClient.stopAutoManage(getActivity());
        mGoogleApiClient.disconnect();
    }

    @Override
    public void onPlaceSelected(int position) {
        mPlaceAutocompleteAdapter.mResultList.get(position).getPrimaryText(null);
        DBManager.saveSelectedPlace(getContext().getApplicationContext()
                ,mPlaceAutocompleteAdapter.mResultList.get(position).getPrimaryText(null).toString() );
        Utility.showToast(getContext(), getString(R.string.selection_saved_toast_message));
        Log.i(TAG,"Selected place: "+mPlaceAutocompleteAdapter.mResultList.get(position).getPrimaryText(null));
    }
}
