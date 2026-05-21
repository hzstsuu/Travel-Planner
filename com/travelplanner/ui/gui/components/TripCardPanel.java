package com.travelplanner.ui.gui.components;
import com.travelplanner.model.Trip;
import com.travelplanner.ui.gui.util.GuiUtil;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.util.function.IntConsumer;
public class TripCardPanel extends JPanel {
    public TripCardPanel(Trip trip, IntConsumer onOpen) {
        setLayout(new BorderLayout(10, 8));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 86, 104), 1, true),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Open trip details");
        JLabel destination = new JLabel("📍 " + trip.getDestination());
        destination.setFont(new Font("SansSerif", Font.BOLD, 18));
        JLabel dates = new JLabel(trip.getStartDate() + " → " + trip.getEndDate());
        dates.setForeground(new Color(145, 155, 170));
        JLabel status = new JLabel(trip.getStatus().toString());
        status.setOpaque(true);
        status.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        status.setBackground(trip.isCompleted() ? new Color(39, 174, 96) : new Color(52, 152, 219));
        status.setForeground(Color.WHITE);
        JLabel desc = new JLabel("<html>" + GuiUtil.escapeHtml(
                trip.getDescription().isBlank() ? "No description." : trip.getDescription()
        ) + "</html>");
        add(destination, BorderLayout.NORTH);
        add(desc, BorderLayout.CENTER);
        JPanel bottom = new JPanel(new BorderLayout());
        bottom.setOpaque(false);
        bottom.add(dates, BorderLayout.WEST);
        bottom.add(status, BorderLayout.EAST);
        add(bottom, BorderLayout.SOUTH);
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                onOpen.accept(trip.getId());
            }
        });
    }
}