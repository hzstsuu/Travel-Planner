package com.travelplanner.model;

import java.time.LocalDate;

public class Trip extends BaseEntity {
    private int    userId;        // NEW: owner of this trip
    private String destination;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private String notes;
    private TripStatus status;

    public Trip() { this.status = TripStatus.PLANNED; }

    public Trip(int id, int userId, String destination,
                LocalDate startDate, LocalDate endDate,
                String description, String notes, TripStatus status) {
        super(id);
        this.userId      = userId;
        this.destination = destination;
        this.startDate   = startDate;
        this.endDate     = endDate;
        this.description = description;
        this.notes       = notes;
        this.status      = status;
    }

    /** Legacy constructor (userId = 0) so old data still deserialises. */
    public Trip(int id, String destination,
                LocalDate startDate, LocalDate endDate,
                String description, String notes, TripStatus status) {
        this(id, 0, destination, startDate, endDate, description, notes, status);
    }

    public int  getUserId()              { return userId; }
    public void setUserId(int v)         { this.userId = v; }

    public String getDestination()       { return destination; }
    public void setDestination(String v) {
        if (v == null || v.isBlank()) throw new IllegalArgumentException("Destination cannot be empty.");
        this.destination = v.trim();
    }

    public LocalDate getStartDate()      { return startDate; }
    public void setStartDate(LocalDate d){
        if (d == null) throw new IllegalArgumentException("Start date cannot be null.");
        this.startDate = d;
    }

    public LocalDate getEndDate()        { return endDate; }
    public void setEndDate(LocalDate d)  {
        if (d == null) throw new IllegalArgumentException("End date cannot be null.");
        this.endDate = d;
    }

    public String getDescription()       { return description; }
    public void setDescription(String v) { this.description = v == null ? "" : v.trim(); }

    public String getNotes()             { return notes; }
    public void setNotes(String v)       { this.notes = v == null ? "" : v.trim(); }

    public TripStatus getStatus()        { return status; }
    public void setStatus(TripStatus s)  {
        if (s == null) throw new IllegalArgumentException("Trip status cannot be null.");
        this.status = s;
    }

    public boolean isCompleted()         { return status == TripStatus.COMPLETED; }

    @Override
    public String toString() {
        return "Trip[" + getId() + "] " + destination + " (" + startDate + " → " + endDate + ")";
    }
}