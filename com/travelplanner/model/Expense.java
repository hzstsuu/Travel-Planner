package com.travelplanner.model;

import java.time.LocalDate;

public class Expense extends BaseEntity {
    private int tripId;
    private ExpenseCategory category;
    private String description;
    private double amount;
    private LocalDate date;

    public Expense() {
    }

    public Expense(
            int id,
            int tripId,
            ExpenseCategory category,
            String description,
            double amount,
            LocalDate date
    ) {
        super(id);
        this.tripId = tripId;
        this.category = category;
        this.description = description;
        this.amount = amount;
        this.date = date;
    }

    public int getTripId() {
        return tripId;
    }

    public void setTripId(int tripId) {
        if (tripId <= 0) {
            throw new IllegalArgumentException("Trip ID must be positive.");
        }
        this.tripId = tripId;
    }

    public ExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(ExpenseCategory category) {
        if (category == null) {
            throw new IllegalArgumentException("Expense category cannot be null.");
        }
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? "" : description.trim();
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        if (amount < 0) {
            throw new IllegalArgumentException("Amount cannot be negative.");
        }
        this.amount = amount;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        if (date == null) {
            throw new IllegalArgumentException("Expense date cannot be null.");
        }
        this.date = date;
    }

    @Override
    public String toString() {
        return "Expense ID: " + getId()
                + "\nTrip ID: " + tripId
                + "\nCategory: " + category
                + "\nDescription: " + description
                + "\nAmount: " + amount
                + "\nDate: " + date;
    }
}
