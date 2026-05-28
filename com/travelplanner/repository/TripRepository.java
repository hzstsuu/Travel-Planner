package com.travelplanner.repository;

import com.travelplanner.model.Trip;
import com.travelplanner.model.TripStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TripRepository extends FileRepository<Trip> {

    public TripRepository(String filePath) {
        super(filePath);
    }

    public List<Trip> findByUserId(int userId) {
        return findAll().stream()
                .filter(t -> t.getUserId() == userId)
                .collect(Collectors.toList());
    }

    @Override
    protected String serialize(Trip t) {
        return t.getId()
                + "|" + t.getUserId()
                + "|" + escape(t.getDestination())
                + "|" + t.getStartDate()
                + "|" + t.getEndDate()
                + "|" + escape(t.getDescription())
                + "|" + escape(t.getNotes())
                + "|" + t.getStatus();
    }

    @Override
    protected Trip deserialize(String line) {
        String[] p = splitLine(line);
        // Support old 7-field format (no userId) and new 8-field format
        if (p.length == 7) {
            return new Trip(
                    Integer.parseInt(p[0]),
                    0,                          // legacy: userId = 0
                    unescape(p[1]),
                    LocalDate.parse(p[2]),
                    LocalDate.parse(p[3]),
                    unescape(p[4]),
                    unescape(p[5]),
                    TripStatus.valueOf(p[6])
            );
        }
        return new Trip(
                Integer.parseInt(p[0]),
                Integer.parseInt(p[1]),
                unescape(p[2]),
                LocalDate.parse(p[3]),
                LocalDate.parse(p[4]),
                unescape(p[5]),
                unescape(p[6]),
                TripStatus.valueOf(p[7])
        );
    }
}