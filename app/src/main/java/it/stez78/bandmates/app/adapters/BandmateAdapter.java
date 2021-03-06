package it.stez78.bandmates.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;

import it.stez78.bandmates.R;
import it.stez78.bandmates.di.GlideApp;
import it.stez78.bandmates.model.Bandmate;

/**
 * Created by Stefano Zanotti on 03/09/2018.
 */
public class BandmateAdapter extends RecyclerView.Adapter<BandmateAdapter.ViewHolder> {

    private Context ctx;
    private List<Bandmate> bandmates;
    private OnBandmateAdapterItemClickListener clickListener;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private LinearLayout container;
        private TextView name;
        private TextView age;
        private TextView location;
        private TextView instrument;
        private ImageView bg;

        public ViewHolder(View rootView) {
            super(rootView);
            container = rootView.findViewById(R.id.list_element_bandmate_container);
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

        public void bind(final Bandmate item, final OnBandmateAdapterItemClickListener listener) {
            container.setOnClickListener(v -> listener.onBandmateAdapterItemClick(item));
        }
    }

    public BandmateAdapter(Context ctx, List<Bandmate> bandmates, OnBandmateAdapterItemClickListener listener) {
        this.ctx = ctx;
        this.bandmates = bandmates;
        this.clickListener = listener;
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
        holder.getAge().setText(ctx.getResources().getString(R.string.bandmate_age,bandmate.getAge()));
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
            case "keyboard":
                storageRef = storageRef.child("keyboard/1.jpg");
                break;
            default:
                storageRef = storageRef.child("vocals/1.jpg");
        }
        GlideApp.with(ctx)
                .load(storageRef)
                .into(holder.getBg());
        holder.bind(bandmate, clickListener);
    }

    @Override
    public int getItemCount() {
        return bandmates.size();
    }
}