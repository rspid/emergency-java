package models;
import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;

public class EventSensor {
    @JsonProperty("event_id")
    private long event_id;
    @JsonProperty("sensor_id")
    private long sensor_id;
    @JsonProperty("created_at")
    private Timestamp created_at;
    @JsonProperty("sensor")
    private Sensor sensor ;
    @JsonProperty("event")
    private Event event ;

    public EventSensor() {
    }

    public EventSensor(long event_id, long sensor_id, Timestamp created_at, Sensor sensor, Event event) {
        this.event_id = event_id;
        this.sensor_id = sensor_id;
        this.created_at = created_at;
        this.sensor = sensor;
        this.event = event;
    }

    //getSensor
    public Sensor getSensor() {
        return sensor;
    }

    //get event
    public Event getEvent() {
        return event;
    }


}
