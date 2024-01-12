package models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Vehicle {
    @JsonProperty("id")
    private long id;
    @JsonProperty("is_busy")
    private boolean is_busy;
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("created_at")
    private Timestamp created_at;
    @JsonProperty("type_vehicle_id")
    private long type_vehicle_id;
    @JsonProperty("base_id")
    private long base_id;

    public Vehicle() {
    }

    public Vehicle(long id, boolean is_busy, double longitude, double latitude, Timestamp created_at, long type_vehicle_id, long base_id) {
        this.id = id;
        this.is_busy = is_busy;
        this.longitude = longitude;
        this.latitude = latitude;
        this.created_at = created_at;
        this.type_vehicle_id = type_vehicle_id;
        this.base_id = base_id;
    }

    //getId
    public long getId() {
        return id;
    }
   
}