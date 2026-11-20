package com.twowheeler.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class DistanceService {

    @Value("${ors.api.key:}")
    private String orsApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Calculates great-circle distance between two GPS coordinates using Haversine formula (in kilometers)
     */
    public double calculateHaversineDistanceKm(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Earth's radius in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }

    /**
     * Calculates distance and duration using OpenRouteService if API key available, or falls back to Haversine
     */
    public Map<String, Object> getRoadDistance(double startLat, double startLon, double endLat, double endLon) {
        Map<String, Object> result = new HashMap<>();

        if (orsApiKey != null && !orsApiKey.isBlank()) {
            try {
                String url = "https://api.openrouteservice.org/v2/directions/driving-car";
                HttpHeaders headers = new HttpHeaders();
                headers.set("Authorization", orsApiKey);
                headers.setContentType(MediaType.APPLICATION_JSON);

                Map<String, Object> body = new HashMap<>();
                body.put("coordinates", Arrays.asList(
                        Arrays.asList(startLon, startLat),
                        Arrays.asList(endLon, endLat)
                ));

                HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(body, headers);
                ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);

                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    List routes = (List) response.getBody().get("routes");
                    if (routes != null && !routes.isEmpty()) {
                        Map firstRoute = (Map) routes.get(0);
                        Map summary = (Map) firstRoute.get("summary");
                        double distanceMeters = ((Number) summary.get("distance")).doubleValue();
                        double durationSeconds = ((Number) summary.get("duration")).doubleValue();

                        result.put("distance", distanceMeters / 1000.0);
                        result.put("duration", durationSeconds / 60.0);
                        result.put("geometry", firstRoute.get("geometry"));
                        return result;
                    }
                }
            } catch (Exception e) {
                System.out.println("ORS routing request failed (" + e.getMessage() + "), using Haversine fallback.");
            }
        }

        // Fallback to Haversine calculation
        double distKm = calculateHaversineDistanceKm(startLat, startLon, endLat, endLon);
        result.put("distance", distKm);
        result.put("duration", (distKm / 35.0) * 60.0); // assume 35 km/h avg speed
        return result;
    }
}
