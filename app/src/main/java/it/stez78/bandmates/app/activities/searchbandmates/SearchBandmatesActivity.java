package it.stez78.bandmates.app.activities.searchbandmates;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.stez78.bandmates.R;
import it.stez78.bandmates.app.adapters.BandmateAdapter;
import it.stez78.bandmates.model.Bandmate;

public class SearchBandmatesActivity extends AppCompatActivity implements OnMapReadyCallback {

    private final static String TAG = SearchBandmatesActivity.class.getSimpleName();

    @BindView(R.id.activity_main_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_main_rw)
    RecyclerView recyclerView;

    private GoogleMap mMap;

    private RecyclerView.LayoutManager layoutManager;
    private BandmateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_map);
        mapFragment.getMapAsync(this);
        setSupportActionBar(toolbar);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("bandmates")
                .whereEqualTo("publicProfile", true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }
                        List<Bandmate> bandmates = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : value) {
                            Bandmate bandmate = doc.toObject(Bandmate.class);
                            bandmates.add(bandmate);
                        }
                        adapter = new BandmateAdapter(bandmates);
                        recyclerView.setAdapter(adapter);
                        adapter.notifyDataSetChanged();

                    }
                });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}
