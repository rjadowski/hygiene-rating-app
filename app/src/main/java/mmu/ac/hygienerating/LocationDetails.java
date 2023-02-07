package mmu.ac.hygienerating;

import java.io.Serializable;

//Object to store the location details
class LocationDetails implements Serializable {

    private String header;
    private String desc;
    private String rating;

    LocationDetails(String header, String desc, String rating) {
        this.header = header;
        this.desc = desc;
        this.rating = rating;
    }

    String getHeader() {
        return header;
    }

    String getDesc() {
        return desc;
    }

    String getRating() {
        return rating;
    }

}
