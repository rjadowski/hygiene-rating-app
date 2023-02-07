package mmu.ac.hygienerating;

import java.io.Serializable;

//Object to store the latitude and longitude data for a location
public class LocationData implements Serializable {

    private String latitude;
    private String longitude;

    LocationData(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

}
