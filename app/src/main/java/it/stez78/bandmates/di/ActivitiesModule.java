package it.stez78.bandmates.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import it.stez78.bandmates.app.activities.bandmatedetails.BandmateDetailsActivity;
import it.stez78.bandmates.app.activities.searchbandmates.SearchBandmatesActivity;

@Module
public abstract class ActivitiesModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract SearchBandmatesActivity contributeSearchBandmatesActivity();

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract BandmateDetailsActivity contributeBandmateDetailsActivity();
}