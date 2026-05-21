package com.travelplanner.repository;

import com.travelplanner.model.Trip;
import com.travelplanner.model.TripStatus;

import java.time.LocalDate;

public class TripRepository extends FileRepository<Trip> {

    public TripRepository(String filePath) {
        super(filePath);
    }

    @Override
    protected String serialize(Trip trip) {
        return trip.getId()
                + "|" + escape(trip.getDestination())
                + "|" + trip.getStartDate()
                + "|" + trip.getEndDate()
                + "|" + escape(trip.getDescription())
                + "|" + escape(trip.getNotes())
                + "|" + trip.getStatus();
    }

    @Override
    protected Trip deserialize(String line) {
        String[] parts = splitLine(line);

        return new Trip(
                Integer.parseInt(parts[0]),
                unescape(parts[1]),
                LocalDate.parse(parts[2]),
                LocalDate.parse(parts[3]),
                unescape(parts[4]),
                unescape(parts[5]),
                TripStatus.valueOf(parts[6])
        );
    }
}
