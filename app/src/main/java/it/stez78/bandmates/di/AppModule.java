package it.stez78.bandmates.di;

import android.app.Application;

import com.firebase.geofire.GeoFire;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import it.stez78.bandmates.AppConfig;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

@Module(includes = ViewModelModule.class)
class AppModule {

    @Provides
    @Singleton
    Picasso providePicasso(Application ctx) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();
        Picasso p = new Picasso.Builder(ctx)
                .downloader(new OkHttp3Downloader(client))
                .build();
        p.setLoggingEnabled(true);
        return p;
    }

    @Provides
    @Singleton
    FirebaseDatabase provideDatabase() {
        return FirebaseDatabase.getInstance();
    }

    @Provides
    @Singleton
    GeoFire provideGeoFire(FirebaseDatabase firebaseDatabase) {
        return new GeoFire(firebaseDatabase.getReference(AppConfig.FIREBASE_DATABASE_GEOFIRE_DB_REF));
    }

    @Provides
    @Singleton
    FirebaseStorage provideFirebaseStorage() {
        return FirebaseStorage.getInstance();
    }

    @Provides
    @Singleton
    FirebaseAuth provideFirebaseAuth() {
        return FirebaseAuth.getInstance();
    }

}