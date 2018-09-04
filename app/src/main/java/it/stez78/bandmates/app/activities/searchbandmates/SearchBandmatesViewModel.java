package it.stez78.bandmates.app.activities.searchbandmates;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import javax.inject.Inject;

import it.stez78.bandmates.model.Bandmate;
import it.stez78.bandmates.repositories.BandmatesRepository;

public class SearchBandmatesViewModel extends ViewModel {

    private BandmatesRepository bandmatesRepository;

    @Inject
    public SearchBandmatesViewModel(BandmatesRepository bandmatesRepository){
        this.bandmatesRepository = bandmatesRepository;
    }

    public LiveData<Bandmate> bandmatesLiveData(){
        return bandmatesRepository.updatedPublicProfilesLivedata();
    }
}
