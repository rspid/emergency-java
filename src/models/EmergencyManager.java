package models;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class EmergencyManager extends Thread {
    private static final String EVENT_API_URL = "http://localhost:3000/api/event";
    private static final String VEHICLE_API_URL = "http://localhost:3000/api/vehicle/free";
    private static final String VEHICLE_ASSIGN_TO_EVENT_API_URL = "http://localhost:3000/api/vehicle/event";
    private static final String SENSOR_NO_EVENT_API_URL = "http://localhost:3000/api/sensor/noevent" ;
    private static final String SENSOR_ALL_API_URL = "http://localhost:3000/api/sensor/all" ;
    private static final double TOLERANCE = 0.00000000000001;

    private ApiClient apiClient;


    private List<Vehicle> realAvailableVehicles = Collections.synchronizedList(new ArrayList<>());
    // private Set<Long> processingEvents = Collections.synchronizedSet(new HashSet<>());
    private List<Long> processingEvents = new ArrayList<>();
    private List<Vehicle> assignedVehicles = Collections.synchronizedList(new ArrayList<>());
    
    private Semaphore realAvailableVehiclesSemaphore = new Semaphore(1);
    private Semaphore assignedVehiclesSemaphore = new Semaphore(1);

    public EmergencyManager() {
        this.apiClient = new ApiClient();
    }

    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Planifier la tâche pour s'exécuter toutes les 10 secondes
        scheduler.scheduleAtFixedRate(this::performTask, 0, 10, TimeUnit.SECONDS);
    }

    public void performTask() {
        while (true) {
            System.out.println("EmergencyManager is running");
            
            List<Sensor> allSensors = apiClient.getList(SENSOR_ALL_API_URL, Sensor[].class);
            List<Sensor> sensors = apiClient.getList(SENSOR_NO_EVENT_API_URL, Sensor[].class);
            List<Vehicle> currentAvailableVehicles = apiClient.getList(VEHICLE_API_URL, Vehicle[].class);
    
            ObjectMapper objectMapper = new ObjectMapper();
    
            try {
                realAvailableVehiclesSemaphore.acquire();
                realAvailableVehicles.clear();
                realAvailableVehicles.addAll(currentAvailableVehicles);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                realAvailableVehiclesSemaphore.release();
            }
    
            System.out.println("Nombre de véhicules disponibles : " + realAvailableVehicles.size());
            System.out.println("Nombre de capteurs : " + sensors.size());
    
            for (Sensor sensor : sensors) {
                if (hasNeighborWithHigherIntensity(sensor, allSensors)) {
                    System.out.println("Capteur " + sensor.getId() + " a un voisin avec une intensité plus élevée");
                    Event event = apiClient.getSingle("http://localhost:3000/api/sensor?id=" + sensor.getId(), Event.class);
                    
                    if (!processingEvents.contains(event.getId())) {
                        processingEvents.add(sensor.getId());
    
                        Thread eventThread = new Thread(() -> {
                            handleEvent(event);
                            System.out.println("Fin du thread");
                            processingEvents.remove(event.getId());
                        });
                        eventThread.start();
                    }
                } else {
                    // Créer un nouvel événement
                    ObjectNode json = objectMapper.createObjectNode();
                    json.put("sensor", sensor.getId());
                    Event event = apiClient.postOrPut(EVENT_API_URL, json, Event.class);
    
                    if (!processingEvents.contains(event.getId())) {
                        processingEvents.add(sensor.getId());
    
                        Thread eventThread = new Thread(() -> {
                            handleEvent(event);
                            System.out.println("Fin du thread");
                            processingEvents.remove(event.getId());
                        });
                        eventThread.start();
                    }
                }
            }
    
            try {
                Thread.sleep(10000); 
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private boolean hasNeighborWithHigherIntensity(Sensor referenceSensor, List<Sensor> sensors) {
        Sensor maxIntensityNeighbor = findNeighborSensorWithMaxIntensity(referenceSensor, sensors);
        return maxIntensityNeighbor != null && maxIntensityNeighbor.getIntensity() > referenceSensor.getIntensity();
    }
    

    private void handleEvent(Event event) {
        System.out.println("Nouveau thread, traitement de l'événement:" + event.getId());
        try {
            int vehicleToAssign = shouldAssignMultipleVehicles(event) ? 2 : 1;
    
            List<Vehicle> unassignedVehicles = new ArrayList<>();
            System.out.println("Nombre de véhicules à assigner : " + vehicleToAssign);
    
            for (int i = 0; i < vehicleToAssign; i++) {
                boolean isUnassignedVehiclesEmpty = true;
    
                while (isUnassignedVehiclesEmpty) {
                    realAvailableVehiclesSemaphore.acquire();
                    assignedVehiclesSemaphore.acquire();
    
                    unassignedVehicles = realAvailableVehicles.stream()
                        .filter(availableVehicle -> assignedVehicles.stream()
                                .noneMatch(assignedVehicle -> assignedVehicle.getId() == availableVehicle.getId()))
                        .collect(Collectors.toList());
    
                    if (!unassignedVehicles.isEmpty()) {
                        System.out.println("Fin du while ");
    
                        Vehicle nearestVehicle = calculateNearestVehicle(event, unassignedVehicles);
    
                        assignVehicleToEvent(event, nearestVehicle);
                        assignedVehicles.add(nearestVehicle);
    
                        isUnassignedVehiclesEmpty = false;
                    }
    
                    assignedVehiclesSemaphore.release();
                    realAvailableVehiclesSemaphore.release();
                    Thread.sleep(5000); // Attendre 5 secondes avant de vérifier à nouveau
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    

    private  Vehicle calculateNearestVehicle(Event event, List<Vehicle> availableVehicles) {
        // a coder
        return availableVehicles.get(0);
    }

    private void assignVehicleToEvent(Event event, Vehicle vehicle) {
        ObjectNode requestBody = new ObjectMapper().createObjectNode();
        
        ObjectNode vehicleNode = new ObjectMapper().createObjectNode();
        vehicleNode.put("id", vehicle.getId());
        vehicleNode.put("eventId", event.getId()); 

        requestBody.set("vehicle", vehicleNode);

        Vehicle updatedVehicule = apiClient.postOrPut(VEHICLE_ASSIGN_TO_EVENT_API_URL, requestBody, Vehicle.class);

        System.out.println("Assignation du véhicule " + updatedVehicule.getId() + " à l'événement " + event.getId());
    }

    private boolean shouldAssignMultipleVehicles(Event event) {
        return event.getSensors().size() > 0 && event.getSensors().get(0).getSensor().getIntensity() >= 5;
    }

    private Sensor findNeighborSensorWithMaxIntensity(Sensor referenceSensor, List<Sensor> sensors) {
        Sensor maxIntensityNeighbor = null;
        double maxIntensity = 0.0;
    
        for (Sensor sensor : sensors) {
            if (sensor.getId() != referenceSensor.getId() && areSensorsAtSameLocation(referenceSensor, sensor)) {
                if (sensor.getIntensity() > maxIntensity) {
                    maxIntensityNeighbor = sensor;
                    maxIntensity = sensor.getIntensity();
                }
            }
        }
    
        return maxIntensityNeighbor;
    }
    
    private boolean areSensorsAtSameLocation(Sensor sensor1, Sensor sensor2) {
        return Math.abs(sensor1.getLatitude() - sensor2.getLatitude()) < TOLERANCE &&
               Math.abs(sensor1.getLongitude() - sensor2.getLongitude()) < TOLERANCE;
    }
    
}