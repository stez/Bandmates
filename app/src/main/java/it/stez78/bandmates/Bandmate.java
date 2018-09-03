package it.stez78.bandmates;

import lombok.Data;

/**
 * Created by Stefano Zanotti on 03/09/2018.
 */
@Data
public class Bandmate {

    private String name;
    private Long age;
    private String instrument;
    private String location;
    private Boolean publicProfile;
}
