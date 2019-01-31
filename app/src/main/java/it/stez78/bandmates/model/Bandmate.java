package it.stez78.bandmates.model;

import android.os.Parcel;
import android.os.Parcelable;

import lombok.Data;

/**
 * Created by Stefano Zanotti on 03/09/2018.
 */
public class Bandmate implements Parcelable {

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

    public Bandmate(){
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getInstrument() {
        return instrument;
    }

    public void setInstrument(String instrument) {
        this.instrument = instrument;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getPublicProfile() {
        return publicProfile;
    }

    public void setPublicProfile(Boolean publicProfile) {
        this.publicProfile = publicProfile;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.name);
        dest.writeString(this.imageUrl);
        dest.writeInt(this.age);
        dest.writeString(this.instrument);
        dest.writeString(this.location);
        dest.writeValue(this.publicProfile);
        dest.writeDouble(this.lat);
        dest.writeDouble(this.lon);
        dest.writeString(this.description);
        dest.writeString(this.email);
    }

    protected Bandmate(Parcel in) {
        this.id = in.readString();
        this.name = in.readString();
        this.imageUrl = in.readString();
        this.age = in.readInt();
        this.instrument = in.readString();
        this.location = in.readString();
        this.publicProfile = (Boolean) in.readValue(Boolean.class.getClassLoader());
        this.lat = in.readDouble();
        this.lon = in.readDouble();
        this.description = in.readString();
        this.email = in.readString();
    }

    public static final Creator<Bandmate> CREATOR = new Creator<Bandmate>() {
        @Override
        public Bandmate createFromParcel(Parcel source) {
            return new Bandmate(source);
        }

        @Override
        public Bandmate[] newArray(int size) {
            return new Bandmate[size];
        }
    };

    @Override
    public String toString() {
        return "Bandmate{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", age=" + age +
                ", instrument='" + instrument + '\'' +
                ", location='" + location + '\'' +
                ", publicProfile=" + publicProfile +
                ", lat=" + lat +
                ", lon=" + lon +
                ", description='" + description + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}
