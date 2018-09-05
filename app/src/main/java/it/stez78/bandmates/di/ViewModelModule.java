package it.stez78.bandmates.di;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;
import it.stez78.bandmates.BandmatesAppViewModelFactory;
import it.stez78.bandmates.app.activities.searchbandmates.SearchBandmatesViewModel;

@Module
abstract class ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(SearchBandmatesViewModel.class)
    abstract ViewModel bindSearchBandmatesViewModel(SearchBandmatesViewModel searchBandmatesViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(BandmatesAppViewModelFactory factory);

}
