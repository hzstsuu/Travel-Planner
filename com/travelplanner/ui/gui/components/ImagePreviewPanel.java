package com.travelplanner.ui.gui.components;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Image;
import java.io.File;
public class ImagePreviewPanel extends JPanel {
    private final JLabel label = new JLabel("No image", JLabel.CENTER);
    private String imagePath;
    public ImagePreviewPanel() {
        setLayout(new BorderLayout());
        add(label, BorderLayout.CENTER);
        label.setToolTipText("Click to open image");
        label.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                openImage();
            }
        });
    }
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
        if (imagePath == null || imagePath.isBlank()) {
            label.setText("No image");
            label.setIcon(null);
            return;
        }
        File file = new File(imagePath);
        if (!file.exists()) {
            label.setText("Image not found");
            label.setIcon(null);
            return;
        }
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaled = icon.getImage().getScaledInstance(220, 150, Image.SCALE_SMOOTH);
        label.setText("");
        label.setIcon(new ImageIcon(scaled));
    }
    private void openImage() {
        try {
            if (imagePath == null || imagePath.isBlank()) {
                return;
            }
            File file = new File(imagePath);
            if (file.exists() && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(file);
            }
        } catch (Exception ignored) {
            // Preview remains non-fatal.
        }
    }
}