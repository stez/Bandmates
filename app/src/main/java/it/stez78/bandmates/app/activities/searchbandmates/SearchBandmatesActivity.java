package it.stez78.bandmates.app.activities.searchbandmates;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import it.stez78.bandmates.BandmatesAppViewModelFactory;
import it.stez78.bandmates.R;
import it.stez78.bandmates.app.adapters.BandmateAdapter;
import it.stez78.bandmates.model.Bandmate;

public class SearchBandmatesActivity extends AppCompatActivity implements HasSupportFragmentInjector, OnMapReadyCallback {

    private final static String TAG = SearchBandmatesActivity.class.getSimpleName();

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @BindView(R.id.activity_search_bandmates_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_search_bandmates_rw)
    RecyclerView recyclerView;

    private GoogleMap mMap;

    private RecyclerView.LayoutManager layoutManager;
    private BandmateAdapter adapter;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private SearchBandmatesViewModel viewModel;

    private List<Bandmate> bandmates = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_bandmates);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_search_bandmates_map);
        mapFragment.getMapAsync(this);
        setSupportActionBar(toolbar);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new BandmateAdapter(bandmates);
        recyclerView.setAdapter(adapter);

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(SearchBandmatesViewModel.class);
        viewModel.bandmatesLiveData().observe(this, bandmate -> {
            bandmates.add(bandmate);
            adapter.notifyDataSetChanged();
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng lugo = new LatLng(44.422150, 11.910800);
        mMap.addMarker(new MarkerOptions().position(lugo).title("Lugo"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(lugo,15f));
    }

    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
