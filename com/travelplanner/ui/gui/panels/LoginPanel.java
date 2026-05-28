package com.travelplanner.ui.gui.panels;
 
import com.travelplanner.model.User;
import com.travelplanner.service.SessionManager;
import com.travelplanner.service.UserService;
import com.travelplanner.ui.gui.util.GuiUtil;
 
import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.prefs.Preferences;
 
/**
 * Login screen with:
 *  1. Account picker  — Facebook-style cards. Each card has an X to hide it
 *                        from the list (account is restored when you log in again).
 *  2. Show/hide password eye toggle.
 *  3. Forgot password  — verified by Full Name + Username → set new password.
 */
public class LoginPanel extends JPanel {
 
    // ── Palette ───────────────────────────────────────────────────────────
    private static final Color BG       = new Color(18,  20,  30);
    private static final Color CARD_BG  = new Color(24,  26,  38);
    private static final Color BORDER   = new Color(55,  62,  88);
    private static final Color MUTED    = new Color(140, 150, 170);
    private static final Color DANGER_C = new Color(220, 75,  75);
 
    // ── Prefs key for dismissed usernames ─────────────────────────────────
    private static final Preferences PREFS =
            Preferences.userNodeForPackage(LoginPanel.class);
    private static final String PREF_DISMISSED = "dismissed_accounts";
 
    // ── State ─────────────────────────────────────────────────────────────
    private final UserService    userService;
    private final Consumer<User> onSuccess;
    private final Runnable       onBack;
    private final Runnable       onSignUp;
 
    private String prefilledUsername = null;
 
    // ── Card-layout views ─────────────────────────────────────────────────
    private static final String VIEW_PICKER = "picker";
    private static final String VIEW_FORM   = "form";
 
    private final CardLayout viewLayout = new CardLayout();
    private final JPanel     viewPanel  = new JPanel(viewLayout);
 
    // ── Form-view widgets ─────────────────────────────────────────────────
    private JTextField     usernameField;
    private JPasswordField passwordField;
    private JLabel         errorLabel;
    private JLabel         formGreeting;
 
    // ── Constructor ───────────────────────────────────────────────────────
 
    public LoginPanel(UserService userService,
                      Consumer<User> onSuccess,
                      Runnable onBack,
                      Runnable onSignUp) {
        this.userService = userService;
        this.onSuccess   = onSuccess;
        this.onBack      = onBack;
        this.onSignUp    = onSignUp;
 
        setLayout(new GridBagLayout());
        setBackground(BG);
        buildViews();
        add(viewPanel);
    }
 
    // ── Build views ───────────────────────────────────────────────────────
 
    private void buildViews() {
        viewPanel.setOpaque(false);
        viewPanel.add(buildPickerView(), VIEW_PICKER);
        viewPanel.add(buildFormView(),   VIEW_FORM);
        showPicker();
    }
 
    // ── VIEW 1: Account picker ────────────────────────────────────────────
 
