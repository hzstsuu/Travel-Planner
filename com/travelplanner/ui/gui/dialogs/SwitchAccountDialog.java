package com.travelplanner.ui.gui.dialogs;
 
import com.travelplanner.model.User;
import com.travelplanner.service.SessionManager;
import com.travelplanner.service.UserService;
import com.travelplanner.ui.gui.panels.LoginPanel;
import com.travelplanner.ui.gui.util.GuiUtil;
 
import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Optional;
 
/**
 * Modal "Switch Account" dialog triggered from the Sidebar.
 *
 * Differs from the public LoginPanel in three ways:
 *  1. Has a "← Back to App" button — user can cancel without losing their session.
 *  2. Does NOT show "Back to Leaderboard" or "Sign Up" links — those are irrelevant mid-session.
 *  3. Selecting an account immediately rebuilds the app for the new user.
 */
public class SwitchAccountDialog extends JDialog {
 
    private static final Color BG      = new Color(18,  20,  30);
    private static final Color CARD_BG = new Color(24,  26,  38);
    private static final Color BORDER  = new Color(55,  62,  88);
    private static final Color MUTED   = new Color(140, 150, 170);
    private static final Color DANGER_C = new Color(220, 75,  75);
 
    // Card names inside the dialog
    private static final String VIEW_PICKER = "picker";
    private static final String VIEW_FORM   = "form";
 
    private final UserService      userService;
    private final java.util.function.Consumer<User> onSwitch;  // called with the new user
 
    private final CardLayout dialogCards = new CardLayout();
    private final JPanel     dialogPanel = new JPanel(dialogCards);
 
    // Form widgets
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JLabel         formGreeting;
    // FIX: removed unused field "prefilledUsername" — it was assigned but never read
 
    // ── Constructor ───────────────────────────────────────────────────────
 
    public SwitchAccountDialog(JFrame owner,
                               UserService userService,
                               java.util.function.Consumer<User> onSwitch) {
        super(owner, "Switch Account", true);
        this.userService = userService;
        this.onSwitch    = onSwitch;
 
        setSize(480, 540);
        setLocationRelativeTo(owner);
        setResizable(false);
        setLayout(new BorderLayout());
 
        dialogPanel.setBackground(BG);
        dialogPanel.add(buildPickerView(), VIEW_PICKER);
        dialogPanel.add(buildFormView(),   VIEW_FORM);
        add(dialogPanel, BorderLayout.CENTER);
 
        showPicker();
    }
 
    // ── VIEW 1: Account picker ────────────────────────────────────────────
 
