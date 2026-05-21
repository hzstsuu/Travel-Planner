package com.travelplanner.model;

import java.time.LocalDate;

public class Trip extends BaseEntity {
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String notes;
    private TripStatus status;

    public Trip() {
        this.status = TripStatus.PLANNED;
    }

    public Trip(
            int id,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            String notes,
            TripStatus status
    ) {
        super(id);
        this.destination = destination;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.notes = notes;
        this.status = status;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        if (destination == null || destination.trim().isEmpty()) {
            throw new IllegalArgumentException("Destination cannot be empty.");
        }
        this.destination = destination.trim();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        if (startDate == null) {
            throw new IllegalArgumentException("Start date cannot be null.");
        }
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        if (endDate == null) {
            throw new IllegalArgumentException("End date cannot be null.");
        }
        this.endDate = endDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes == null ? "" : notes.trim();
    }

    public TripStatus getStatus() {
        return status;
    }

    public void setStatus(TripStatus status) {
        if (status == null) {
            throw new IllegalArgumentException("Trip status cannot be null.");
        }
        this.status = status;
    }

    public boolean isCompleted() {
        return status == TripStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return "Trip ID: " + getId()
                + "\nDestination: " + destination
                + "\nStart Date: " + startDate
                + "\nEnd Date: " + endDate
                + "\nDescription: " + description
                + "\nNotes: " + notes
                + "\nStatus: " + status;
    }
}
