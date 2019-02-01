package it.stez78.bandmates.app.activities.searchbandmates;

import android.Manifest;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.VisibleRegion;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.SphericalUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Logger;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import it.stez78.bandmates.AppConfig;
import it.stez78.bandmates.R;
import it.stez78.bandmates.app.adapters.BandmateAdapter;
import it.stez78.bandmates.app.adapters.OnBandmateAdapterItemClickListener;
import it.stez78.bandmates.app.fragments.bandmatepreviewdialog.BandmatePreviewDialogFragment;
import it.stez78.bandmates.model.Bandmate;
import timber.log.Timber;

public class SearchBandmatesActivity extends AppCompatActivity implements HasSupportFragmentInjector, OnMapReadyCallback, OnBandmateAdapterItemClickListener {

    private final static String TAG = "searchBandmatesActivity";
    private final static int RC_SIGN_IN = 1;
    static final int RC_PERMISSION_LOCATION = 2;


    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @BindView(R.id.activity_search_bandmates_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_search_bandmates_rw)
    RecyclerView recyclerView;

    @BindView(R.id.activity_search_bandmates_logout_button)
    Button logoutButton;

    @BindView(R.id.activity_search_bandmates_bottom_nav)
    BottomNavigationView bottomNavigationView;

    private GoogleMap googleMap;
    private HashMap<String, Marker> keyMarkerMap = new HashMap<>();
    private FusedLocationProviderClient fusedLocationProviderClient;

    private RecyclerView.LayoutManager layoutManager;
    private BandmateAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SearchBandmatesViewModel viewModel;

    @Inject
    FirebaseDatabase firebaseDatabase;

    @Inject
    GeoFire geoFire;

    private Context ctx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ctx = this;
        setContentView(R.layout.activity_search_bandmates);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchBandmatesViewModel.class);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_search_bandmates_map);
        mapFragment.getMapAsync(this);
        setupPlaceAutocompleteFragment();
        setSupportActionBar(toolbar);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, RC_PERMISSION_LOCATION);
            return;
        } else {
            moveCameraToCurrentPosition();
        }
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BandmateAdapter(this, viewModel.getBandmates(),this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case RC_PERMISSION_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    moveCameraToCurrentPosition();
                } else {
                    // permission denied
                }
            }
        }
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
        bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private void toolbarForVisitors(){
        toolbar.getMenu().findItem(R.id.menu_profile_login).setVisible(true);
        toolbar.getMenu().findItem(R.id.menu_profile).setVisible(false);
        bottomNavigationView.setVisibility(View.GONE);
    }

    public void logout(){
        AuthUI.getInstance()
            .signOut(this)
            .addOnCompleteListener(task -> toolbarForVisitors());
    }

    public void login(){
        List<AuthUI.IdpConfig> providers = Arrays.asList(new AuthUI.IdpConfig.EmailBuilder().build());
        startActivityForResult(
            AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setLogo(R.drawable.icon_bandmates)
                    .setTheme(R.style.AppTheme)
                    .setAvailableProviders(providers)
                    .build(),
            RC_SIGN_IN);
    }

    private void setupPlaceAutocompleteFragment() {
        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.activity_search_place_autocomplete_fragment);
        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                googleMap.clear();
                googleMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                googleMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
                googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 10.0f));
            }

            @Override
            public void onError(Status status) {

            }
        });
    }

    private void moveCameraToCurrentPosition(){
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        LatLng newCameraPosition = new LatLng(location.getLatitude(),location.getLongitude());
                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(newCameraPosition));
                        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(newCameraPosition, 5f));
                    }
        });
    }

    private double zoomLevelToRadius(double zoomLevel) {
        // Approximation
        return 591657550.5/Math.pow(2, zoomLevel-1);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        viewModel.setBandmates(new ArrayList<>());
        adapter.notifyDataSetChanged();
        LatLng center = googleMap.getCameraPosition().target;
        double radius = 1000;
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(center.latitude,center.longitude), radius);
        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
            @Override
            public void onKeyEntered(String key, GeoLocation location) {
                Timber.d("GEOFIRE ENTERED "+key);
                DatabaseReference bandmateRef = firebaseDatabase.getReference(AppConfig.FIREBASE_DATABASE_BANDAMATES_DB_REF).child(key);
                bandmateRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Bandmate bandmate = dataSnapshot.getValue(Bandmate.class);
                        viewModel.addBandmate(bandmate);
                        adapter.notifyDataSetChanged();
                        Marker m = googleMap.addMarker(new MarkerOptions()
                                .position(new LatLng(bandmate.getLat(),bandmate.getLon()))
                                .title(bandmate.getName())
                                .snippet(bandmate.getInstrument()));
                        keyMarkerMap.put(key,m);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onKeyExited(String key) {
                Timber.d("GEOFIRE EXITED "+key);
                viewModel.removeBandmateById(key);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onKeyMoved(String key, GeoLocation location) {

            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {

            }
        });

        googleMap.setOnCameraIdleListener(() -> {
            VisibleRegion visibleRegion = googleMap.getProjection().getVisibleRegion();
            double distance = SphericalUtil.computeDistanceBetween(
                    visibleRegion.farLeft, googleMap.getCameraPosition().target);
            LatLng center1 = googleMap.getCameraPosition().target;
            Toast.makeText(getApplicationContext(),"CENTER: " + center1.latitude + "," + center1.longitude + "- ZOOM: " + googleMap.getCameraPosition().zoom + " - DISTANCE: "+ distance/1000,Toast.LENGTH_SHORT).show();
            geoQuery.setCenter(new GeoLocation(center1.latitude, center1.longitude));
            geoQuery.setRadius(distance/1000);
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
            case R.id.menu_profile_populatedata:
                Random random = new Random();
                int howMany = random.nextInt(100)+1;
                showRandomDataDialog(howMany);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void showRandomDataDialog(int howMany){
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.random_data_title)
                .setMessage(getString(R.string.generate_random_data_message, howMany))
                .setPositiveButton(R.string.yes_please, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(),getString(R.string.generating_random_data,howMany),Toast.LENGTH_LONG).show();
                        viewModel.generateBandmates(howMany);
                    }
                })
                .setNegativeButton(R.string.no_thanks, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .create();
        dialog.show();
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
    public void onBandmateAdapterItemClick(Bandmate item) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag(BandmatePreviewDialogFragment.TAG);
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        BandmatePreviewDialogFragment bandmatePreviewDialogFragment = new BandmatePreviewDialogFragment();
        Bundle params = new Bundle();
        params.putParcelable(Bandmate.BANDMATE_PARCELABLE_KEY, item);
        bandmatePreviewDialogFragment.setArguments(params);
        bandmatePreviewDialogFragment.show(ft, BandmatePreviewDialogFragment.TAG);
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
