package it.stez78.bandmates;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private final static String TAG = MainActivity.class.getSimpleName();

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private BandmateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = findViewById(R.id.main_activity_rw);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
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
}
