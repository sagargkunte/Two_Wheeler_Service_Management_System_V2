package com.twowheeler.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "appointments")
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "bike_id", referencedColumnName = "id", nullable = false)
    private Bike bikeId;

    @ManyToOne
    @JoinColumn(name = "mechanic_id", referencedColumnName = "id", nullable = false)
    private Mechanic mechanicId;

    @Column(name = "appointment_date", nullable = false)
    private String date;

    @Column(name = "appointment_time", nullable = false)
    private String time;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(nullable = false)
    private String status = "booked"; // "booked", "bike-received", "under-repair", "completed", "cancelled"

    @Column(name = "estimated_completion")
    private String estimatedCompletion;

    @Column(name = "mechanic_notes", columnDefinition = "TEXT")
    private String mechanicNotes;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    public Appointment() {
    }

    public Appointment(Bike bikeId, Mechanic mechanicId, String date, String time, String notes) {
        this.bikeId = bikeId;
        this.mechanicId = mechanicId;
        this.date = date;
        this.time = time;
        this.notes = notes;
        this.status = "booked";
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Property for Mongo _id frontend compatibility
    public String get_id() {
        return id != null ? id.toString() : null;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Bike getBikeId() {
        return bikeId;
    }

    public void setBikeId(Bike bikeId) {
        this.bikeId = bikeId;
    }

    public Mechanic getMechanicId() {
        return mechanicId;
    }

    public void setMechanicId(Mechanic mechanicId) {
        this.mechanicId = mechanicId;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getEstimatedCompletion() {
        return estimatedCompletion;
    }

    public void setEstimatedCompletion(String estimatedCompletion) {
        this.estimatedCompletion = estimatedCompletion;
    }

    public String getMechanicNotes() {
        return mechanicNotes;
    }

    public void setMechanicNotes(String mechanicNotes) {
        this.mechanicNotes = mechanicNotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