    private JPanel buildPickerView() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
 
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(26, 32, 26, 32)
        ));
        card.setPreferredSize(new Dimension(420, 480));
 
        // Header
        JPanel header = new JPanel(new BorderLayout(0, 4));
        header.setOpaque(false);
 
        JLabel title = new JLabel("🔄  Switch Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
 
        JLabel sub = new JLabel("Choose an account to switch to");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);
 
        // Currently logged-in badge
        String currentBadge = SessionManager.isLoggedIn()
                ? "Currently logged in as: @" + SessionManager.current().getUsername()
                : "";
        JLabel currentLbl = new JLabel(currentBadge);
        currentLbl.setFont(new Font("SansSerif", Font.PLAIN, 11));
        currentLbl.setForeground(new Color(82, 160, 100));
 
        header.add(title,      BorderLayout.NORTH);
        header.add(sub,        BorderLayout.CENTER);
        header.add(currentLbl, BorderLayout.SOUTH);
        card.add(header, BorderLayout.NORTH);
 
        // Account list — excludes currently logged-in user
        JPanel accountList = new JPanel();
        accountList.setOpaque(false);
        accountList.setLayout(new BoxLayout(accountList, BoxLayout.Y_AXIS));
 
        int currentId = SessionManager.currentUserId();
        List<User> others = userService.getAllUsers().stream()
                .filter(u -> u.getId() != currentId)
                .toList();
 
        if (others.isEmpty()) {
            JPanel emptyPanel = new JPanel(new GridBagLayout());
            emptyPanel.setOpaque(false);
            JLabel emptyLbl = new JLabel("No other accounts found.");
            emptyLbl.setForeground(MUTED);
            emptyLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
            emptyPanel.add(emptyLbl);
            accountList.add(emptyPanel);
        } else {
            for (User u : others) {
                accountList.add(buildAccountRow(u));
                accountList.add(Box.createVerticalStrut(8));
            }
        }
 
        JScrollPane scroll = new JScrollPane(accountList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        card.add(scroll, BorderLayout.CENTER);
 
        // ── Footer: Use different + Back to App ───────────────────────────
        JPanel footer = new JPanel();
        footer.setOpaque(false);
        footer.setLayout(new BoxLayout(footer, BoxLayout.Y_AXIS));
 
        JButton otherBtn = LoginPanel.linkButton("Use a different account");
        otherBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherBtn.addActionListener(e -> showForm(null));
        footer.add(otherBtn);
        footer.add(Box.createVerticalStrut(8));
 
        // Back to App — prominent, clearly labelled
        JButton backBtn = new JButton("← Back to App");
        backBtn.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backBtn.setForeground(new Color(120, 130, 150));
        backBtn.setBorderPainted(false);
        backBtn.setContentAreaFilled(false);
        backBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> dispose());   // close dialog, return to app
        footer.add(backBtn);
 
        card.add(footer, BorderLayout.SOUTH);
        root.add(card);
        return root;
    }
 
    /** Single account row with avatar + name + click to select. */
    private JPanel buildAccountRow(User user) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setBackground(new Color(30, 33, 48));
        row.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        row.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
 
        // Avatar
        Color bg = avatarColor(user.getUsername());
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(bg);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(42, 42));
        JLabel initLbl = new JLabel(initials(user.getFullName()), SwingConstants.CENTER);
        initLbl.setFont(new Font("SansSerif", Font.BOLD, 15));
        initLbl.setForeground(Color.WHITE);
        initLbl.setOpaque(false);
        avatar.add(initLbl);
 
        // Text
        JLabel nameLbl = new JLabel(
                user.getFullName().isBlank() ? user.getUsername() : user.getFullName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setForeground(Color.WHITE);
 
        JLabel userLbl = new JLabel("@" + user.getUsername());
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLbl.setForeground(MUTED);
 
        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        text.add(nameLbl);
        text.add(Box.createVerticalStrut(2));
        text.add(userLbl);
 
        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("SansSerif", Font.PLAIN, 22));
        arrow.setForeground(MUTED);
 
        row.add(avatar, BorderLayout.WEST);
        row.add(text,   BorderLayout.CENTER);
        row.add(arrow,  BorderLayout.EAST);
 
        // Hover + click
        row.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                row.setBackground(new Color(40, 44, 64));
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GuiUtil.ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                row.setBackground(new Color(30, 33, 48));
                row.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 14)));
            }
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                // FIX: removed "prefilledUsername = user.getUsername();" — field deleted
                showForm(user);
            }
        });
 
        return row;
    }
 
    // ── VIEW 2: Password form ─────────────────────────────────────────────
 
    private JPanel buildFormView() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setBackground(BG);
 
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(34, 42, 34, 42)
        ));
        card.setPreferredSize(new Dimension(420, 400));
 
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;
        g.insets = new Insets(5, 0, 5, 0);
 
        g.gridy = 0;
        JLabel title = new JLabel("🔄  Switch Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, g);
 
        g.gridy = 1;
        formGreeting = new JLabel(" ");
        formGreeting.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formGreeting.setForeground(MUTED);
        formGreeting.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(formGreeting, g);
 
        g.gridy = 2; g.insets = new Insets(14, 0, 3, 0);
        card.add(LoginPanel.fieldLabel("Username"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 8, 0);
        usernameField = new JTextField(18);
        usernameField.putClientProperty("JTextField.placeholderText", "username");
        card.add(usernameField, g);
 
        g.gridy = 4; g.insets = new Insets(6, 0, 3, 0);
        card.add(LoginPanel.fieldLabel("Password"), g);
        g.gridy = 5; g.insets = new Insets(0, 0, 4, 0);
        passwordField = new JPasswordField(18);
        card.add(LoginPanel.buildPasswordRow(passwordField), g);
 
        g.gridy = 6; g.insets = new Insets(0, 0, 10, 0);
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(DANGER_C);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(errorLabel, g);
 
        g.gridy = 7; g.insets = new Insets(0, 0, 10, 0);
        JButton switchBtn = GuiUtil.primaryButton("Switch Account");
        switchBtn.setPreferredSize(new Dimension(320, 36));
        switchBtn.addActionListener(e -> doSwitch());
        card.add(switchBtn, g);
 
        // ── Back links ────────────────────────────────────────────────────
        g.gridy = 8; g.insets = new Insets(4, 0, 0, 0);
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        backRow.setOpaque(false);
 
        JButton backToPicker = LoginPanel.grayLinkButton("← Choose account");
        backToPicker.addActionListener(e -> showPicker());
 
        JLabel pipe = new JLabel("|");
        pipe.setForeground(new Color(70, 78, 100));
 
        JButton backToApp = new JButton("← Back to App");
        backToApp.setFont(new Font("SansSerif", Font.PLAIN, 12));
        backToApp.setForeground(new Color(120, 130, 150));
        backToApp.setBorderPainted(false);
        backToApp.setContentAreaFilled(false);
        backToApp.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        backToApp.addActionListener(e -> dispose());
 
        backRow.add(backToPicker);
        backRow.add(pipe);
        backRow.add(backToApp);
        card.add(backRow, g);
 
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> doSwitch());
 
        root.add(card);
        return root;
    }
 
    // ── View switching ────────────────────────────────────────────────────
 
    private void showPicker() {
        dialogPanel.removeAll();
        dialogPanel.add(buildPickerView(), VIEW_PICKER);
        dialogPanel.add(buildFormView(),   VIEW_FORM);
        dialogCards.show(dialogPanel, VIEW_PICKER);
        dialogPanel.revalidate();
        dialogPanel.repaint();
    }
 
    private void showForm(User prefill) {
        if (prefill != null) {
            usernameField.setText(prefill.getUsername());
            usernameField.setEditable(false);
            String first = prefill.getFullName().isBlank()
                    ? prefill.getUsername()
                    : prefill.getFullName().split(" ")[0];
            formGreeting.setText("Switching to " + first + "'s account 👋");
        } else {
            usernameField.setText("");
            usernameField.setEditable(true);
            formGreeting.setText("Enter the account's credentials");
        }
        errorLabel.setText(" ");
        passwordField.setText("");
        dialogCards.show(dialogPanel, VIEW_FORM);
        SwingUtilities.invokeLater(passwordField::requestFocus);
    }
 
    // ── Switch logic ──────────────────────────────────────────────────────
 
    private void doSwitch() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
 
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields.");
            return;
        }
        Optional<User> opt = userService.login(username, password);
        if (opt.isPresent()) {
            User newUser = opt.get();
            // Don't switch to the same account
            if (SessionManager.isLoggedIn()
                    && SessionManager.current().getId() == newUser.getId()) {
                errorLabel.setText("You are already logged in as @" + username + ".");
                return;
            }
            SessionManager.login(newUser);
            LoginPanel.restoreAccount(username);
            dispose();
            onSwitch.accept(newUser);
        } else {
            errorLabel.setText("Incorrect username or password.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
 
    // ── Helpers ───────────────────────────────────────────────────────────
 
    private static String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, 1).toUpperCase();
        return (p[0].substring(0, 1) + p[p.length - 1].substring(0, 1)).toUpperCase();
    }
 
    private static Color avatarColor(String username) {
        Color[] palette = {
            new Color(82,  130, 255), new Color(155, 89,  182),
            new Color(46,  204, 113), new Color(230, 126, 34),
            new Color(231, 76,  60),  new Color(52,  152, 219),
            new Color(26,  188, 156), new Color(241, 196, 15),
        };
        return palette[Math.abs(username.hashCode()) % palette.length];
    }
}