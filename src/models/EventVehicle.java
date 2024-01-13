package models;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.Timestamp;


public class EventVehicle {
    @JsonProperty("id")
    private long id;
    @JsonProperty("event_id")
    private long event_id;
    @JsonProperty("vehicle_id")
    private long vehicle_id;
    @JsonProperty("on_site")
    private boolean on_site;
    @JsonProperty("created_at")
    private Timestamp created_at;
    @JsonProperty("event")
    private Event event;


    public EventVehicle() {
    }

    public EventVehicle(long id, long event_id, long vehicle_id, boolean on_site, Timestamp created_at, Event event) {
        this.id = id;
        this.event_id = event_id;
        this.vehicle_id = vehicle_id;
        this.on_site = on_site;
        this.created_at = created_at;
        this.event = event;
    }

    
}
