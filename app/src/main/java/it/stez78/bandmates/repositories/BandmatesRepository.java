package it.stez78.bandmates.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.stez78.bandmates.di.AppExecutors;
import it.stez78.bandmates.model.Bandmate;

@Singleton
public class BandmatesRepository {

    public final static String DATABASE_BANDMATES_COLLECTION_NAME = "bandmates";
    private final static String PUBLIC_PROFILE_FIELD = "publicProfile";

    private FirebaseDatabase db;
    private DatabaseReference bandmateDbRef;
    private final FirebaseQueryLiveData liveData;
    private final AppExecutors appExecutors;

    @Inject
    public BandmatesRepository(FirebaseDatabase db, AppExecutors appExecutors) {
        this.db = db;
        this.appExecutors = appExecutors;
        bandmateDbRef = db.getReference(DATABASE_BANDMATES_COLLECTION_NAME);
        liveData = new FirebaseQueryLiveData(bandmateDbRef);
    }

    public LiveData<List<Bandmate>> updatedPublicProfilesListLivedata() {
        MediatorLiveData<List<Bandmate>> res = new MediatorLiveData<>();
        res.addSource(liveData, dataSnapshot -> {
            appExecutors.networkIO().execute(() -> {
                List<Bandmate> bandmates = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Bandmate bandmate = ds.getValue(Bandmate.class);
                    bandmates.add(bandmate);
                }
                res.postValue(bandmates);
            });
        });
        return res;
    }
}
