package it.stez78.bandmates.app.fragments.bandmatepreviewdialog;

import android.arch.lifecycle.ViewModel;

import javax.inject.Inject;

import it.stez78.bandmates.model.Bandmate;

public class BandmatePreviewDialogViewModel extends ViewModel {

    private Bandmate bandmate;

    @Inject
    public BandmatePreviewDialogViewModel(){

    }

    public void init(Bandmate bandmate){
        this.bandmate = bandmate;
    }

    public Bandmate getBandmate() {
        return bandmate;
    }
}
