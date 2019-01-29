package it.stez78.bandmates.app.activities.searchbandmates;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import it.stez78.bandmates.model.Bandmate;
import it.stez78.bandmates.repositories.AuthRepository;
import it.stez78.bandmates.repositories.BandmatesRepository;

public class SearchBandmatesViewModel extends ViewModel {

    private BandmatesRepository bandmatesRepository;
    private AuthRepository authRepository;

    private List<Bandmate> bandmates = new ArrayList<>();
    private final MutableLiveData<String> bandmateKeyInput = new MutableLiveData();
    private final LiveData<Bandmate> bandmateLiveData;

    @Inject
    public SearchBandmatesViewModel(BandmatesRepository bandmatesRepository, AuthRepository authRepository) {
        this.bandmatesRepository = bandmatesRepository;
        this.authRepository = authRepository;
        this.bandmateLiveData  = Transformations.switchMap(bandmateKeyInput, (childID) -> {
                    return bandmatesRepository.getSingleBandmate(childID);
                });
    }

    public LiveData<List<Bandmate>> bandmatesLiveData() {
        return bandmatesRepository.updatedPublicProfilesListLivedata();
    }

    public boolean checkUserSignedIn(){
        return authRepository.isUserAuthenticated();
    }

    public List<Bandmate> getBandmates() {
        return bandmates;
    }

    public void setBandmates(List<Bandmate> bandmates) {
        this.bandmates.clear();
        this.bandmates.addAll(bandmates);
    }

    public void addBandmate(Bandmate bandmate){
        this.bandmates.add(bandmate);
    }

    public void generateBandmates(int howMany){
        bandmatesRepository.generateBandmates(howMany);
    }

    public LiveData<Bandmate> getBandmateLiveData(){
        return bandmateLiveData;
    }

    public void setBandmateChildId(String childId){
        this.bandmateKeyInput.setValue(childId);
    }
}
