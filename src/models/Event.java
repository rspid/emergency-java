package models;

import java.sql.Timestamp;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;


public class Event {

    private long id;
    
    @JsonProperty("is_over")
    private boolean is_over;
    @JsonProperty("created_at")
    private Timestamp created_at;
    @JsonProperty("sensors")
    private List<EventSensor> sensors;


    // Constructeur par défaut nécessaire pour la désérialisation JSON
    public Event() {
    }
    // Constructeur
    public Event(long id, boolean is_over, Timestamp created_at, List<EventSensor> sensors) {
        this.id = id;
        this.is_over = is_over;
        this.created_at = created_at;
        this.sensors = sensors;
    }

    // Getters et setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean getisOver() {
        return this.is_over;
    }

    public void setOver(boolean is_over) {
        this.is_over = is_over;
    }

    public Timestamp getCreatedAt() {
        return this.created_at;
    }

    public void setCreatedAt(Timestamp created_at) {
        this.created_at = created_at;
    }
    public List<EventSensor> getSensors() {
        return this.sensors;
    }

}
