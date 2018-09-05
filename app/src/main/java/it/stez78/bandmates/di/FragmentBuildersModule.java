package it.stez78.bandmates.di;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;
import it.stez78.bandmates.app.activities.searchbandmates.EmptyFragment;

@Module
public abstract class FragmentBuildersModule {

    @ContributesAndroidInjector
    abstract EmptyFragment contributeEmptyFragment();

}
