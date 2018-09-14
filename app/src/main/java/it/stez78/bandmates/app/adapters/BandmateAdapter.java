package it.stez78.bandmates.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.List;

import it.stez78.bandmates.R;
import it.stez78.bandmates.di.GlideApp;
import it.stez78.bandmates.model.Bandmate;
import timber.log.Timber;

/**
 * Created by Stefano Zanotti on 03/09/2018.
 */
public class BandmateAdapter extends RecyclerView.Adapter<BandmateAdapter.ViewHolder> {

    private Context ctx;
    private List<Bandmate> bandmates;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView age;
        private TextView location;
        private TextView instrument;
        private ImageView bg;

        public ViewHolder(View rootView) {
            super(rootView);
            name = rootView.findViewById(R.id.list_element_bandmate_name);
            age = rootView.findViewById(R.id.list_element_bandmate_age);
            location = rootView.findViewById(R.id.list_element_bandmate_location);
            instrument = rootView.findViewById(R.id.list_element_bandmate_instrument);
            bg = rootView.findViewById(R.id.list_element_bandmate_bg);
        }

        public TextView getName() {
            return name;
        }

        public TextView getAge() {
            return age;
        }

        public TextView getLocation() {
            return location;
        }

        public TextView getInstrument() {
            return instrument;
        }

        public ImageView getBg() { return bg; }
    }

    public BandmateAdapter(Context ctx, List<Bandmate> bandmates) {
        this.ctx = ctx;
        this.bandmates = bandmates;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.list_element_bandmate, viewGroup, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Bandmate bandmate = bandmates.get(position);
        holder.getName().setText(bandmate.getName());
        holder.getAge().setText(bandmate.getAge().toString()+" years old");
        holder.getInstrument().setText(bandmate.getInstrument());
        holder.getLocation().setText(bandmate.getLocation());
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        StorageReference storageRef = firebaseStorage.getReference().child("backgrounds");

        switch (bandmate.getInstrument()){
            case "guitar":
                storageRef = storageRef.child("guitar/1.jpg");
                break;
            case "drums":
                storageRef = storageRef.child("drum/1.jpg");
                break;
            case "bass":
                storageRef = storageRef.child("bass/1.jpg");
                break;
            default:
                storageRef = storageRef.child("vocals/1.jpg");
        }
        GlideApp.with(ctx)
                .load(storageRef)
                .into(holder.getBg());
    }

    @Override
    public int getItemCount() {
        return bandmates.size();
    }
}