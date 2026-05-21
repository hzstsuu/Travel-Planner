package com.travelplanner.ui.gui.util;
import com.travelplanner.model.Expense;
import com.travelplanner.model.ItineraryItem;
import com.travelplanner.model.TravelLog;
import com.travelplanner.model.Trip;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
public final class ExportUtil {
    private ExportUtil() {
    }
    public static void exportTripAsText(
            Trip trip,
            List<ItineraryItem> itinerary,
            List<Expense> expenses,
            List<TravelLog> logs,
            Path outputPath
    ) throws IOException {
        StringBuilder builder = new StringBuilder();
        builder.append("TRAVEL PLANNER EXPORT\n");
        builder.append("=====================\n\n");
        builder.append(trip).append("\n\n");
        builder.append("ITINERARY\n");
        builder.append("---------\n");
        for (ItineraryItem item : itinerary) {
            builder.append("Day ")
                    .append(item.getDayNumber())
                    .append(": ")
                    .append(item.getActivity())
                    .append("\n");
        }
        builder.append("\nEXPENSES\n");
        builder.append("--------\n");
        for (Expense expense : expenses) {
            builder.append(expense.getDate())
                    .append(" | ")
                    .append(expense.getCategory())
                    .append(" | ")
                    .append(expense.getDescription())
                    .append(" | ")
                    .append(expense.getAmount())
                    .append("\n");
        }
        builder.append("\nTRAVEL LOGS\n");
        builder.append("-----------\n");
        for (TravelLog log : logs) {
            builder.append(log.getLogDate())
                    .append(": ")
                    .append(log.getReflection())
                    .append("\nImage: ")
                    .append(log.getImagePath())
                    .append("\n\n");
        }
        Files.writeString(outputPath, builder.toString());
    }
}