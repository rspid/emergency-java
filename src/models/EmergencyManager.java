package models;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

public class EmergencyManager extends Thread {
    private static final String EVENT_API_URL = "http://localhost:3000/api/event";
    private static final String VEHICLE_API_URL = "http://localhost:3000/api/vehicle/free";
    private static final String SENSORS_API_URL = "http://localhost:3000/api/sensor/active" ;

    private List<Vehicle> realAvailableVehicles = Collections.synchronizedList(new ArrayList<>());
    // private Set<Long> processingEvents = Collections.synchronizedSet(new HashSet<>());
    private List<Long> processingEvents = new ArrayList<>();
    private List<Vehicle> assignedVehicles = Collections.synchronizedList(new ArrayList<>());
    
    private Semaphore realAvailableVehiclesSemaphore = new Semaphore(1);
    private Semaphore assignedVehiclesSemaphore = new Semaphore(1);

    public void run() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

        // Planifier la tâche pour s'exécuter toutes les 10 secondes
        scheduler.scheduleAtFixedRate(this::performTask, 0, 10, TimeUnit.SECONDS);
    }

    public void performTask() {
        while (true) {
            System.out.println("EmergencyManager is running");
            // List<Event> events = fetchData(EVENT_API_URL, Event[].class);
            List<Sensor> sensors = fetchData(SENSORS_API_URL, Sensor[].class);
            List<Vehicle> currentAvailableVehicles = fetchData(VEHICLE_API_URL, Vehicle[].class);

            // Utiliser synchronized pour garantir un accès sûr à la liste
            try {
                realAvailableVehiclesSemaphore.acquire();
                realAvailableVehicles.clear();
                realAvailableVehicles.addAll(currentAvailableVehicles);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                realAvailableVehiclesSemaphore.release();
            }
            for (Sensor sensor : sensors) {
                //a coder
                //chercher si le sensor à un voisin ou non avec une intensité supérieur à l'intensité du capteur
                //si oui on ajoute le sensor à la liste des sensors de l'event
                //si non on continue et on crée un nouvel event
                if (!processingEvents.contains(event.getId())) {
                    // Marquer l'intervention comme étant en cours de traitement
                    processingEvents.add(sensor.getId());
                    System.out.println("Traitement de l'événement " + event.getId());

                    Thread eventThread = new Thread(() -> {
                        handleEvent(event);
                        System.out.println("Fin du thread");
                        processingEvents.remove(event.getId());
                    });
                    eventThread.start();
                }
            }

            try {
                Thread.sleep(10000); // Attendre 10 secondes (ajustez selon vos besoins)
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void handleEvent(Event event) {
        System.out.println("Nouveau thread");
        try {
            int vehicleToAssign = shouldAssignMultipleVehicles(event) ? 2 : 1;
    
            List<Vehicle> unassignedVehicles = new ArrayList<>();
    
            for (int i = 0; i < vehicleToAssign; i++) {
                boolean isUnassignedVehiclesEmpty = true;
    
                while (isUnassignedVehiclesEmpty) {
                    assignedVehiclesSemaphore.acquire(); // Acquérir le sémaphore pour garantir un accès sûr à la liste
                    realAvailableVehiclesSemaphore.acquire(); // Acquérir le sémaphore pour garantir un accès sûr à la liste
                    // unassignedVehicles.clear();
                    // unassignedVehicles.addAll(realAvailableVehicles);
                    // unassignedVehicles.removeAll(assignedVehicles);
                    unassignedVehicles = realAvailableVehicles.stream()
                        .filter(availableVehicle -> assignedVehicles.stream()
                                .noneMatch(assignedVehicle -> assignedVehicle.getId() == availableVehicle.getId()))
                        .collect(Collectors.toList());
                    if (!unassignedVehicles.isEmpty()) {
                        System.out.println("Fin du while ");
                        isUnassignedVehiclesEmpty = false;
                    }
    
                    realAvailableVehiclesSemaphore.release(); // Relâcher le sémaphore après avoir terminé l'accès à la liste
                    assignedVehiclesSemaphore.release(); // Relâcher le sémaphore après avoir terminé l'accès à la liste
                    Thread.sleep(5000); // Attendre 5 secondes avant de vérifier à nouveau
                }
    
                Vehicle nearestVehicle = calculateNearestVehicle(event, unassignedVehicles);
    
                assignedVehiclesSemaphore.acquire(); // Acquérir le sémaphore pour garantir un accès sûr à la liste
                try {
                    assignVehicleToEvent(event, nearestVehicle);
                    assignedVehicles.add(nearestVehicle);
                } finally {
                    assignedVehiclesSemaphore.release(); // Relâcher le sémaphore après avoir terminé l'accès à la liste
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    
    

    private <T> List<T> fetchData(String apiUrl, Class<T[]> responseType) {
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            reader.close();

            String apiResponse = response.toString();
            ObjectMapper mapper = new ObjectMapper();
            // return List.of(mapper.readValue(apiResponse, responseType));
            return Collections.synchronizedList(Arrays.asList(mapper.readValue(apiResponse, responseType)));
        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private static Vehicle calculateNearestVehicle(Event event, List<Vehicle> availableVehicles) {
        // a coder
        return availableVehicles.get(0);
    }

    private static void assignVehicleToEvent(Event event, Vehicle vehicle) {
        // a coder
        System.out.println("Assignation du véhicule " + vehicle.getId() + " à l'événement " + event.getId());
    }

    private boolean shouldAssignMultipleVehicles(Event event) {
        return event.getSensors().size() > 0 && event.getSensors().get(0).getSensor().getIntensity() >= 5;
    }
}


// public void run() {
    //     while (true) {
    //         System.out.println("EmergencyManager is running");
    //         List<Event> events = fetchData(EVENT_API_URL, Event[].class);
    //         List<Vehicle> currentAvailableVehicles = fetchData(VEHICLE_API_URL, Vehicle[].class);

    //         // Utiliser synchronized pour garantir un accès sûr à la liste
    //         try {
    //             realAvailableVehiclesSemaphore.acquire();
    //             realAvailableVehicles.clear();
    //             realAvailableVehicles.addAll(currentAvailableVehicles);
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         } finally {
    //             realAvailableVehiclesSemaphore.release();
    //         }

    //         for (Event event : events) {
    //             System.out.println("Events en cours " + processingEvents);
    //             if (!processingEvents.contains(event.getId())) {
    //                 // Marquer l'intervention comme étant en cours de traitement
    //                 processingEvents.add(event.getId());
    //                 System.out.println("Traitement de l'événement " + event.getId());

    //                 Thread eventThread = new Thread(() -> {
    //                     handleEvent(event);
                    
    //                     // Retirer l'intervention de la liste des interventions en cours de traitement
    //                     processingEvents.remove(event.getId());
    //                 });
    //                 eventThread.start();
    //             }
    //         }

    //         try {
    //             Thread.sleep(10000); // Attendre 10 secondes (ajustez selon vos besoins)
    //         } catch (InterruptedException e) {
    //             e.printStackTrace();
    //         }
    //     }
    // }

    // private void handleEvent(Event event) {
    //     try {
    //         realAvailableVehiclesSemaphore.acquire();
    //         int vehicleToAssign = shouldAssignMultipleVehicles(event) ? 2 : 1;
    //         Iterator<Vehicle> iterator = realAvailableVehicles.iterator();
    //         System.out.println("nombre de véhicule à assigner" + vehicleToAssign);
    //         assignedVehiclesSemaphore.acquire();
    //         for (int i = 0; i < vehicleToAssign; i++) {
    //             while (realAvailableVehicles.size() == 0 || assignedVehicles.containsAll(realAvailableVehicles)) {
    //                 try {
    //                     System.out.println("En attente de véhicules disponibles...");
    //                     Thread.sleep(5000); // Attendre 5 secondes avant de vérifier à nouveau
    //                 } catch (InterruptedException e) {
    //                     e.printStackTrace();
    //                 }
    //             }

    //             Vehicle nearestVehicle = calculateNearestVehicle(event, realAvailableVehicles);
    //             assignVehicleToEvent(event, nearestVehicle);
    //             assignedVehicles.add(nearestVehicle);
    //         }
    //     } catch (InterruptedException e) {
    //         e.printStackTrace();
    //     } finally {
    //         realAvailableVehiclesSemaphore.release();
    //         assignedVehiclesSemaphore.release();
    //     }
    // }