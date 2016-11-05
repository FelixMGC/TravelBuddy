package com.teambee.travelbuddy;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;

/**
 * Created by Jimmy on 10/24/2016.
 */

public class LayerSwitcher {

    private String mType;
    private GoogleMap mMap;
    private Activity mActivity;
    private double mLat, mLong;
    private int mRadius;

    public LayerSwitcher(Activity activity, GoogleMap map, double latitude, double longitude, int radius, String layer) {
        mActivity = activity;
        mMap = map;
        mLat = latitude;
        mLong = longitude;
        mRadius = radius;
        mType = layer;
    }
    public void setLayer(String layer) {
        String mLayer = layer;
    }

    public String getLayer(){
        return mType;
    }

    public void startAsyncTasks(){
        String url = getUrl(mLat, mLong, mType);
        Log.d("variables******", String.valueOf(mLat));
        Log.d("variables******", String.valueOf(mLong));
        Log.d("variables******", mType);
        Object[] DataTransfer = new Object[2];
        DataTransfer[0] = mMap;
        DataTransfer[1] = url;
        Log.d("onClick", url);
        GetLayerPlacesData getLayerPlacesData = new GetLayerPlacesData();
        getLayerPlacesData.execute(DataTransfer);
        Toast.makeText(mActivity,"Displaying " + mType +"s nearby..." , Toast.LENGTH_LONG).show();
    }

    private String getUrl(double latitude, double longitude, String layerPlaces) {

        StringBuilder googlePlacesUrl = new StringBuilder("https://maps.googleapis.com/maps/api/place/nearbysearch/json?");
        String KEY = mActivity.getString(R.string.google_maps_key);
        googlePlacesUrl.append("location=" + latitude + "," + longitude);
        googlePlacesUrl.append("&radius=" + mRadius);
        googlePlacesUrl.append("&type=" + layerPlaces);
        googlePlacesUrl.append("&sensor=true");
        googlePlacesUrl.append("&key=" + KEY);
        Log.d("getUrl", googlePlacesUrl.toString());
        return (googlePlacesUrl.toString());
    }
}
