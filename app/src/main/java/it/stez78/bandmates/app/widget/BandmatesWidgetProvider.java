package it.stez78.bandmates.app.widget;

import android.Manifest;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.widget.RemoteViews;

import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import it.stez78.bandmates.AppConfig;
import it.stez78.bandmates.R;
import it.stez78.bandmates.app.activities.searchbandmates.SearchBandmatesActivity;
import timber.log.Timber;

public class BandmatesWidgetProvider extends AppWidgetProvider {

    private static void updateWidgetData(Context context, PendingIntent pendingIntent, AppWidgetManager appWidgetManager, int appWidgetId){
        final RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.bandmates_appwidget);
        views.setOnClickPendingIntent(R.id.widget_container, pendingIntent);

        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            views.setTextViewText(R.id.widget_location,context.getString(R.string.location_not_available));
            views.setTextViewText(R.id.widget_search_result, context.getString(R.string.no_result));
            appWidgetManager.updateAppWidget(appWidgetId, views);
        } else {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    try {
                        Geocoder geo = new Geocoder(context, Locale.getDefault());
                        List<Address> addresses = geo.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                        if (addresses.isEmpty()) {
                            views.setTextViewText(R.id.widget_location, context.getString(R.string.waiting_location));
                        } else {
                            String locationName = addresses.get(0).getLocality() + ", "
                                    + addresses.get(0).getAdminArea() + ", "
                                    + addresses.get(0).getCountryName();
                            views.setTextViewText(R.id.widget_location, locationName);
                        }
                        final int[] bandmatesCounter = {0};
                        GeoFire geoFire =  new GeoFire(FirebaseDatabase.getInstance().getReference(AppConfig.FIREBASE_DATABASE_GEOFIRE_DB_REF));
                        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(location.getLatitude(),location.getLongitude()), 50);
                        geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                            @Override
                            public void onKeyEntered(String key, GeoLocation location) {
                                bandmatesCounter[0]++;
                                views.setTextViewText(R.id.widget_search_result,context.getResources().getQuantityString(R.plurals.bandmate_search_total_result, bandmatesCounter[0], bandmatesCounter[0]));
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                            @Override
                            public void onKeyExited(String key) {
                                bandmatesCounter[0]--;
                                views.setTextViewText(R.id.widget_search_result,context.getResources().getQuantityString(R.plurals.bandmate_search_total_result, bandmatesCounter[0], bandmatesCounter[0]));
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                            @Override
                            public void onKeyMoved(String key, GeoLocation location) {

                            }

                            @Override
                            public void onGeoQueryReady() {
                                views.setTextViewText(R.id.widget_search_result,context.getResources().getQuantityString(R.plurals.bandmate_search_total_result, bandmatesCounter[0], bandmatesCounter[0]));
                                appWidgetManager.updateAppWidget(appWidgetId, views);
                            }

                            @Override
                            public void onGeoQueryError(DatabaseError error) {

                            }
                        });
                        appWidgetManager.updateAppWidget(appWidgetId, views);
                    } catch (IOException e){
                        Timber.d(e);
                    }
                }
            });
        }
    }

    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            int appWidgetId = appWidgetIds[i];
            Intent intent = new Intent(context, SearchBandmatesActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            updateWidgetData(context, pendingIntent, appWidgetManager, appWidgetId);
        }
    }
}
