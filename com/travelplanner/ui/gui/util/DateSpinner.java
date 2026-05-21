package com.travelplanner.ui.gui.util;
import javax.swing.JSpinner;
import javax.swing.SpinnerDateModel;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
public class DateSpinner extends JSpinner {
    public DateSpinner(LocalDate initialDate) {
        super(new SpinnerDateModel());
        setEditor(new JSpinner.DateEditor(this, "yyyy-MM-dd"));
        setDate(initialDate == null ? LocalDate.now() : initialDate);
    }
    public LocalDate getDate() {
        Date date = (Date) getValue();
        return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
    }
    public void setDate(LocalDate localDate) {
        Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        setValue(date);
    }
}