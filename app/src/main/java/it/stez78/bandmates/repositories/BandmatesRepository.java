package it.stez78.bandmates.repositories;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.github.javafaker.Address;
import com.github.javafaker.Faker;
import com.github.javafaker.Internet;
import com.github.javafaker.Lorem;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DecimalFormatSymbols;
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
    private final AppExecutors appExecutors;

    @Inject
    public BandmatesRepository(FirebaseDatabase db, AppExecutors appExecutors, GeoFire geoFire) {
        this.db = db;
        this.geoFire = geoFire;
        this.appExecutors = appExecutors;
        bandmateDbRef = db.getReference(AppConfig.FIREBASE_DATABASE_BANDAMATES_DB_REF);
    }

    public void generateBandmates(int howMany){
        String[] instruments = {"guitar","drums","bass","keyboard","vocals"};
        Faker faker = new Faker(new Locale("it"));
        new AppExecutors().networkIO().execute(() -> {
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
                        Timber.d("There was an error saving the location to GeoFire");
                    } else {
                        Timber.d("Location saved on server successfully!");
                    }
                });
            }
        });
    }
}
