// import java.io.BufferedReader;
// import java.io.InputStreamReader;
// import java.net.HttpURLConnection;
// import java.net.URL;

// import com.fasterxml.jackson.databind.ObjectMapper;

import models.EmergencyManager;
// import models.Event;

public class App {
    public static void main(String[] args) {
        EmergencyManager emergencyManager = new EmergencyManager();
        emergencyManager.run();
    }
}
    
//     public static void main(String[] args) throws Exception {
//         try {
//             // Remplacez cette URL par l'URL de l'API que vous souhaitez interroger
//             String stringToGetEvents = "http://localhost:3000/api/event";
            
//             // Créez l'objet URL avec l'URL de l'API
//             URL urlToGetEvents = new URL(stringToGetEvents);
            
//             // Ouvrez la connexion HTTP
//             HttpURLConnection connection = (HttpURLConnection) urlToGetEvents.openConnection();
            
//             // Définissez la méthode de requête (GET, POST, etc.)
//             connection.setRequestMethod("GET");
            
//             // Récupérez la réponse de l'API
//             BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//             String line;
//             StringBuilder response = new StringBuilder();
            
//             while ((line = reader.readLine()) != null) {
//                 response.append(line);
//             }
            
//             reader.close();
            
//             String apiResponse = response.toString();
//             ObjectMapper mapper = new ObjectMapper();
//             // System.out.println(apiResponse);
//             Event[] events = mapper.readValue(apiResponse, Event[].class);

//             for (Event event : events) {
//                 System.out.println("Event ID: " + event.getId());
//                 // System.out.println("Is Over: " + event.isOver());
//                 // System.out.println("Created At: " + event.getCreatedAt());
    
//                 // // Vous pouvez également accéder aux capteurs associés à chaque événement
//                 // for (Sensor sensor : event.getSensors()) {
//                 //     System.out.println("Sensor ID: " + sensor.getId());
//                 //     System.out.println("Sensor Intensity: " + sensor.getIntensity());
//                 //     System.out.println("Sensor Longitude: " + sensor.getLongitude());
//                 //     System.out.println("Sensor Latitude: " + sensor.getLatitude());
//                 //     System.out.println("Sensor Created At: " + sensor.getCreatedAt());
//                 // }
    
//                 System.out.println("----------");
//             }

            
//             // Fermez la connexion
//             connection.disconnect();
//         } catch (Exception e) {
//             e.printStackTrace();
//         }
//     }
// }
