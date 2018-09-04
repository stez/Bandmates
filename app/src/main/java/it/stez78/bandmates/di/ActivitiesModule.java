package it.stez78.bandmates.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import it.stez78.bandmates.app.MainActivity;

@Module
public abstract class ActivitiesModule {

    @ContributesAndroidInjector(modules = FragmentBuildersModule.class)
    abstract MainActivity contributeMainActivity();
}