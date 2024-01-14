package models;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Sensor {
    @JsonProperty("id")
    private long id;
    @JsonProperty("intensity")
    private int intensity;
    @JsonProperty("longitude")
    private double longitude;
    @JsonProperty("latitude")
    private double latitude;
    @JsonProperty("created_at")
    private Timestamp created_at;
    @JsonProperty("events")
    private List<EventSensor> events;

    public Sensor() {
    }
    public Sensor(long id, int intensity, double longitude, double latitude, Timestamp created_at,
            List<EventSensor> events) {
        this.id = id;
        this.intensity = intensity;
        this.longitude = longitude;
        this.latitude = latitude;
        this.created_at = created_at;
        this.events = events;
    }
    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getIntensity() {
        return this.intensity;
    }

    public void setIntensity(int intensity) {
        this.intensity = intensity;
    }

    public double getLongitude() {
        return this.longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


    public double getLatitude() {
        return this.latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }


    public Timestamp getCreatedAt() {
        return this.created_at;
    }

    public void setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
    }

    //getter to return latitude and longitude
    public double[] getCoordinates() {
        double[] coordinates = {this.latitude, this.longitude};
        return coordinates;
    }
    //get first event
    public EventSensor getEvent() {
        return this.events.get(0);
    }
}
