package it.stez78.bandmates.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.stez78.bandmates.model.Bandmate;
import timber.log.Timber;

@Singleton
public class BandmatesRepository {

    public final static String DATABASE_BANDMATES_COLLECTION_NAME = "bandmates";
    private final static String PUBLIC_PROFILE_FIELD = "publicProfile";

    private FirebaseDatabase db;
    private DatabaseReference bandmateDbRef;
    private final FirebaseQueryLiveData liveData;

    @Inject
    public BandmatesRepository(FirebaseDatabase db) {
        this.db = db;
        bandmateDbRef = db.getReference(DATABASE_BANDMATES_COLLECTION_NAME);
        liveData = new FirebaseQueryLiveData(bandmateDbRef);
    }

    public LiveData<Bandmate> updatedPublicProfilesLivedata() {
        MediatorLiveData<Bandmate> res = new MediatorLiveData<>();
        res.addSource(liveData, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(@Nullable DataSnapshot dataSnapshot) {
                if (dataSnapshot != null) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            res.postValue(dataSnapshot.getValue(Bandmate.class));
                        }
                    }).start();
                } else {
                    res.setValue(null);
                }
            }
        });
        return res;
    }
}
