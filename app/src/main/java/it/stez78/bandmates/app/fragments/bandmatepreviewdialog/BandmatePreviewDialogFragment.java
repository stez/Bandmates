package it.stez78.bandmates.app.fragments.bandmatepreviewdialog;

import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import it.stez78.bandmates.BandmatesAppViewModelFactory;
import it.stez78.bandmates.R;
import it.stez78.bandmates.di.Injectable;
import it.stez78.bandmates.model.Bandmate;

public class BandmatePreviewDialogFragment extends AppCompatDialogFragment implements Injectable {

    public static final String TAG = "bandmatePreviewDialogFragment";
    public static final String BANDMATE_KEY = "bandmateKey";

    @BindView(R.id.dialog_bandmate_preview_avatar)
    ImageView avatar;

    @BindView(R.id.dialog_bandmate_preview_name)
    TextView name;

    @BindView(R.id.dialog_bandmate_preview_instrument)
    TextView instrument;

    @BindView(R.id.dialog_bandmate_preview_age)
    TextView age;

    @BindView(R.id.dialog_bandmate_preview_location)
    TextView location;

    @Inject
    BandmatesAppViewModelFactory viewModelFactory;

    private BandmatePreviewDialogViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.dialog_bandmate_preview, container, false);
        ButterKnife.bind(this, v);
        return v;
    }

    private void populateViews() {
        Bandmate bandmate = viewModel.getBandmate();
        name.setText(bandmate.getName());
        instrument.setText(bandmate.getInstrument());
        age.setText(bandmate.getAge()+"");
        location.setText(bandmate.getLocation());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BandmatePreviewDialogViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.getParcelable(BANDMATE_KEY) != null) {
            viewModel.init(args.getParcelable(BANDMATE_KEY));
            populateViews();
        } else {
            Toast.makeText(getContext(), R.string.dialog_bandmate_preview_opening_error, Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

}
