package it.stez78.bandmates.di;

import android.app.Application;

import com.google.firebase.firestore.FirebaseFirestore;
import com.jakewharton.picasso.OkHttp3Downloader;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
    FirebaseFirestore provideFirestore() {
        return FirebaseFirestore.getInstance();
    }

}