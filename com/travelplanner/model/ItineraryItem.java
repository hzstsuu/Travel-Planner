package com.travelplanner.model;

public class ItineraryItem extends BaseEntity {
    private int tripId;
    private int dayNumber;
    private String activity;

    public ItineraryItem() {
    }

    public ItineraryItem(int id, int tripId, int dayNumber, String activity) {
        super(id);
        this.tripId = tripId;
        this.dayNumber = dayNumber;
        this.activity = activity;
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

    public int getDayNumber() {
        return dayNumber;
    }

    public void setDayNumber(int dayNumber) {
        if (dayNumber <= 0) {
            throw new IllegalArgumentException("Day number must be positive.");
        }
        this.dayNumber = dayNumber;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        if (activity == null || activity.trim().isEmpty()) {
            throw new IllegalArgumentException("Activity cannot be empty.");
        }
        this.activity = activity.trim();
    }

    @Override
    public String toString() {
        return "Itinerary ID: " + getId()
                + "\nTrip ID: " + tripId
                + "\nDay: " + dayNumber
                + "\nActivity: " + activity;
    }
}
