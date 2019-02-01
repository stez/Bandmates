package it.stez78.bandmates.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.javafaker.Address;
import com.github.javafaker.ChuckNorris;
import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import com.github.javafaker.Lorem;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;

import javax.inject.Inject;
import javax.inject.Singleton;

import it.stez78.bandmates.AppConfig;
import it.stez78.bandmates.di.AppExecutors;
import it.stez78.bandmates.model.Bandmate;
import timber.log.Timber;

@Singleton
public class BandmatesRepository {

    private static final char decimalSeparator = new DecimalFormatSymbols().getDecimalSeparator();

    private FirebaseDatabase db;
    private GeoFire geoFire;
    private DatabaseReference bandmateDbRef;
    private final FirebaseQueryLiveData liveData;
    private final AppExecutors appExecutors;

    @Inject
    public BandmatesRepository(FirebaseDatabase db, AppExecutors appExecutors, GeoFire geoFire) {
        this.db = db;
        this.geoFire = geoFire;
        this.appExecutors = appExecutors;
        bandmateDbRef = db.getReference(AppConfig.FIREBASE_DATABASE_BANDAMATES_DB_REF);
        liveData = new FirebaseQueryLiveData(bandmateDbRef);
    }

    public LiveData<List<Bandmate>> updatedPublicProfilesListLivedata() {
        MediatorLiveData<List<Bandmate>> res = new MediatorLiveData<>();
        res.addSource(liveData, dataSnapshot -> {
            Timber.d("SNAPSHOT RECEIVED: "+dataSnapshot);
            appExecutors.networkIO().execute(() -> {
                List<Bandmate> bandmates = new ArrayList<>();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Bandmate bandmate = ds.getValue(Bandmate.class);
                    bandmate.setId(ds.getKey());
                    bandmates.add(bandmate);
                }
                res.postValue(bandmates);
            });
        });
        return res;
    }

    public LiveData<Bandmate> getSingleBandmate(String childId){
        DatabaseReference getSingle = bandmateDbRef.child(childId);
        MutableLiveData<Bandmate> res = new MutableLiveData<>();
        getSingle.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                res.postValue(dataSnapshot.getValue(Bandmate.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return res;
    }

    public void generateBandmates(int howMany){
        String[] instruments = {"guitar","drums","bass","keyboard","vocals"};
        Faker faker = new Faker(new Locale("it"));
        new AppExecutors().networkIO().execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < howMany; i++) {
                    Bandmate b = new Bandmate();
                    b.setId(UUID.randomUUID().toString());
                    b.setAge(new Random().nextInt(20) + 30);
                    b.setName(faker.name().fullName());
                    b.setInstrument(instruments[new Random().nextInt(5)]);
                    b.setPublicProfile(true);
                    Address address = faker.address();
                    b.setLat(Double.valueOf(address.latitude().replace(decimalSeparator, '.')));
                    b.setLon(Double.valueOf(address.longitude().replace(decimalSeparator, '.')));
                    b.setLocation(address.cityName());
                    Internet internet = faker.internet();
                    b.setEmail(internet.emailAddress());
                    b.setImageUrl(internet.avatar());
                    Lorem lorem = faker.lorem();
                    b.setDescription(lorem.paragraph());
                    bandmateDbRef.child(b.getId()).setValue(b);
                    geoFire.setLocation(b.getId(), new GeoLocation(b.getLat(), b.getLon()), (key, error) -> {
                        if (error != null) {
                            System.err.println("There was an error saving the location to GeoFire: " + error);
                        } else {
                            System.out.println("Location saved on server successfully!");
                        }
                    });
                }
            }
        });
    }
}
