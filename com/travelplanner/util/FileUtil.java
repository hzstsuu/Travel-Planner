package com.travelplanner.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {
    private FileUtil() {}

    public static void initializeDataFiles() {
        try {
            Path dir = Path.of("data");
            if (!Files.exists(dir)) Files.createDirectory(dir);

            createIfMissing("data/users.txt");        // NEW
            createIfMissing("data/trips.txt");
            createIfMissing("data/itineraries.txt");
            createIfMissing("data/expenses.txt");
            createIfMissing("data/travel_logs.txt");
        } catch (IOException e) {
            System.out.println("Error initializing data files: " + e.getMessage());
        }
    }

    private static void createIfMissing(String fileName) throws IOException {
        Path p = Path.of(fileName);
        if (!Files.exists(p)) Files.createFile(p);
    }
}