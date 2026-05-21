package com.travelplanner.repository;

import com.travelplanner.model.TravelLog;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class TravelLogRepository extends FileRepository<TravelLog> {

    public TravelLogRepository(String filePath) {
        super(filePath);
    }

    public List<TravelLog> findByTripId(int tripId) {
        return findAll()
                .stream()
                .filter(log -> log.getTripId() == tripId)
                .collect(Collectors.toList());
    }

    public void deleteByTripId(int tripId) {
        List<TravelLog> remaining = findAll()
                .stream()
                .filter(log -> log.getTripId() != tripId)
                .collect(Collectors.toList());

        writeAll(remaining);
    }

    @Override
    protected String serialize(TravelLog log) {
        return log.getId()
                + "|" + log.getTripId()
                + "|" + escape(log.getReflection())
                + "|" + escape(log.getImagePath())
                + "|" + log.getLogDate();
    }

    @Override
    protected TravelLog deserialize(String line) {
        String[] parts = splitLine(line);

        return new TravelLog(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                unescape(parts[2]),
                unescape(parts[3]),
                LocalDate.parse(parts[4])
        );
    }
}
