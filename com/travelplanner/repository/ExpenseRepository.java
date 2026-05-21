package com.travelplanner.repository;

import com.travelplanner.model.Expense;
import com.travelplanner.model.ExpenseCategory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

public class ExpenseRepository extends FileRepository<Expense> {

    public ExpenseRepository(String filePath) {
        super(filePath);
    }

    public List<Expense> findByTripId(int tripId) {
        return findAll()
                .stream()
                .filter(expense -> expense.getTripId() == tripId)
                .collect(Collectors.toList());
    }

    public void deleteByTripId(int tripId) {
        List<Expense> remaining = findAll()
                .stream()
                .filter(expense -> expense.getTripId() != tripId)
                .collect(Collectors.toList());

        writeAll(remaining);
    }

    @Override
    protected String serialize(Expense expense) {
        return expense.getId()
                + "|" + expense.getTripId()
                + "|" + expense.getCategory()
                + "|" + escape(expense.getDescription())
                + "|" + expense.getAmount()
                + "|" + expense.getDate();
    }

    @Override
    protected Expense deserialize(String line) {
        String[] parts = splitLine(line);

        return new Expense(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                ExpenseCategory.valueOf(parts[2]),
                unescape(parts[3]),
                Double.parseDouble(parts[4]),
                LocalDate.parse(parts[5])
        );
    }
}
