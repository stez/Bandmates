package it.stez78.bandmates.repositories;

import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Stefano Zanotti on 23/01/2019.
 */
public class FirebaseLiveData<T> extends LiveData<T> {

    private static final String TAG = "FirebaseLiveData";

    @NonNull
    private final DatabaseReference reference;
    private final Class<T> type;
    private final GenericTypeIndicator<T> typeIndicator;

    private ValueEventListener listener = new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (type != null) {
                setValue(dataSnapshot.getValue(type));
            } else if (typeIndicator != null) {
                setValue(dataSnapshot.getValue(typeIndicator));
            } else {
                Log.w(TAG, "no type specified");
                //noinspection unchecked
                setValue((T) dataSnapshot.getValue());
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            setValue(null);
        }
    };

    public FirebaseLiveData(@NonNull DatabaseReference reference, Class<T> type) {
        this.reference = reference;
        this.type = type;
        typeIndicator = null;
    }

    public FirebaseLiveData(@NonNull DatabaseReference reference,
                            GenericTypeIndicator<T> typeIndicator) {
        this.reference = reference;
        this.type = null;
        this.typeIndicator = typeIndicator;
    }

    @Override
    protected void onActive() {
        super.onActive();
        reference.addValueEventListener(listener);
    }

    @Override
    protected void onInactive() {
        super.onInactive();
        reference.removeEventListener(listener);
    }
}