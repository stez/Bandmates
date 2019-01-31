package it.stez78.bandmates.app.activities.bandmatedetails;

import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import it.stez78.bandmates.model.Bandmate;

/**
 * Created by Stefano Zanotti on 31/01/2019.
 */
public class BandmateDetailsViewModel extends ViewModel {

    private Bandmate bandmate;

    @Inject
    public BandmateDetailsViewModel(){

    }

    public void init(Bandmate bandmate){
        this.bandmate = bandmate;
    }

    public Bandmate getBandmate() {
        return bandmate;
    }
}
