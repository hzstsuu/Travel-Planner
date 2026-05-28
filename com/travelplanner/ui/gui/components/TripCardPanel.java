package com.travelplanner.ui.gui.components;
 
import com.travelplanner.model.Trip;
import com.travelplanner.ui.gui.util.GuiUtil;
 
import javax.swing.*;
import java.awt.*;
import java.util.function.IntConsumer;
 
public class TripCardPanel extends JPanel {
 
    public TripCardPanel(Trip trip, IntConsumer onOpen) {
        setLayout(new BorderLayout(0, 3));   // FIX: reduced vertical gap from 6 → 3
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(60, 66, 88), 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)  // FIX: tighter padding (was 12,14)
        ));
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setToolTipText("Open trip details");
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 72));  // FIX: reduced max height 90 → 72
 
        // Destination
        JLabel dest = new JLabel("📍 " + trip.getDestination());
        dest.setFont(new Font("SansSerif", Font.BOLD, 13));   // FIX: slightly smaller font 15 → 13
 
        // Description — single line, truncated
        String desc = trip.getDescription().isBlank() ? "No description." : trip.getDescription();
        JLabel descLbl = new JLabel("<html><span style='color:#9AA4B2'>"
                + GuiUtil.escapeHtml(desc.length() > 60 ? desc.substring(0, 60) + "…" : desc)
                + "</span></html>");
        descLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));  // FIX: smaller font 12 → 11
 
        // Bottom row: dates + status badge
        JPanel bottom = new JPanel(new BorderLayout(0, 0));
        bottom.setOpaque(false);
 
        JLabel dates = new JLabel(trip.getStartDate() + " → " + trip.getEndDate());
        dates.setFont(new Font("SansSerif", Font.PLAIN, 10));   // FIX: smaller 11 → 10
        dates.setForeground(new Color(140, 150, 170));
 
        JLabel status = new JLabel(trip.getStatus().toString());
        status.setOpaque(true);
        status.setFont(new Font("SansSerif", Font.BOLD, 10));
        status.setBorder(BorderFactory.createEmptyBorder(2, 6, 2, 6));  // FIX: tighter badge padding
        status.setBackground(trip.isCompleted() ? new Color(39, 174, 96) : new Color(52, 130, 219));
        status.setForeground(Color.WHITE);
 
        bottom.add(dates,  BorderLayout.WEST);
        bottom.add(status, BorderLayout.EAST);
 
        add(dest,    BorderLayout.NORTH);
        add(descLbl, BorderLayout.CENTER);
        add(bottom,  BorderLayout.SOUTH);
 
        // Click → open details
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                onOpen.accept(trip.getId());
            }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(82, 130, 255), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(60, 66, 88), 1, true),
                        BorderFactory.createEmptyBorder(8, 12, 8, 12)
                ));
            }
        });
    }
}