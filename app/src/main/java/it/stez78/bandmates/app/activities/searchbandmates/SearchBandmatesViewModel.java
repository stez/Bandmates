package it.stez78.bandmates.app.activities.searchbandmates;

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

    @Inject
    public SearchBandmatesViewModel(BandmatesRepository bandmatesRepository, AuthRepository authRepository) {
        this.bandmatesRepository = bandmatesRepository;
        this.authRepository = authRepository;
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

    public void removeBandmateById(String bandmateId){
        List<Bandmate> removeList = new ArrayList<>(this.bandmates);
        for (Bandmate b : removeList){
            if (b.getId().equals(bandmateId)){
                this.bandmates.remove(b);
            }
        }
    }

    public void generateBandmates(int howMany){
        bandmatesRepository.generateBandmates(howMany);
    }

}
