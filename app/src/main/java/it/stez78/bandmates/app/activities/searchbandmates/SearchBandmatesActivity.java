package it.stez78.bandmates.app.activities.searchbandmates;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import it.stez78.bandmates.R;
import it.stez78.bandmates.app.adapters.BandmateAdapter;
import timber.log.Timber;

public class SearchBandmatesActivity extends AppCompatActivity implements HasSupportFragmentInjector, OnMapReadyCallback {

    private final static String TAG = SearchBandmatesActivity.class.getSimpleName();
    private final static int RC_SIGN_IN = 1;

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @BindView(R.id.activity_search_bandmates_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_search_bandmates_rw)
    RecyclerView recyclerView;

    @BindView(R.id.activity_search_bandmates_logout_button)
    Button logoutButton;

    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;

    private RecyclerView.LayoutManager layoutManager;
    private BandmateAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SearchBandmatesViewModel viewModel;

    @Inject
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bandmates);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchBandmatesViewModel.class);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_search_bandmates_map);
        mapFragment.getMapAsync(this);

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.activity_search_place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 12.0f));
            }

            @Override
            public void onError(Status status) {

            }
        });
        setSupportActionBar(toolbar);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            LatLng newCameraPosition = new LatLng(location.getLatitude(),location.getLongitude());
                            googleMap.moveCamera(CameraUpdateFactory.newLatLng(newCameraPosition));
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCameraPosition, 10f));
                        }
                    }
                });

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BandmateAdapter(this, viewModel.getBandmates());
        recyclerView.setAdapter(adapter);
        viewModel.bandmatesLiveData().observe(this, bandmatesList -> {
            Timber.d("RECEIVED BANDMATES LIST: "+bandmatesList);
            viewModel.setBandmates(bandmatesList);
            adapter.notifyDataSetChanged();
            //geoFirestore.setLocation(bandmate.getId(),bandmate.getLatlon());
            /*adapter.notifyDataSetChanged();
            googleMap.addMarker(new MarkerOptions()
                    .position(new LatLng(bandmate.getLatlon().getLatitude(),bandmate.getLatlon().getLongitude()))
                    .title(bandmate.getName())
                    .snippet(bandmate.getInstrument()));*/
        });

        GeoFire geoFire = new GeoFire(firebaseDatabase.getReference("bandmates"));
//        geoFire.getLocation("059b318a-612d-4f1c-9895-09e63c0c4d9f", new LocationCallback() {
//            @Override
//            public void onLocationResult(String key, GeoLocation location) {
//                if (location != null) {
//                    System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
//                } else {
//                    System.out.println(String.format("There is no location for key %s in GeoFire", key));
//                }
//            }
//
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                System.err.println("There was an error getting the GeoFire location: " + databaseError);
//            }
//        });

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(44.35916, 11.7132), 100);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {

            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                Timber.d("DATA ENTERED " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {
                Timber.d("DATA EXITED " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                Timber.d("DATA MOVED " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                Timber.d("DATA CHANGED " + dataSnapshot.getValue().toString());
            }

            @Override
            public void onGeoQueryReady() {
                // ...
            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                // ...
            }

        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        if (viewModel.checkUserSignedIn()){
            toolbarForAuthenticatedUser();
        } else {
            toolbarForVisitors();
        }
        return true;
    }

    private void toolbarForAuthenticatedUser(){
        toolbar.getMenu().findItem(R.id.menu_profile_login).setVisible(false);
        toolbar.getMenu().findItem(R.id.menu_profile).setVisible(true);
    }

    private void toolbarForVisitors(){
        toolbar.getMenu().findItem(R.id.menu_profile_login).setVisible(true);
        toolbar.getMenu().findItem(R.id.menu_profile).setVisible(false);
    }

    public void logout(){
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        toolbarForVisitors();
                    }
                });
    }

    public void login(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.icon_bandmates)
                        .setTheme(R.style.AppTheme)
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        googleMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();

                LatLng farRight = visibleRegion.farRight;
                LatLng farLeft = visibleRegion.farLeft;
                LatLng nearRight = visibleRegion.nearRight;
                LatLng nearLeft = visibleRegion.nearLeft;

                float[] distanceWidth = new float[2];
                Location.distanceBetween(
                        (farRight.latitude+nearRight.latitude)/2,
                        (farRight.longitude+nearRight.longitude)/2,
                        (farLeft.latitude+nearLeft.latitude)/2,
                        (farLeft.longitude+nearLeft.longitude)/2,
                        distanceWidth
                );


                float[] distanceHeight = new float[2];
                Location.distanceBetween(
                        (farRight.latitude+nearRight.latitude)/2,
                        (farRight.longitude+nearRight.longitude)/2,
                        (farLeft.latitude+nearLeft.latitude)/2,
                        (farLeft.longitude+nearLeft.longitude)/2,
                        distanceHeight
                );

                float distance;

                if (distanceWidth[0]>distanceHeight[0]){
                    distance = distanceWidth[0];
                } else {
                    distance = distanceHeight[0];
                }

                LatLng center = googleMap.getCameraPosition().target;

                Toast.makeText(getApplicationContext(),"CENTRO CAMERA: "+center.toString()+" RAGGIO CAMERA: "+distance/1000,Toast.LENGTH_LONG).show();
                /*
                bandmates.clear();
                adapter.notifyDataSetChanged();
                GeoQuery geoQuery = geoFirestore.queryAtLocation(new GeoPoint(center.latitude,center.longitude),distance/1000);
                geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {
                    @Override
                    public void onDocumentEntered(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {
                        Bandmate bandmate =documentSnapshot.toObject(Bandmate.class);
                        bandmates.add(bandmate);
                        googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(bandmate.getLatlon().getLatitude(),bandmate.getLatlon().getLongitude()))
                                .title(bandmate.getName())
                                .snippet(bandmate.getInstrument()));
                    }

                    @Override
                    public void onDocumentExited(DocumentSnapshot documentSnapshot) {

                    }

                    @Override
                    public void onDocumentMoved(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                    }

                    @Override
                    public void onDocumentChanged(DocumentSnapshot documentSnapshot, GeoPoint geoPoint) {

                    }

                    @Override
                    public void onGeoQueryReady() {
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onGeoQueryError(Exception e) {

                    }
                });*/
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_profile_popup_logout:
                logout();
                return true;
            case R.id.menu_profile_login:
                login();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            if (resultCode == RESULT_OK) {
                toolbarForAuthenticatedUser();
            } else {
                toolbarForVisitors();
            }
        }
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
