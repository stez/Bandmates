package it.stez78.bandmates.app.activities.searchbandmates;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import it.stez78.bandmates.model.Bandmate;
import it.stez78.bandmates.repositories.AuthRepository;
import it.stez78.bandmates.repositories.BandmatesRepository;

public class SearchBandmatesViewModel extends ViewModel {

    private BandmatesRepository bandmatesRepository;
    private AuthRepository authRepository;

    @Inject
    public SearchBandmatesViewModel(BandmatesRepository bandmatesRepository, AuthRepository authRepository) {
        this.bandmatesRepository = bandmatesRepository;
        this.authRepository = authRepository;
    }

    public LiveData<Bandmate> bandmatesLiveData() {
        return bandmatesRepository.updatedPublicProfilesLivedata();
    }

    public boolean checkUserSignedIn(){
        return authRepository.isUserAuthenticated();
    }
}
