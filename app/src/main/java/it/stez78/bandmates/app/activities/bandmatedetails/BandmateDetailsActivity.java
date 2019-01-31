package it.stez78.bandmates.app.activities.bandmatedetails;

import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.android.DispatchingAndroidInjector;
import dagger.android.support.HasSupportFragmentInjector;
import de.hdodenhof.circleimageview.CircleImageView;
import it.stez78.bandmates.R;
import it.stez78.bandmates.di.GlideApp;
import it.stez78.bandmates.model.Bandmate;

/**
 * Created by Stefano Zanotti on 31/01/2019.
 */
public class BandmateDetailsActivity extends AppCompatActivity implements HasSupportFragmentInjector {

    @Inject
    DispatchingAndroidInjector<Fragment> dispatchingAndroidInjector;

    @BindView(R.id.activity_bandmate_profile_toolbar)
    Toolbar toolbar;

    @BindView(R.id.activity_bandmate_profile_name)
    TextView name;

    @BindView(R.id.activity_bandmate_profile_age)
    TextView age;

    @BindView(R.id.activity_bandmate_profile_instrument)
    TextView instrument;

    @BindView(R.id.activity_bandmate_profile_location)
    TextView location;

    @BindView(R.id.activity_bandmate_profile_about)
    TextView about;

    @BindView(R.id.activity_bandmate_profile_avatar_image)
    CircleImageView avatarImage;

    @BindView(R.id.activity_bandmate_profile_instrument_banner_image)
    ImageView instrumentBannerImage;

    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private BandmateDetailsViewModel viewModel;

    @Inject
    FirebaseStorage firebaseStorage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bandmate_profile);
        ButterKnife.bind(this);
        viewModel = ViewModelProviders.of(this, viewModelFactory).get(BandmateDetailsViewModel.class);
        Bundle args = getIntent().getExtras();
        if (args != null && args.getParcelable(Bandmate.BANDMATE_PARCELABLE_KEY) != null) {
            viewModel.init(args.getParcelable(Bandmate.BANDMATE_PARCELABLE_KEY));
            populateUI();
        } else {
            Toast.makeText(this, R.string.dialog_bandmate_preview_opening_error, Toast.LENGTH_SHORT).show();
            finish();
        }
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar!=null){
            actionBar.setTitle(R.string.bandmate_profile_title);
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void populateUI(){
        Bandmate bandmate = viewModel.getBandmate();
        name.setText(bandmate.getName());
        instrument.setText(bandmate.getInstrument());
        age.setText(getString(R.string.bandmate_age, bandmate.getAge()));
        location.setText(bandmate.getLocation());
        about.setText(bandmate.getDescription());
        Picasso.get()
                .load(bandmate.getImageUrl())
                .placeholder(R.drawable.ic_person_outline_black_24dp)
                .resize(50, 50)
                .centerCrop()
                .into(avatarImage);
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
        GlideApp.with(this)
                .load(storageRef)
                .into(instrumentBannerImage);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.activity_bandmate_profile_contact_fab)
    public void contactBandmate() {
        Bandmate bandmate = viewModel.getBandmate();
        Intent intent =  new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {bandmate.getEmail()});
        intent.putExtra(Intent.EXTRA_SUBJECT, getResources().getString(R.string.bandmate_contact_email_subject));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    @Override
    public DispatchingAndroidInjector<Fragment> supportFragmentInjector() {
        return dispatchingAndroidInjector;
    }
}