    private JPanel buildPickerView() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);
 
        JPanel card = new JPanel(new BorderLayout(0, 14));
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(28, 34, 28, 34)
        ));
        card.setPreferredSize(new Dimension(460, 510));
 
        // Title
        JLabel title = new JLabel("Choose an Account");
        title.setFont(new Font("SansSerif", Font.BOLD, 21));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
 
        JLabel sub = new JLabel("Select your account or log in with a different one");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(MUTED);
        sub.setHorizontalAlignment(SwingConstants.CENTER);
 
        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        titleBlock.add(title);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(sub);
        card.add(titleBlock, BorderLayout.NORTH);
 
        // Account list (rebuilt each time so dismissed set is respected)
        JPanel accountList = new JPanel();
        accountList.setOpaque(false);
        accountList.setLayout(new BoxLayout(accountList, BoxLayout.Y_AXIS));
 
        Set<String> dismissed = getDismissed();
        List<User> users = userService.getAllUsers().stream()
                .filter(u -> !dismissed.contains(u.getUsername()))
                .toList();
 
        if (users.isEmpty()) {
            JLabel empty = new JLabel("No saved accounts — sign up or use a different account.");
            empty.setForeground(MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            accountList.add(Box.createVerticalStrut(20));
            accountList.add(empty);
        } else {
            for (User u : users) {
                accountList.add(buildAccountCard(u, accountList));
                accountList.add(Box.createVerticalStrut(8));
            }
        }
 
        JScrollPane scroll = new JScrollPane(accountList);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.setPreferredSize(new Dimension(390, 280));
        card.add(scroll, BorderLayout.CENTER);
 
        // Bottom links
        JPanel bottom = new JPanel();
        bottom.setOpaque(false);
        bottom.setLayout(new BoxLayout(bottom, BoxLayout.Y_AXIS));
 
        JButton otherBtn = linkButton("Use a different account");
        otherBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        otherBtn.addActionListener(e -> { prefilledUsername = null; showForm(null); });
 
        JPanel signupRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signupRow.setOpaque(false);
        JLabel noAccLbl = new JLabel("Don't have an account?");
        noAccLbl.setForeground(MUTED);
        noAccLbl.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JButton signupLink = new JButton("Sign Up");
        signupLink.setBorderPainted(false);
        signupLink.setContentAreaFilled(false);
        signupLink.setForeground(GuiUtil.ACCENT);
        signupLink.setFont(new Font("SansSerif", Font.BOLD, 13));
        signupLink.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLink.addActionListener(e -> onSignUp.run());
        signupRow.add(noAccLbl);
        signupRow.add(signupLink);
 
        JButton backBtn = grayLinkButton("← Back to Leaderboard");
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.addActionListener(e -> onBack.run());
 
        bottom.add(otherBtn);
        bottom.add(Box.createVerticalStrut(6));
        bottom.add(signupRow);
        bottom.add(Box.createVerticalStrut(4));
        bottom.add(backBtn);
        card.add(bottom, BorderLayout.SOUTH);
 
        root.add(card);
        return root;
    }
 
    /** One account card — avatar + name + @username + ✕ dismiss button. */
    private JPanel buildAccountCard(User user, JPanel listContainer) {
        JPanel card = new JPanel(new BorderLayout(12, 0));
        card.setBackground(new Color(30, 33, 48));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(10, 14, 10, 10)
        ));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 64));
 
        // Avatar circle
        Color avatarBg = avatarColor(user.getUsername());
        JPanel avatar = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                        RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(avatarBg);
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
 
        // Name + @username
        JLabel nameLbl = new JLabel(
                user.getFullName().isBlank() ? user.getUsername() : user.getFullName());
        nameLbl.setFont(new Font("SansSerif", Font.BOLD, 14));
        nameLbl.setForeground(Color.WHITE);
 
        JLabel userLbl = new JLabel("@" + user.getUsername());
        userLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLbl.setForeground(MUTED);
 
        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        textBlock.add(nameLbl);
        textBlock.add(Box.createVerticalStrut(2));
        textBlock.add(userLbl);
 
        // ── ✕ dismiss button ──────────────────────────────────────────────
        JButton dismissBtn = new JButton("✕");
        dismissBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
        dismissBtn.setForeground(new Color(130, 140, 160));
        dismissBtn.setBorderPainted(false);
        dismissBtn.setContentAreaFilled(false);
        dismissBtn.setFocusPainted(false);
        dismissBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        dismissBtn.setToolTipText("Remove from list");
        dismissBtn.setPreferredSize(new Dimension(28, 28));
        dismissBtn.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                dismissBtn.setForeground(DANGER_C);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                dismissBtn.setForeground(new Color(130, 140, 160));
            }
        });
        dismissBtn.addActionListener(e -> {
            addToDismissed(user.getUsername());
            showPicker();
        });
 
        // Right panel: arrow + dismiss stacked
        JPanel rightPanel = new JPanel(new BorderLayout(0, 0));
        rightPanel.setOpaque(false);
        JLabel arrow = new JLabel("›");
        arrow.setFont(new Font("SansSerif", Font.PLAIN, 22));
        arrow.setForeground(MUTED);
        rightPanel.add(arrow,      BorderLayout.CENTER);
        rightPanel.add(dismissBtn, BorderLayout.EAST);
 
        card.add(avatar,     BorderLayout.WEST);
        card.add(textBlock,  BorderLayout.CENTER);
        card.add(rightPanel, BorderLayout.EAST);
 
        card.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(40, 44, 64));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(GuiUtil.ACCENT, 1, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 10)));
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                card.setBackground(new Color(30, 33, 48));
                card.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(BORDER, 1, true),
                        BorderFactory.createEmptyBorder(10, 14, 10, 10)));
            }
            @Override public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getSource() == card) {
                    prefilledUsername = user.getUsername();
                    showForm(user);
                }
            }
        });
 
        return card;
    }
 
    // ── VIEW 2: Password form ─────────────────────────────────────────────
 
    private JPanel buildFormView() {
        JPanel root = new JPanel(new GridBagLayout());
        root.setOpaque(false);
 
        JPanel card = new JPanel(new GridBagLayout());
        card.setBackground(CARD_BG);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1, true),
                BorderFactory.createEmptyBorder(34, 42, 34, 42)
        ));
        card.setPreferredSize(new Dimension(420, 450));
 
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;
        g.insets = new Insets(5, 0, 5, 0);
 
        g.gridy = 0;
        JLabel title = new JLabel("Welcome Back ✈");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(title, g);
 
        g.gridy = 1;
        formGreeting = new JLabel(" ");
        formGreeting.setFont(new Font("SansSerif", Font.PLAIN, 13));
        formGreeting.setForeground(MUTED);
        formGreeting.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(formGreeting, g);
 
        g.gridy = 2; g.insets = new Insets(12, 0, 3, 0);
        card.add(fieldLabel("Username"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 8, 0);
        usernameField = new JTextField(18);
        usernameField.putClientProperty("JTextField.placeholderText", "your_username");
        card.add(usernameField, g);
 
        g.gridy = 4; g.insets = new Insets(6, 0, 3, 0);
        card.add(fieldLabel("Password"), g);
        g.gridy = 5; g.insets = new Insets(0, 0, 2, 0);
        passwordField = new JPasswordField(18);
        passwordField.putClientProperty("JTextField.placeholderText", "••••••••");
        card.add(buildPasswordRow(passwordField), g);
 
        g.gridy = 6; g.insets = new Insets(0, 0, 10, 0);
        JPanel forgotRow = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        forgotRow.setOpaque(false);
        JButton forgotBtn = linkButton("Forgot password?");
        forgotBtn.addActionListener(e -> openForgotPasswordDialog());
        forgotRow.add(forgotBtn);
        card.add(forgotRow, g);
 
        g.gridy = 7; g.insets = new Insets(0, 0, 8, 0);
        errorLabel = new JLabel(" ");
        errorLabel.setForeground(DANGER_C);
        errorLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        card.add(errorLabel, g);
 
        g.gridy = 8; g.insets = new Insets(0, 0, 10, 0);
        JButton loginBtn = GuiUtil.primaryButton("Log In");
        loginBtn.setPreferredSize(new Dimension(320, 36));
        loginBtn.addActionListener(e -> doLogin());
        card.add(loginBtn, g);
 
        g.gridy = 9; g.insets = new Insets(0, 0, 4, 0);
        JPanel signupRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0));
        signupRow.setOpaque(false);
        JLabel noAcc = new JLabel("Don't have an account?");
        noAcc.setForeground(MUTED);
        noAcc.setFont(new Font("SansSerif", Font.PLAIN, 13));
        JButton signupLink2 = new JButton("Sign Up");
        signupLink2.setBorderPainted(false);
        signupLink2.setContentAreaFilled(false);
        signupLink2.setForeground(GuiUtil.ACCENT);
        signupLink2.setFont(new Font("SansSerif", Font.BOLD, 13));
        signupLink2.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        signupLink2.addActionListener(e -> onSignUp.run());
        signupRow.add(noAcc);
        signupRow.add(signupLink2);
        card.add(signupRow, g);
 
        g.gridy = 10; g.insets = new Insets(2, 0, 0, 0);
        JPanel backRow = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
        backRow.setOpaque(false);
        JButton backToPicker = grayLinkButton("← Choose account");
        backToPicker.addActionListener(e -> showPicker());
        JLabel pipe = new JLabel("|");
        pipe.setForeground(new Color(70, 78, 100));
        JButton backToLb = grayLinkButton("Leaderboard");
        backToLb.addActionListener(e -> onBack.run());
        backRow.add(backToPicker);
        backRow.add(pipe);
        backRow.add(backToLb);
        card.add(backRow, g);
 
        usernameField.addActionListener(e -> passwordField.requestFocus());
        passwordField.addActionListener(e -> doLogin());
 
        root.add(card);
        return root;
    }
 
    // ── View switching ────────────────────────────────────────────────────
 
    public void showPicker() {
        viewPanel.removeAll();
        viewPanel.add(buildPickerView(), VIEW_PICKER);
        viewPanel.add(buildFormView(),   VIEW_FORM);
        viewLayout.show(viewPanel, VIEW_PICKER);
        viewPanel.revalidate();
        viewPanel.repaint();
    }
 
    private void showForm(User prefill) {
        if (prefill != null) {
            usernameField.setText(prefill.getUsername());
            usernameField.setEditable(false);
            String first = prefill.getFullName().isBlank()
                    ? prefill.getUsername()
                    : prefill.getFullName().split(" ")[0];
            formGreeting.setText("Hi, " + first + "! 👋");
        } else {
            usernameField.setText("");
            usernameField.setEditable(true);
            formGreeting.setText("Log in to your Travel Planner account");
        }
        errorLabel.setText(" ");
        passwordField.setText("");
        viewLayout.show(viewPanel, VIEW_FORM);
        SwingUtilities.invokeLater(passwordField::requestFocus);
    }
 
    // ── Dismissed accounts (persisted via java.util.prefs) ───────────────
 
    private Set<String> getDismissed() {
        String raw = PREFS.get(PREF_DISMISSED, "");
        Set<String> set = new HashSet<>();
        if (!raw.isBlank()) {
            for (String s : raw.split(",")) {
                if (!s.isBlank()) set.add(s.trim());
            }
        }
        return set;
    }
 
    private void addToDismissed(String username) {
        Set<String> current = getDismissed();
        current.add(username);
        PREFS.put(PREF_DISMISSED, String.join(",", current));
    }
 
    /**
     * Called after a successful login so the account reappears in the picker.
     */
    public static void restoreAccount(String username) {
        Set<String> current = new HashSet<>();
        String raw = PREFS.get(PREF_DISMISSED, "");
        if (!raw.isBlank()) {
            for (String s : raw.split(",")) {
                if (!s.isBlank()) current.add(s.trim());
            }
        }
        current.remove(username);
        PREFS.put(PREF_DISMISSED, String.join(",", current));
    }
 
    // ── Forgot password dialog ────────────────────────────────────────────
 
    private void openForgotPasswordDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dlg  = new JDialog(owner instanceof Frame f ? f : null,
                "Reset Password", true);
        dlg.setSize(460, 430);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());
 
        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(26, 32, 10, 32));
 
        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridwidth = 2;
        g.insets = new Insets(4, 0, 4, 0);
 
        g.gridy = 0;
        JLabel hdr = new JLabel("🔑  Reset Your Password");
        hdr.setFont(new Font("SansSerif", Font.BOLD, 18));
        content.add(hdr, g);
 
        g.gridy = 1;
        JLabel hint = new JLabel(
            "<html><span style='color:#9AA4B2;font-size:11px'>"
            + "Enter your <b>Username</b> and registered <b>Full Name</b> "
            + "to verify your identity, then choose a new password.</span></html>");
        content.add(hint, g);
 
        g.gridy = 2; g.insets = new Insets(12, 0, 3, 0);
        content.add(fieldLabel("Full Name (as registered)"), g);
        g.gridy = 3; g.insets = new Insets(0, 0, 5, 0);
        JTextField fullNameField = new JTextField(18);
        fullNameField.putClientProperty("JTextField.placeholderText", "Juan dela Cruz");
        content.add(fullNameField, g);
 
        g.gridy = 4; g.insets = new Insets(6, 0, 3, 0);
        content.add(fieldLabel("Username"), g);
        g.gridy = 5; g.insets = new Insets(0, 0, 5, 0);
        JTextField userField = new JTextField(18);
        if (prefilledUsername != null) userField.setText(prefilledUsername);
        userField.putClientProperty("JTextField.placeholderText", "your_username");
        content.add(userField, g);
 
        g.gridy = 6; g.insets = new Insets(6, 0, 3, 0);
        content.add(fieldLabel("New Password"), g);
        g.gridy = 7; g.insets = new Insets(0, 0, 5, 0);
        JPasswordField newPF = new JPasswordField(18);
        content.add(buildPasswordRow(newPF), g);
 
        g.gridy = 8; g.insets = new Insets(6, 0, 3, 0);
        content.add(fieldLabel("Confirm New Password"), g);
        g.gridy = 9; g.insets = new Insets(0, 0, 10, 0);
        JPasswordField confirmPF = new JPasswordField(18);
        content.add(buildPasswordRow(confirmPF), g);
 
        g.gridy = 10; g.insets = new Insets(0, 0, 0, 0);
        JLabel errLbl = new JLabel(" ");
        errLbl.setForeground(DANGER_C);
        errLbl.setFont(new Font("SansSerif", Font.PLAIN, 12));
        errLbl.setHorizontalAlignment(SwingConstants.CENTER);
        content.add(errLbl, g);
 
        dlg.add(content, BorderLayout.CENTER);
 
        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 12));
        JButton cancelBtn = new JButton("Cancel");
        JButton resetBtn  = GuiUtil.primaryButton("Reset Password");
 
        cancelBtn.addActionListener(e -> dlg.dispose());
        resetBtn.addActionListener(e -> {
            String fn  = fullNameField.getText().trim();
            String un  = userField.getText().trim();
            String np  = new String(newPF.getPassword());
            String cp  = new String(confirmPF.getPassword());
 
            if (fn.isEmpty() || un.isEmpty() || np.isEmpty()) {
                errLbl.setText("All fields are required."); return;
            }
            if (np.length() < 4) {
                errLbl.setText("Password must be at least 4 characters."); return;
            }
            if (!np.equals(cp)) {
                errLbl.setText("Passwords do not match."); return;
            }
            boolean ok = userService.resetPassword(un, fn, np);
            if (ok) {
                dlg.dispose();
                JOptionPane.showMessageDialog(this,
                        "Password reset! You can now log in with your new password.",
                        "✅ Success", JOptionPane.INFORMATION_MESSAGE);
                prefilledUsername = un;
                showForm(userService.findByUsername(un).orElse(null));
            } else {
                errLbl.setText("Full Name or Username did not match any account.");
            }
        });
 
        actions.add(cancelBtn);
        actions.add(resetBtn);
        dlg.add(actions, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }
 
    // ── Login logic ───────────────────────────────────────────────────────
 
    private void doLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        if (username.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please fill in all fields."); return;
        }
        Optional<User> opt = userService.login(username, password);
        if (opt.isPresent()) {
            SessionManager.login(opt.get());
            restoreAccount(username);
            errorLabel.setText(" ");
            onSuccess.accept(opt.get());
        } else {
            errorLabel.setText("Incorrect username or password.");
            passwordField.setText("");
            passwordField.requestFocus();
        }
    }
 
    // ── Static helpers (reused by SignUpPanel and SwitchAccountDialog) ────
    // FIX: changed from package-private to public so dialogs package can access them
 
    public static JPanel buildPasswordRow(JPasswordField field) {
        JPanel row = new JPanel(new BorderLayout(4, 0));
        row.setOpaque(false);
        JToggleButton eye = new JToggleButton("👁");
        eye.setFont(new Font("SansSerif", Font.PLAIN, 14));
        eye.setPreferredSize(new Dimension(36, 30));
        eye.setFocusPainted(false);
        eye.setToolTipText("Show password");
        eye.putClientProperty("JButton.buttonType", "borderless");
        eye.addActionListener(e -> {
            if (eye.isSelected()) { field.setEchoChar((char) 0); eye.setToolTipText("Hide password"); }
            else                  { field.setEchoChar('•');       eye.setToolTipText("Show password"); }
        });
        row.add(field, BorderLayout.CENTER);
        row.add(eye,   BorderLayout.EAST);
        return row;
    }
 
    public static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(new Font("SansSerif", Font.PLAIN, 13));
        return l;
    }
 
    public static JButton linkButton(String text) {
        JButton b = new JButton("<html><u>" + text + "</u></html>");
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(GuiUtil.ACCENT);
        b.setFont(new Font("SansSerif", Font.PLAIN, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    public static JButton grayLinkButton(String text) {
        JButton b = new JButton(text);
        b.setBorderPainted(false);
        b.setContentAreaFilled(false);
        b.setForeground(new Color(120, 130, 150));
        b.setFont(new Font("SansSerif", Font.PLAIN, 12));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
 
    private static String initials(String name) {
        if (name == null || name.isBlank()) return "?";
        String[] p = name.trim().split("\\s+");
        if (p.length == 1) return p[0].substring(0, 1).toUpperCase();
        return (p[0].substring(0, 1) + p[p.length - 1].substring(0, 1)).toUpperCase();
    }
 
    private static Color avatarColor(String username) {
        Color[] palette = {
            new Color(82, 130, 255), new Color(155, 89, 182), new Color(46, 204, 113),
            new Color(230, 126, 34), new Color(231, 76,  60), new Color(52, 152, 219),
            new Color(26, 188, 156), new Color(241, 196, 15),
        };
        return palette[Math.abs(username.hashCode()) % palette.length];
    }
}