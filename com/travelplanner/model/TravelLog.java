package com.travelplanner.model;

import java.time.LocalDate;

public class TravelLog extends BaseEntity {
    private int tripId;
    private String reflection;
    private String imagePath;
    private LocalDate logDate;

    public TravelLog() {
    }

    public TravelLog(
            int id,
            int tripId,
            String reflection,
            String imagePath,
            LocalDate logDate
    ) {
        super(id);
        this.tripId = tripId;
        this.reflection = reflection;
        this.imagePath = imagePath;
        this.logDate = logDate;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        if (tripId <= 0) {
            throw new IllegalArgumentException("Trip ID must be positive.");
        }
        this.tripId = tripId;
    }

    public String getReflection() {
        return reflection;
    }

    public void setReflection(String reflection) {
        if (reflection == null || reflection.trim().isEmpty()) {
            throw new IllegalArgumentException("Reflection cannot be empty.");
        }
        this.reflection = reflection.trim();
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath == null ? "" : imagePath.trim();
    }

    public LocalDate getLogDate() {
        return logDate;
    }

    public void setLogDate(LocalDate logDate) {
        if (logDate == null) {
            throw new IllegalArgumentException("Log date cannot be null.");
        }
        this.logDate = logDate;
    }

    @Override
    public String toString() {
        return "Travel Log ID: " + getId()
                + "\nTrip ID: " + tripId
                + "\nReflection: " + reflection
                + "\nImage Path: " + imagePath
                + "\nLog Date: " + logDate;
    }
}
