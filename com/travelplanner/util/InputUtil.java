package com.travelplanner.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

import com.travelplanner.model.ExpenseCategory;

public final class InputUtil {
    private InputUtil() {
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

        // ====================== MISSING METHODS FOR CONSOLEMENU ======================

    private static final Scanner scanner = new Scanner(System.in);

    public static String readLine(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }

    public static String readOptionalLine(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        return input.isEmpty() ? "" : input;
    }

    public static int readInt(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Invalid number. Try again.");
            }
        }
    }

    public static int readPositiveInt(String prompt) {
        while (true) {
            int value = readInt(prompt);
            if (value > 0) return value;
            System.out.println("Must be positive.");
        }
    }

    public static double readNonNegativeDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value >= 0) return value;
                System.out.println("Cannot be negative.");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number.");
            }
        }
    }

    public static LocalDate readDate(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine().trim());
            } catch (DateTimeParseException e) {
                System.out.println("Invalid date format. Use yyyy-MM-dd.");
            }
        }
    }

    public static ExpenseCategory readExpenseCategory() {
        System.out.println("\nAvailable categories:");
        ExpenseCategory[] categories = ExpenseCategory.values();
        for (int i = 0; i < categories.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, categories[i]);
        }
        while (true) {
            int choice = readInt("Choose category number: ");
            if (choice >= 1 && choice <= categories.length) {
                return categories[choice - 1];
            }
            System.out.println("Invalid choice.");
        }
    }
}
