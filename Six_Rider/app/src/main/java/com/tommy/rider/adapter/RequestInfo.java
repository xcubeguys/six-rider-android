package com.tommy.rider.adapter;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by navneet on 2/6/16.
 */
public class RequestInfo {

    //Variables that are in our json

    private String request_status;
    private String driver_id;
    private String trip_id;
    @SerializedName("pickup")
    @Expose
    private Pickup pickup;
    @SerializedName("destination")
    @Expose
    private Destination destination;


    //Getters and setters

    public String getRequest_status() {
        return request_status;
    }

    public void setRequest_status(String name) {
        this.request_status = name;
    }
    public String getDriver_id() {
        return driver_id;
    }

    public void setDriver_id(String name) {
        this.driver_id = name;
    }
    public String getTrip_id() {
        return trip_id;
    }

    public void setTrip_id(String name) {
        this.trip_id = name;
    }



    public Pickup getPickup() {
        return pickup;
    }

    public void setPickup(Pickup pickup) {
        this.pickup = pickup;
    }

    public Destination getDestination() {
        return destination;
    }

    public void setDestination(Destination destination) {
        this.destination = destination;
    }


    public class Destination {

        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("long")
        @Expose
        private String _long;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLong() {
            return _long;
        }

        public void setLong(String _long) {
            this._long = _long;
        }

    }

    public class Pickup {

        @SerializedName("lat")
        @Expose
        private String lat;
        @SerializedName("long")
        @Expose
        private String _long;

        public String getLat() {
            return lat;
        }

        public void setLat(String lat) {
            this.lat = lat;
        }

        public String getLong() {
            return _long;
        }

        public void setLong(String _long) {
            this._long = _long;
        }

    }
}