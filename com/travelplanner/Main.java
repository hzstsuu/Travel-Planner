package com.travelplanner;
import com.travelplanner.repository.ExpenseRepository;
import com.travelplanner.repository.ItineraryRepository;
import com.travelplanner.repository.TravelLogRepository;
import com.travelplanner.repository.TripRepository;
import com.travelplanner.service.ExpenseService;
import com.travelplanner.service.ItineraryService;
import com.travelplanner.service.TravelLogService; 
import com.travelplanner.service.TripService;
import com.travelplanner.ui.gui.TravelPlannerApp;
import com.travelplanner.util.FileUtil;
import javax.swing.SwingUtilities;
public class Main {
    public static void main(String[] args) {
        FileUtil.initializeDataFiles();
        TripRepository tripRepository = new TripRepository("data/trips.txt");
        ItineraryRepository itineraryRepository = new ItineraryRepository("data/itineraries.txt");
        ExpenseRepository expenseRepository = new ExpenseRepository("data/expenses.txt");
        TravelLogRepository travelLogRepository = new TravelLogRepository("data/travel_logs.txt");
        TripService tripService = new TripService(tripRepository);
        ItineraryService itineraryService = new ItineraryService(itineraryRepository, tripRepository);
        ExpenseService expenseService = new ExpenseService(expenseRepository, tripRepository);
        TravelLogService travelLogService = new TravelLogService(travelLogRepository, tripRepository);
        SwingUtilities.invokeLater(() -> {
            TravelPlannerApp app = new TravelPlannerApp(
                    tripService,
                    itineraryService,
                    expenseService,
                    travelLogService
            );
            app.setVisible(true);
        });
    }
}