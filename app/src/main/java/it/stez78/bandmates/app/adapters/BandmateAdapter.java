package it.stez78.bandmates.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import it.stez78.bandmates.model.Bandmate;
import it.stez78.bandmates.R;

/**
 * Created by Stefano Zanotti on 03/09/2018.
 */
public class BandmateAdapter extends RecyclerView.Adapter<BandmateAdapter.ViewHolder> {

    private List<Bandmate> bandmates;

    public static class ViewHolder extends RecyclerView.ViewHolder {

        private TextView name;
        private TextView age;
        private TextView location;
        private TextView instrument;

        public ViewHolder(View rootView) {
            super(rootView);
            name = rootView.findViewById(R.id.list_element_bandmate_name);
            age = rootView.findViewById(R.id.list_element_bandmate_age);
            location = rootView.findViewById(R.id.list_element_bandmate_location);
            instrument = rootView.findViewById(R.id.list_element_bandmate_instrument);
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
    }

    public BandmateAdapter(List<Bandmate> bandmates) {
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
        holder.getAge().setText(bandmate.getAge().toString());
        holder.getInstrument().setText(bandmate.getInstrument());
        holder.getLocation().setText(bandmate.getLocation());
    }

    @Override
    public int getItemCount() {
        return bandmates.size();
    }
}