package com.travelplanner.repository;

import com.travelplanner.model.ItineraryItem;

import java.util.List;
import java.util.stream.Collectors;

public class ItineraryRepository extends FileRepository<ItineraryItem> {

    public ItineraryRepository(String filePath) {
        super(filePath);
    }

    public List<ItineraryItem> findByTripId(int tripId) {
        return findAll()
                .stream()
                .filter(item -> item.getTripId() == tripId)
                .collect(Collectors.toList());
    }

    public void deleteByTripId(int tripId) {
        List<ItineraryItem> remaining = findAll()
                .stream()
                .filter(item -> item.getTripId() != tripId)
                .collect(Collectors.toList());

        writeAll(remaining);
    }

    @Override
    protected String serialize(ItineraryItem item) {
        return item.getId()
                + "|" + item.getTripId()
                + "|" + item.getDayNumber()
                + "|" + escape(item.getActivity());
    }

    @Override
    protected ItineraryItem deserialize(String line) {
        String[] parts = splitLine(line);

        return new ItineraryItem(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                Integer.parseInt(parts[2]),
                unescape(parts[3])
        );
    }
}
