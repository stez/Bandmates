package it.stez78.bandmates.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import it.stez78.bandmates.app.activities.searchbandmates.EmptyFragment;
import it.stez78.bandmates.app.fragments.bandmatepreviewdialog.BandmatePreviewDialogFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract BandmatePreviewDialogFragment bandmatePreviewDialogFragment();

}
