package com.teambee.travelbuddy;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.api.Status;
import android.location.LocationListener;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, View.OnClickListener, LocationListener {

    private static final String TAG = MapsActivity.class.getSimpleName();
    private GoogleMap mMap;
    private LocationManager myLocationManager;
    private int RADIUS = 1000;
    private double mLat = 0.0, mLon = 0.0;
    private Location mLocation;
    private Boolean isFabOpen = false;
    private FloatingActionButton fab,fab1,fab2,fab3,fab4, route_fab;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    private DrawerLayout drawerLayout;
    private Toolbar toolbar;
    private View placeBottomSheetView;
    private BottomSheetBehavior placeBottomSheetBehavior;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // setting the toolbar
        toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar);
        // toolbar.getBackground().setAlpha(0);
        setSupportActionBar(toolbar);
        initNavigationDrawer();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab1 = (FloatingActionButton)findViewById(R.id.fab1);
        fab2 = (FloatingActionButton)findViewById(R.id.fab2);
        fab3 = (FloatingActionButton)findViewById(R.id.fab3);
        fab4 = (FloatingActionButton)findViewById(R.id.fab4);
        route_fab = (FloatingActionButton)findViewById(R.id.route_fab);
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotate_forward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotate_backward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fab1.setOnClickListener(this);
        fab2.setOnClickListener(this);
        fab3.setOnClickListener(this);
        fab4.setOnClickListener(this);
        route_fab.setOnClickListener(this);

        // adding the bottom view which displays a place information
        placeBottomSheetView = findViewById(R.id.design_bottom_sheet);
        // variable to control the bottom sheet behavior
        placeBottomSheetBehavior = BottomSheetBehavior.from(placeBottomSheetView);
        placeBottomSheetBehavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {
                switch (newState) {
                    case BottomSheetBehavior.STATE_DRAGGING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_DRAGGING");
                        // this part hides the button immediately
                        // route_fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
                        break;
                    case BottomSheetBehavior.STATE_SETTLING:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_SETTLING");
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_EXPANDED");
                        // route_fab.setVisibility(View.INVISIBLE);
                        break;
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_COLLAPSED");
                        // waits bottom sheet to collapse to show
                        route_fab.animate().scaleX(1).scaleY(1).setDuration(300).start();
                        break;
                    case BottomSheetBehavior.STATE_HIDDEN:
                        Log.i("BottomSheetCallback", "BottomSheetBehavior.STATE_HIDDEN");
                        route_fab.setVisibility(View.INVISIBLE);
                        break;
                } // end switch
            } // end of onStateChanged

            // controls what happen while the bottom sheet is sliding
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                Log.i("BottomSheetCallback", "slideOffset: " + slideOffset);
                if (slideOffset >= 0.2 && slideOffset <= 0.6){
//                    placeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_SETTLING);
                }
                route_fab.animate().scaleX(1 - slideOffset).scaleY(1 - slideOffset).setDuration(0).start();
                //
                // route_fab.animate().scaleX(0).scaleY(0).setDuration(300).start();
            }
        }); // en of setBottomSheetCallback

        // Adding the autocomplete place search box supporter fom google to the toolbar
        PlaceAutocompleteFragment autocompleteFrag = (PlaceAutocompleteFragment)getFragmentManager()
                .findFragmentById(R.id.place_autocomplete_fragment);

        // Adding a listener for actions when a place is selected
        autocompleteFrag.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place Selected Name: " + place.getName());
                Log.i(TAG, "Place Selected Name: " + place.getLatLng().latitude + place.getLatLng().longitude);

                // Add a marker to demoLoc and move the camera
                LatLng demoLoc = place.getLatLng();
                mMap.addMarker(new MarkerOptions().position(demoLoc).title(place.getName().toString()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(demoLoc));

                placeBottomSheetBehavior.setPeekHeight(150);
                placeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                route_fab.setVisibility(View.VISIBLE);

//                if (placeBottomSheetBehavior.getState() == BottomSheetBehavior.STATE_COLLAPSED) {
//                    placeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
//                } else {
//                    placeBottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
//                }

            } // end of onPlaceSelectedListener

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.e(TAG, "onError: Status = " + status.toString());

                Toast.makeText(MapsActivity.this, "Place selection failed: " + status.getStatusMessage(), Toast.LENGTH_SHORT).show();

            }// end of onError

        }); // end of onPLaceSelectedListener


    } // end of onCreate

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        // enable or disable toolbar in the bottom right corner of the map
        googleMap.getUiSettings().setMapToolbarEnabled(false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return;
        }
        else {
            mMap.setMyLocationEnabled(true);

            myLocationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            Criteria criteria = new Criteria();

            mLocation = myLocationManager.getLastKnownLocation(myLocationManager.getBestProvider(criteria, true));

            if (null != mLocation) {
                mLocation = myLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                LatLng pinCurrentLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
                mMap.addMarker(new MarkerOptions().position(pinCurrentLocation).title("Starting Point"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(pinCurrentLocation));
            } else {
                myLocationManager.requestLocationUpdates(myLocationManager.getBestProvider(criteria,true), 6000, 10, this);
            }
        }
    } // en of onMapReady

    @Override
    public void onLocationChanged(Location location) {
        Log.d("Value of Location onLocationChanged", location.toString());
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        myLocationManager.removeUpdates(this);
        mLat = mLocation.getLongitude();
        mLon = mLocation.getLatitude();
        LatLng pinCurrentLocation = new LatLng(mLocation.getLatitude(), mLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(pinCurrentLocation).title("Starting Point"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pinCurrentLocation));
        Log.d("what are the locations", String.valueOf(mLat) + String.valueOf(mLon));
    } // end of on location Change

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    //FAB BUTTONS/////////////////////////////////
    @Override
    public void onClick(View v) {
        String type = "";
        int id = v.getId();
        switch (id){
            case R.id.fab:
                animateFAB();
                break;
            case R.id.fab1:
                type = "restaurant";
                startLayerDisplay(type);
                break;
            case R.id.fab2:
                type = "hotel";
                startLayerDisplay(type);
                break;
            case R.id.fab3:
                type = "gas_station";
                startLayerDisplay(type);
                break;
            case R.id.fab4:
                type = "park";
                startLayerDisplay(type);
                break;
        }
    }

    public void startLayerDisplay(String layerType){
        mMap.clear();
        Log.d("locations on switch", String.valueOf(mLat) + String.valueOf(mLon));
        LayerSwitcher layerSwitcher = new LayerSwitcher(this, mMap, mLocation.getLatitude(), mLocation.getLongitude(), RADIUS, layerType);
        layerSwitcher.startAsyncTasks();
    }

    public void animateFAB(){
        if(isFabOpen){
            fab.startAnimation(rotate_backward);
            fab1.startAnimation(fab_close);
            fab2.startAnimation(fab_close);
            fab3.startAnimation(fab_close);
            fab4.startAnimation(fab_close);
            fab1.setClickable(false);
            fab2.setClickable(false);
            fab3.setClickable(false);
            fab4.setClickable(false);
            isFabOpen = false;
        } else {

            fab.startAnimation(rotate_forward);
            fab1.startAnimation(fab_open);
            fab2.startAnimation(fab_open);
            fab3.startAnimation(fab_open);
            fab4.startAnimation(fab_open);
            fab1.setClickable(true);
            fab2.setClickable(true);
            fab3.setClickable(true);
            fab4.setClickable(true);
            isFabOpen = true;
        }
    }

    //DRAWER LAYOUT///////////////////////////////////
    public void initNavigationDrawer() {
        NavigationView navigationView = (NavigationView)findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                int id = menuItem.getItemId();

                switch (id){
                    case R.id.home:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.destination:
                        drawerLayout.closeDrawers();
                    case R.id.history:
                        drawerLayout.closeDrawers();
                    case R.id.clearRoute:
                        drawerLayout.closeDrawers();
                        break;
                    case R.id.settings:
                        drawerLayout.closeDrawers();
                        break;
                }
                return true;
            }
        });

        View header = navigationView.getHeaderView(0);
        TextView welcometxt = (TextView)header.findViewById(R.id.welcome_user);
        welcometxt.append(" Team B");
        drawerLayout = (DrawerLayout)findViewById(R.id.drawer);

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.drawer_open,R.string.drawer_close){
            @Override
            public void onDrawerClosed(View v){
                super.onDrawerClosed(v);
            }
            @Override
            public void onDrawerOpened(View v) {
                super.onDrawerOpened(v);
            }
        };
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
    }

}