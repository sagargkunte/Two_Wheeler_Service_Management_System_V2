package com.twowheeler.dto;

import java.util.List;

public class RequestDtos {

    public static class AddBikeRequest {
        private String name;
        private String model;
        private String registrationNumber;
        private String problem;
        private String serviceType;
        private String location;
        private Double latitude;
        private Double longitude;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getModel() { return model; }
        public void setModel(String model) { this.model = model; }
        public String getRegistrationNumber() { return registrationNumber; }
        public void setRegistrationNumber(String registrationNumber) { this.registrationNumber = registrationNumber; }
        public String getProblem() { return problem; }
        public void setProblem(String problem) { this.problem = problem; }
        public String getServiceType() { return serviceType; }
        public void setServiceType(String serviceType) { this.serviceType = serviceType; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
    }

    public static class MechanicProfileRequest {
        private String email;
        private String shopName;
        private String address;
        private LocationDto location;
        private List<String> servicesOffered;
        private String description;

        public static class LocationDto {
            private Double latitude;
            private Double longitude;

            public Double getLatitude() { return latitude; }
            public void setLatitude(Double latitude) { this.latitude = latitude; }
            public Double getLongitude() { return longitude; }
            public void setLongitude(Double longitude) { this.longitude = longitude; }
        }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getShopName() { return shopName; }
        public void setShopName(String shopName) { this.shopName = shopName; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public LocationDto getLocation() { return location; }
        public void setLocation(LocationDto location) { this.location = location; }
        public List<String> getServicesOffered() { return servicesOffered; }
        public void setServicesOffered(List<String> servicesOffered) { this.servicesOffered = servicesOffered; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class CreateAppointmentRequest {
        private Long mechanicId;
        private Long bikeId;
        private String date;
        private String time;
        private String notes;

        public Long getMechanicId() { return mechanicId; }
        public void setMechanicId(Long mechanicId) { this.mechanicId = mechanicId; }
        public Long getBikeId() { return bikeId; }
        public void setBikeId(Long bikeId) { this.bikeId = bikeId; }
        public String getDate() { return date; }
        public void setDate(String date) { this.date = date; }
        public String getTime() { return time; }
        public void setTime(String time) { this.time = time; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class UpdateAppointmentRequest {
        private String status;
        private String estimatedCompletion;
        private String mechanicNotes;

        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getEstimatedCompletion() { return estimatedCompletion; }
        public void setEstimatedCompletion(String estimatedCompletion) { this.estimatedCompletion = estimatedCompletion; }
        public String getMechanicNotes() { return mechanicNotes; }
        public void setMechanicNotes(String mechanicNotes) { this.mechanicNotes = mechanicNotes; }
    }

    public static class DirectionsRequest {
        private Coordinate source;
        private Coordinate destination;

        public static class Coordinate {
            private Double lat;
            private Double lon;

            public Double getLat() { return lat; }
            public void setLat(Double lat) { this.lat = lat; }
            public Double getLon() { return lon; }
            public void setLon(Double lon) { this.lon = lon; }
        }

        public Coordinate getSource() { return source; }
        public void setSource(Coordinate source) { this.source = source; }
        public Coordinate getDestination() { return destination; }
        public void setDestination(Coordinate destination) { this.destination = destination; }
    }
}
