package it.stez78.bandmates.model;

import com.google.firebase.firestore.GeoPoint;

import lombok.Data;

/**
 * Created by Stefano Zanotti on 03/09/2018.
 */
@Data
public class Bandmate {

    private String id;
    private String name;
    private Long age;
    private String instrument;
    private String location;
    private Boolean publicProfile;
    private GeoPoint latlon;
}
