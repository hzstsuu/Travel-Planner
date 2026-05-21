package com.travelplanner.service;

import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseCategory;
import com.travelplanner.repository.ExpenseRepository;
import com.travelplanner.repository.TripRepository;

import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;

    public ExpenseService(ExpenseRepository expenseRepository, TripRepository tripRepository) {
        this.expenseRepository = expenseRepository;
        this.tripRepository = tripRepository;
    }

    public Expense addExpense(
            int tripId,
            ExpenseCategory category,
            String description,
            double amount,
            LocalDate date
    ) {
        ensureTripExists(tripId);

        Expense expense = new Expense();
        expense.setTripId(tripId);
        expense.setCategory(category);
        expense.setDescription(description);
        expense.setAmount(amount);
        expense.setDate(date);

        return expenseRepository.save(expense);
    }

    public List<Expense> getExpensesByTripId(int tripId) {
        ensureTripExists(tripId);
        return expenseRepository.findByTripId(tripId);
    }

    public boolean updateExpense(
            int id,
            ExpenseCategory category,
            String description,
            double amount,
            LocalDate date
    ) {
        return expenseRepository.findById(id)
                .map(expense -> {
                    expense.setCategory(category);
                    expense.setDescription(description);
                    expense.setAmount(amount);
                    expense.setDate(date);
                    return expenseRepository.update(expense);
                })
                .orElse(false);
    }

    public boolean deleteExpense(int id) {
        return expenseRepository.delete(id);
    }

    public double getTotalExpensesForTrip(int tripId) {
        ensureTripExists(tripId);

        return expenseRepository.findByTripId(tripId)
                .stream()
                .mapToDouble(Expense::getAmount)
                .sum();
    }

    public Map<ExpenseCategory, Double> getBudgetSummaryByCategory(int tripId) {
        ensureTripExists(tripId);

        Map<ExpenseCategory, Double> summary = new EnumMap<>(ExpenseCategory.class);

        for (ExpenseCategory category : ExpenseCategory.values()) {
            summary.put(category, 0.0);
        }

        for (Expense expense : expenseRepository.findByTripId(tripId)) {
            summary.put(
                    expense.getCategory(),
                    summary.get(expense.getCategory()) + expense.getAmount()
            );
        }

        return summary;
    }

    public void deleteByTripId(int tripId) {
        expenseRepository.deleteByTripId(tripId);
    }

    private void ensureTripExists(int tripId) {
        if (tripRepository.findById(tripId).isEmpty()) {
            throw new IllegalArgumentException("Trip with ID " + tripId + " does not exist.");
        }
    }
}
