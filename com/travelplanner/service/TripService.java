package com.travelplanner.service;

import com.travelplanner.model.Trip;
import com.travelplanner.model.TripStatus;
import com.travelplanner.repository.TripRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TripService {
    private final TripRepository tripRepository;

    public TripService(TripRepository tripRepository) {
        this.tripRepository = tripRepository;
    }

    // ── User-scoped creation ───────────────────────────────────────────────

    public Trip createTrip(int userId, String destination,
                           LocalDate startDate, LocalDate endDate,
                           String description) {
        validateDates(startDate, endDate);
        Trip trip = new Trip();
        trip.setUserId(userId);
        trip.setDestination(destination);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setDescription(description);
        trip.setNotes("");
        trip.setStatus(TripStatus.PLANNED);
        return tripRepository.save(trip);
    }

    /** Legacy no-user overload kept for ConsoleMenu compatibility. */
    public Trip createTrip(String destination, LocalDate startDate,
                           LocalDate endDate, String description) {
        return createTrip(0, destination, startDate, endDate, description);
    }

    // ── Queries ────────────────────────────────────────────────────────────

    /** All trips for a specific user. */
    public List<Trip> getAllTrips(int userId) {
        return tripRepository.findByUserId(userId);
    }

    /** All trips across all users (used for leaderboard). */
    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(int id) {
        return tripRepository.findById(id);
    }

    public List<Trip> getCompletedTrips(int userId) {
        return getAllTrips(userId).stream()
                .filter(Trip::isCompleted).collect(Collectors.toList());
    }

    public List<Trip> getCompletedTrips() {
        return getAllTrips().stream()
                .filter(Trip::isCompleted).collect(Collectors.toList());
    }

    // ── Mutations ──────────────────────────────────────────────────────────

    public boolean updateTrip(int id, String destination,
                              LocalDate startDate, LocalDate endDate,
                              String description, String notes) {
        validateDates(startDate, endDate);
        return tripRepository.findById(id).map(trip -> {
            trip.setDestination(destination);
            trip.setStartDate(startDate);
            trip.setEndDate(endDate);
            trip.setDescription(description);
            trip.setNotes(notes);
            return tripRepository.update(trip);
        }).orElse(false);
    }

    public boolean updateNotes(int tripId, String notes) {
        return tripRepository.findById(tripId).map(trip -> {
            trip.setNotes(notes);
            return tripRepository.update(trip);
        }).orElse(false);
    }

    public boolean deleteTrip(int id) {
        return tripRepository.delete(id);
    }

    /** Mark a trip as COMPLETED. */
    public boolean markTripCompleted(int id) {
        return tripRepository.findById(id).map(trip -> {
            trip.setStatus(TripStatus.COMPLETED);
            return tripRepository.update(trip);
        }).orElse(false);
    }

    /** Revert a COMPLETED trip back to PLANNED. */
    public boolean markTripPlanned(int id) {
        return tripRepository.findById(id).map(trip -> {
            trip.setStatus(TripStatus.PLANNED);
            return tripRepository.update(trip);
        }).orElse(false);
    }

    // ── Helpers ────────────────────────────────────────────────────────────

    private void validateDates(LocalDate start, LocalDate end) {
        if (start == null || end == null)
            throw new IllegalArgumentException("Trip dates cannot be empty.");
        if (end.isBefore(start))
            throw new IllegalArgumentException("End date cannot be before start date.");
    }
}