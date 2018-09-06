package it.stez78.bandmates.repositories;

import com.google.firebase.auth.FirebaseAuth;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Created by Stefano Zanotti on 06/09/2018.
 */
@Singleton
public class AuthRepository {

    private FirebaseAuth firebaseAuth;

    @Inject
    public AuthRepository(FirebaseAuth firebaseAuth){
        this.firebaseAuth = firebaseAuth;
    }

    public Boolean isUserAuthenticated(){
        return firebaseAuth.getCurrentUser() != null;
    }
}
