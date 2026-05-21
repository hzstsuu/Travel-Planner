package com.travelplanner.service;

import com.travelplanner.model.ItineraryItem;
import com.travelplanner.repository.ItineraryRepository;
import com.travelplanner.repository.TripRepository;

import java.util.Comparator;
import java.util.List;

public class ItineraryService {
    private final ItineraryRepository itineraryRepository;
    private final TripRepository tripRepository;

    public ItineraryService(ItineraryRepository itineraryRepository, TripRepository tripRepository) {
        this.itineraryRepository = itineraryRepository;
        this.tripRepository = tripRepository;
    }

    public ItineraryItem addItineraryItem(int tripId, int dayNumber, String activity) {
        ensureTripExists(tripId);

        ItineraryItem item = new ItineraryItem();
        item.setTripId(tripId);
        item.setDayNumber(dayNumber);
        item.setActivity(activity);

        return itineraryRepository.save(item);
    }

    public List<ItineraryItem> getItineraryByTripId(int tripId) {
        ensureTripExists(tripId);

        List<ItineraryItem> items = itineraryRepository.findByTripId(tripId);
        items.sort(Comparator.comparingInt(ItineraryItem::getDayNumber));
        return items;
    }

    public boolean updateItineraryItem(int id, int dayNumber, String activity) {
        return itineraryRepository.findById(id)
                .map(item -> {
                    item.setDayNumber(dayNumber);
                    item.setActivity(activity);
                    return itineraryRepository.update(item);
                })
                .orElse(false);
    }

    public boolean deleteItineraryItem(int id) {
        return itineraryRepository.delete(id);
    }

    public void deleteByTripId(int tripId) {
        itineraryRepository.deleteByTripId(tripId);
    }

    private void ensureTripExists(int tripId) {
        if (tripRepository.findById(tripId).isEmpty()) {
            throw new IllegalArgumentException("Trip with ID " + tripId + " does not exist.");
        }
    }
}
