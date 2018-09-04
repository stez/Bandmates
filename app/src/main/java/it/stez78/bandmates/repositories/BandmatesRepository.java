package it.stez78.bandmates.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.stez78.bandmates.app.adapters.BandmateAdapter;
import it.stez78.bandmates.model.Bandmate;

@Singleton
public class BandmatesRepository {

    private final static String FIRESTORE_BANDMATES_COLLECTION_NAME = "bandmates";
    private final static String PUBLIC_PROFILE_FIELD = "publicProfile";

    private FirebaseFirestore db;

    @Inject
    public BandmatesRepository(FirebaseFirestore db){
        this.db = db;
    }

    public LiveData<Bandmate> updatedPublicProfilesLivedata(){
        MutableLiveData<Bandmate> res = new MutableLiveData<>();
        db.collection(FIRESTORE_BANDMATES_COLLECTION_NAME)
                .whereEqualTo(PUBLIC_PROFILE_FIELD, true)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            return;
                        }
                        for (QueryDocumentSnapshot doc : value) {
                            res.postValue(doc.toObject(Bandmate.class));
                        }
                    }
                });
        return res;
    }
}
