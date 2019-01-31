package it.stez78.bandmates.app.fragments.bandmatepreviewdialog;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatDialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.stez78.bandmates.BandmatesAppViewModelFactory;
import it.stez78.bandmates.R;
import it.stez78.bandmates.app.activities.bandmatedetails.BandmateDetailsActivity;
import it.stez78.bandmates.di.Injectable;
import it.stez78.bandmates.model.Bandmate;

public class BandmatePreviewDialogFragment extends AppCompatDialogFragment implements Injectable {

    public static final String TAG = "bandmatePreviewDialogFragment";

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
        age.setText(getString(R.string.bandmate_age, bandmate.getAge()));
        location.setText(bandmate.getLocation());
        Picasso.get()
                .load(bandmate.getImageUrl())
                .placeholder(R.drawable.ic_person_outline_black_24dp)
                .resize(50, 50)
                .centerCrop()
                .into(avatar);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BandmatePreviewDialogViewModel.class);
        Bundle args = getArguments();
        if (args != null && args.getParcelable(Bandmate.BANDMATE_PARCELABLE_KEY) != null) {
            viewModel.init(args.getParcelable(Bandmate.BANDMATE_PARCELABLE_KEY));
            populateViews();
        } else {
            Toast.makeText(getContext(), R.string.dialog_bandmate_preview_opening_error, Toast.LENGTH_SHORT).show();
            dismiss();
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow()
                .setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    @OnClick(R.id.dialog_bandmate_preview_button_contact)
    public void contactBandmate() {
        Bandmate bandmate = viewModel.getBandmate();
        Intent intent =  new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {bandmate.getEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.bandmate_contact_email_subject));
        if (intent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    @OnClick(R.id.dialog_bandmate_preview_button_details)
    public void openBandmateDetails(){
        Intent intent = new Intent(getContext(), BandmateDetailsActivity.class);
        intent.putExtra(Bandmate.BANDMATE_PARCELABLE_KEY, viewModel.getBandmate());
        startActivity(intent);
    }
}
