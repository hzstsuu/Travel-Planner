package com.travelplanner.service;

import com.travelplanner.model.TravelLog;
import com.travelplanner.repository.TravelLogRepository;
import com.travelplanner.repository.TripRepository;

import java.time.LocalDate;
import java.util.List;

public class TravelLogService {
    private final TravelLogRepository travelLogRepository;
    private final TripRepository tripRepository;

    public TravelLogService(TravelLogRepository travelLogRepository, TripRepository tripRepository) {
        this.travelLogRepository = travelLogRepository;
        this.tripRepository = tripRepository;
    }

    public TravelLog addTravelLog(
            int tripId,
            String reflection,
            String imagePath,
            LocalDate logDate
    ) {
        ensureTripExists(tripId);

        TravelLog travelLog = new TravelLog();
        travelLog.setTripId(tripId);
        travelLog.setReflection(reflection);
        travelLog.setImagePath(imagePath);
        travelLog.setLogDate(logDate);

        return travelLogRepository.save(travelLog);
    }

    public List<TravelLog> getLogsByTripId(int tripId) {
        ensureTripExists(tripId);
        return travelLogRepository.findByTripId(tripId);
    }

    public boolean updateTravelLog(
            int id,
            String reflection,
            String imagePath,
            LocalDate logDate
    ) {
        return travelLogRepository.findById(id)
                .map(log -> {
                    log.setReflection(reflection);
                    log.setImagePath(imagePath);
                    log.setLogDate(logDate);
                    return travelLogRepository.update(log);
                })
                .orElse(false);
    }

    public boolean deleteTravelLog(int id) {
        return travelLogRepository.delete(id);
    }

    public void deleteByTripId(int tripId) {
        travelLogRepository.deleteByTripId(tripId);
    }

    private void ensureTripExists(int tripId) {
        if (tripRepository.findById(tripId).isEmpty()) {
            throw new IllegalArgumentException("Trip with ID " + tripId + " does not exist.");
        }
    }
}
