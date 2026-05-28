package com.travelplanner.ui.gui.panels;

import com.travelplanner.model.User;
import com.travelplanner.service.SessionManager;
import com.travelplanner.service.UserService;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class SignUpPanel extends JPanel {

    private static final Color BG      = new Color(18,  20,  30);
    private static final Color CARD_BG = new Color(24,  26,  38);
    private static final Color BORDER  = new Color(55,  62,  88);
    private static final Color MUTED   = new Color(140, 150, 170);

    private final UserService    userService;
    private final Consumer<User> onSuccess;
    private final Runnable       onBack;
    private final Runnable       onLogin;

    private JTextField     usernameField;
    private JTextField     fullNameField;
    private JTextField     emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private JLabel         errorLabel;

    public SignUpPanel(UserService userService,
                       Consumer<User> onSuccess,
                       Runnable onBack,
                       Runnable onLogin) {
        this.userService = userService;
        this.onSuccess   = onSuccess;
        this.onBack      = onBack;
        this.onLogin     = onLogin;

        setLayout(new GridBagLayout());
        setBackground(BG);
        build();
    }

    private void build() {
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(30, 42, 30, 42)
        ));
        card.setPreferredSize(new Dimension(420, 545));

        GridBagConstraints g = new GridBagConstraints();
        g.fill      = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;
        g.insets    = new Insets(5, 0, 5, 0);

        // ── Title ─────────────────────────────────────────────────────────
        g.gridy = 0;
        JLabel title = new JLabel("Create Account ✈");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, g);

        g.gridy = 1;
        JLabel sub = new JLabel("Start planning your next adventure");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(sub, g);

        // ── Full Name ─────────────────────────────────────────────────────
        g.gridy = 2; g.insets = new Insets(12, 0, 3, 0);
        card.add(LoginPanel.fieldLabel("Full Name"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 6, 0);
        fullNameField = new JTextField(18);
        fullNameField.putClientProperty("JTextField.placeholderText", "Juan dela Cruz");
        card.add(fullNameField, g);

        // ── Username ──────────────────────────────────────────────────────
        g.gridy = 4; g.insets = new Insets(6, 0, 3, 0);
        card.add(LoginPanel.fieldLabel("Username"), g);
        g.gridy = 5; g.insets = new Insets(0, 0, 6, 0);
        usernameField = new JTextField(18);
        usernameField.putClientProperty("JTextField.placeholderText", "juantravel");
        card.add(usernameField, g);

        // ── Email ─────────────────────────────────────────────────────────
        g.gridy = 6; g.insets = new Insets(6, 0, 3, 0);
        card.add(LoginPanel.fieldLabel("Email (optional)"), g);
        g.gridy = 7; g.insets = new Insets(0, 0, 6, 0);
        emailField = new JTextField(18);
        emailField.putClientProperty("JTextField.placeholderText", "juan@example.com");
        card.add(emailField, g);

        // ── Password ─────────────────────────────────────────────────────
        g.gridy = 8; g.insets = new Insets(6, 0, 3, 0);
        card.add(LoginPanel.fieldLabel("Password"), g);
        g.gridy = 9; g.insets = new Insets(0, 0, 6, 0);
        passwordField = new JPasswordField(18);
        card.add(LoginPanel.buildPasswordRow(passwordField), g);

        // ── Confirm Password ──────────────────────────────────────────────
        g.gridy = 10; g.insets = new Insets(6, 0, 3, 0);
        card.add(LoginPanel.fieldLabel("Confirm Password"), g);
        g.gridy = 11; g.insets = new Insets(0, 0, 6, 0);
        confirmField = new JPasswordField(18);
        card.add(LoginPanel.buildPasswordRow(confirmField), g);

        // ── Password hint ─────────────────────────────────────────────────
        g.gridy = 12; g.insets = new Insets(0, 0, 10, 0);
        JLabel hint = new JLabel("Password must be at least 4 characters.");
        hint.setFont(new Font("SansSerif", Font.PLAIN, 11));
        hint.setForeground(new Color(110, 120, 140));
        card.add(hint, g);

        // ── Error label ───────────────────────────────────────────────────
        g.gridy = 13; g.insets = new Insets(0, 0, 8, 0);
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(new Color(220, 75, 75));
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(errorLabel, g);

        // ── Create Account button ─────────────────────────────────────────
        g.gridy = 14; g.insets = new Insets(0, 0, 10, 0);
        JButton registerBtn = GuiUtil.primaryButton("Create Account");
        registerBtn.setPreferredSize(new Dimension(320, 36));
        registerBtn.addActionListener(e -> doRegister());
        card.add(registerBtn, g);

        // ── "Already have an account?  Log In" ────────────────────────────
        // FIX: build the row manually so "Log In" is bold + bright accent blue
        g.gridy = 15; g.insets = new Insets(0, 0, 4, 0);
        JPanel linkRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 0));
        linkRow.setOpaque(false);

        JLabel alreadyLbl = new JLabel("Already have an account?");
        alreadyLbl.setForeground(MUTED);
        alreadyLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));

        // Plain JButton — no HTML, no underline — guaranteed visible on dark bg
        JButton loginLink = new JButton("Log In");
        loginLink.setFont(new Font("SansSerif", Font.BOLD, 13));
        loginLink.setForeground(new Color(100, 160, 255));   // bright accent blue
        loginLink.setBackground(CARD_BG);
        loginLink.setBorder(BorderFactory.createEmptyBorder(0, 2, 0, 2));
        loginLink.setContentAreaFilled(false);
        loginLink.setBorderPainted(false);
        loginLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        loginLink.setFocusPainted(false);
        // Hover: brighten to white-blue
        loginLink.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                loginLink.setForeground(new Color(160, 200, 255));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                loginLink.setForeground(new Color(100, 160, 255));
            }
        });
        loginLink.addActionListener(e -> onLogin.run());

        linkRow.add(alreadyLbl);
        linkRow.add(loginLink);
        card.add(linkRow, g);

        // ── Back to Leaderboard ───────────────────────────────────────────
        g.gridy = 16; g.insets = new Insets(2, 0, 0, 0);
        JButton backBtn = LoginPanel.grayLinkButton("← Back to Leaderboard");
        backBtn.setHorizontalAlignment(SwingConstants.CENTER);
        backBtn.addActionListener(e -> onBack.run());
        card.add(backBtn, g);

        add(card);
    }

    private void doRegister() {
        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String email    = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirm  = new String(confirmField.getPassword());

        if (fullName.isEmpty() || username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Full name, username, and password are required.");
            return;
        }
        if (password.length() < 4) {
            errorLabel.setText("Password must be at least 4 characters.");
            return;
        }
        if (!password.equals(confirm)) {
            errorLabel.setText("Passwords do not match.");
            return;
        }
        try {
            User user = userService.register(username, password, fullName, email);
            SessionManager.login(user);
            onSuccess.accept(user);
        } catch (IllegalArgumentException ex) {
            errorLabel.setText(ex.getMessage());
        }
    }
}