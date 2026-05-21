package com.travelplanner.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class FileUtil {
    private FileUtil() {
    }

    public static void initializeDataFiles() {
        try {
            Path dataDirectory = Path.of("data");

            if (!Files.exists(dataDirectory)) {
                Files.createDirectory(dataDirectory);
            }

            createFileIfMissing("data/trips.txt");
            createFileIfMissing("data/itineraries.txt");
            createFileIfMissing("data/expenses.txt");
            createFileIfMissing("data/travel_logs.txt");

        } catch (IOException e) {
            System.out.println("Error initializing data files: " + e.getMessage());
        }
    }

    private static void createFileIfMissing(String fileName) throws IOException {
        Path path = Path.of(fileName);

        if (!Files.exists(path)) {
            Files.createFile(path);
        }
    }
}
