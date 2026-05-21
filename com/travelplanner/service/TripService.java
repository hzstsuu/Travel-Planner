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

    public Trip createTrip(
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            String description
    ) {
        validateTripDates(startDate, endDate);

        Trip trip = new Trip();
        trip.setDestination(destination);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setDescription(description);
        trip.setNotes("");
        trip.setStatus(TripStatus.PLANNED);

        return tripRepository.save(trip);
    }

    public List<Trip> getAllTrips() {
        return tripRepository.findAll();
    }

    public Optional<Trip> getTripById(int id) {
        return tripRepository.findById(id);
    }

    public boolean updateTrip(
            int id,
            String destination,
            LocalDate startDate,
            LocalDate endDate,
            String description,
            String notes
    ) {
        validateTripDates(startDate, endDate);

        Optional<Trip> optionalTrip = tripRepository.findById(id);

        if (optionalTrip.isEmpty()) {
            return false;
        }

        Trip trip = optionalTrip.get();
        trip.setDestination(destination);
        trip.setStartDate(startDate);
        trip.setEndDate(endDate);
        trip.setDescription(description);
        trip.setNotes(notes);

        return tripRepository.update(trip);
    }

    public boolean updateNotes(int tripId, String notes) {
        Optional<Trip> optionalTrip = tripRepository.findById(tripId);

        if (optionalTrip.isEmpty()) {
            return false;
        }

        Trip trip = optionalTrip.get();
        trip.setNotes(notes);

        return tripRepository.update(trip);
    }

    public boolean deleteTrip(int id) {
        return tripRepository.delete(id);
    }

    public boolean markTripCompleted(int id) {
        Optional<Trip> optionalTrip = tripRepository.findById(id);

        if (optionalTrip.isEmpty()) {
            return false;
        }

        Trip trip = optionalTrip.get();
        trip.setStatus(TripStatus.COMPLETED);

        return tripRepository.update(trip);
    }

    public List<Trip> getCompletedTrips() {
        return tripRepository.findAll()
                .stream()
                .filter(Trip::isCompleted)
                .collect(Collectors.toList());
    }

    private void validateTripDates(LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new IllegalArgumentException("Trip dates cannot be empty.");
        }

        if (endDate.isBefore(startDate)) {
            throw new IllegalArgumentException("End date cannot be before start date.");
        }
    }
}
