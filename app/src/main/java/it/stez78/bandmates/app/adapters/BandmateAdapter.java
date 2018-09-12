package it.stez78.bandmates.app.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import it.stez78.bandmates.R;
import it.stez78.bandmates.model.Bandmate;

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
        private ImageView bgGuitar;
        private ImageView bgDrums;
        private ImageView bgBass;
        private ImageView bgVocals;

        public ViewHolder(View rootView) {
            super(rootView);
            name = rootView.findViewById(R.id.list_element_bandmate_name);
            age = rootView.findViewById(R.id.list_element_bandmate_age);
            location = rootView.findViewById(R.id.list_element_bandmate_location);
            instrument = rootView.findViewById(R.id.list_element_bandmate_instrument);
            bgGuitar = rootView.findViewById(R.id.list_element_bandmate_bg_guitar);
            bgDrums = rootView.findViewById(R.id.list_element_bandmate_bg_drums);
            bgBass = rootView.findViewById(R.id.list_element_bandmate_bg_bass);
            bgVocals = rootView.findViewById(R.id.list_element_bandmate_bg_vocals);
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

        public ImageView getBgGuitar() { return bgGuitar; }

        public ImageView getBgDrums() { return bgDrums; }

        public ImageView getBgBass() { return bgBass; }

        public ImageView getBgVocals() { return bgVocals; }
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
        holder.getAge().setText(bandmate.getAge().toString()+" years old");
        holder.getInstrument().setText(bandmate.getInstrument());
        holder.getLocation().setText(bandmate.getLocation());
        switch (bandmate.getInstrument()){
            case "guitar":
                holder.getBgGuitar().setVisibility(View.VISIBLE);
                holder.getBgDrums().setVisibility(View.INVISIBLE);
                holder.getBgBass().setVisibility(View.INVISIBLE);
                holder.getBgVocals().setVisibility(View.INVISIBLE);
                break;
            case "drums":
                holder.getBgGuitar().setVisibility(View.INVISIBLE);
                holder.getBgDrums().setVisibility(View.VISIBLE);
                holder.getBgBass().setVisibility(View.INVISIBLE);
                holder.getBgVocals().setVisibility(View.INVISIBLE);
                break;
            case "bass":
                holder.getBgGuitar().setVisibility(View.INVISIBLE);
                holder.getBgDrums().setVisibility(View.INVISIBLE);
                holder.getBgBass().setVisibility(View.VISIBLE);
                holder.getBgVocals().setVisibility(View.INVISIBLE);
                break;
            case "vocals":
                holder.getBgGuitar().setVisibility(View.INVISIBLE);
                holder.getBgDrums().setVisibility(View.INVISIBLE);
                holder.getBgBass().setVisibility(View.INVISIBLE);
                holder.getBgVocals().setVisibility(View.VISIBLE);
                break;
            default:
                holder.getBgGuitar().setVisibility(View.INVISIBLE);
                holder.getBgDrums().setVisibility(View.INVISIBLE);
                holder.getBgBass().setVisibility(View.INVISIBLE);
                holder.getBgVocals().setVisibility(View.VISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return bandmates.size();
    }
}