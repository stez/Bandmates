package it.stez78.bandmates.model;

import lombok.Data;

/**
 * Created by Stefano Zanotti on 03/09/2018.
 */
@Data
public class Bandmate {

    private String id;
    private String name;
    private String imageUrl;
    private int age;
    private String instrument;
    private String location;
    private Boolean publicProfile;
    private double lat;
    private double lon;
    private String description;
    private String email;
}
