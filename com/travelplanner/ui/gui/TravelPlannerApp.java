package com.travelplanner.ui.gui;

import com.travelplanner.model.User;
import com.travelplanner.service.*;
import com.travelplanner.ui.gui.dialogs.SwitchAccountDialog;
import com.travelplanner.ui.gui.panels.*;
import com.travelplanner.ui.gui.theme.ThemeManager;
import com.travelplanner.ui.gui.util.GuiUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Main application window.
 *
 * Flow:
 *   App starts → LeaderboardPanel (public)
 *     ├── "Log In"   → LoginPanel  → onSuccess → main app shell
 *     └── "Sign Up"  → SignUpPanel → onSuccess → main app shell
 *
 *   Inside the app:
 *     "Switch Account" → opens SwitchAccountDialog (modal, has ← Back to App)
 *     "Log Out"        → clears session and returns to leaderboard
 */
public class TravelPlannerApp extends JFrame {

    // ── Root card names ───────────────────────────────────────────────────
    public static final String CARD_LEADERBOARD = "leaderboard";
    public static final String CARD_LOGIN        = "login";
    public static final String CARD_SIGNUP       = "signup";
    public static final String CARD_APP          = "app";

    // ── In-app content card names ─────────────────────────────────────────
    public static final String CARD_DASHBOARD  = "dashboard";
    public static final String CARD_TRIPS      = "trips";
    public static final String CARD_ITINERARY  = "itinerary";
    public static final String CARD_EXPENSES   = "expenses";
    public static final String CARD_LOGS       = "logs";
    public static final String CARD_SETTINGS   = "settings";

    // ── Services ──────────────────────────────────────────────────────────
    private final UserService      userService;
    private final TripService      tripService;
    private final ItineraryService itineraryService;
    private final ExpenseService   expenseService;
    private final TravelLogService travelLogService;

    // ── Root card panel ───────────────────────────────────────────────────
    private final CardLayout rootLayout = new CardLayout();
    private final JPanel     rootPanel  = new JPanel(rootLayout);

    // ── App shell ─────────────────────────────────────────────────────────
    private SidebarPanel    sidebarPanel;
    private JPanel          appShell;
    private final CardLayout appContentLayout = new CardLayout();
    private final JPanel     appContentPanel  = new JPanel(appContentLayout);

    private DashboardPanel  dashboardPanel;
    private TripsPanel      tripsPanel;
    private ItineraryPanel  itineraryPanel;
    private ExpensesPanel   expensesPanel;
    private LogsPanel       logsPanel;
    private SettingsPanel   settingsPanel;

    // ── Status bar widgets ────────────────────────────────────────────────
    private JLabel statusLabel;
    private JLabel clockLabel;
    private JLabel userLabel;

    // ── Constructor ───────────────────────────────────────────────────────

    public TravelPlannerApp(UserService userService,
                            TripService tripService,
                            ItineraryService itineraryService,
                            ExpenseService expenseService,
                            TravelLogService travelLogService) {
        this.userService      = userService;
        this.tripService      = tripService;
        this.itineraryService = itineraryService;
        this.expenseService   = expenseService;
        this.travelLogService = travelLogService;

        ThemeManager.installDarkTheme();
        configureFrame();
        buildRoot();
        startClock();
        showCard(CARD_LEADERBOARD);
    }

    // ── Frame ─────────────────────────────────────────────────────────────

    private void configureFrame() {
        setTitle("Travel Planner");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(900, 560));
        setSize(900, 560);
        setLocationRelativeTo(null);
    }

    // ── Root panel ────────────────────────────────────────────────────────

    private void buildRoot() {
        setLayout(new BorderLayout());
        add(rootPanel);

        // 1. Public leaderboard
        LeaderboardPanel lb = new LeaderboardPanel(
                userService, tripService, expenseService,
                () -> showCard(CARD_LOGIN),
                () -> showCard(CARD_SIGNUP)
        );
        rootPanel.add(lb, CARD_LEADERBOARD);

        // 2. Login panel (public flow only — Switch Account uses its own dialog)
        LoginPanel loginPanel = new LoginPanel(
                userService,
                this::onLoginSuccess,
                () -> showCard(CARD_LEADERBOARD),
                () -> showCard(CARD_SIGNUP)
        );
        rootPanel.add(loginPanel, CARD_LOGIN);

        // 3. Sign Up
        SignUpPanel signUpPanel = new SignUpPanel(
                userService,
                this::onLoginSuccess,
                () -> showCard(CARD_LEADERBOARD),
                () -> showCard(CARD_LOGIN)
        );
        rootPanel.add(signUpPanel, CARD_SIGNUP);
    }

    // ── Login / account-switch success ────────────────────────────────────

    private void onLoginSuccess(User user) {
        setTitle("Travel Planner — " + user.getFullName());
        buildAppShell();
        showCard(CARD_APP);
        navigateTo(CARD_DASHBOARD);
        if (userLabel != null) {
            userLabel.setText("Logged in as: " + user.getUsername());
        }
    }

    private void buildAppShell() {
        if (appShell != null) rootPanel.remove(appShell);

        dashboardPanel = new DashboardPanel(tripService, expenseService, this::openTripDetails);
        tripsPanel     = new TripsPanel(tripService, itineraryService,
                                        expenseService, travelLogService, this::openTripDetails);
        itineraryPanel = new ItineraryPanel(tripService, itineraryService);
        expensesPanel  = new ExpensesPanel(tripService, expenseService);
        logsPanel      = new LogsPanel(tripService, travelLogService);
        settingsPanel  = new SettingsPanel(dark -> {
            ThemeManager.setDark(dark);
            if (statusLabel != null)
                statusLabel.setText(dark ? "Dark theme enabled" : "Light theme enabled");
        });

        appContentPanel.removeAll();
        appContentPanel.setBorder(BorderFactory.createEmptyBorder(16, 18, 16, 18));
        appContentPanel.add(dashboardPanel,  CARD_DASHBOARD);
        appContentPanel.add(tripsPanel,      CARD_TRIPS);
        appContentPanel.add(itineraryPanel,  CARD_ITINERARY);
        appContentPanel.add(expensesPanel,   CARD_EXPENSES);
        appContentPanel.add(logsPanel,       CARD_LOGS);
        appContentPanel.add(settingsPanel,   CARD_SETTINGS);

        sidebarPanel = new SidebarPanel(
                this::navigateTo,
                this::logout,
                this::switchAccount   // opens SwitchAccountDialog
        );

        appShell = new JPanel(new BorderLayout());
        appShell.add(sidebarPanel,    BorderLayout.WEST);
        appShell.add(appContentPanel, BorderLayout.CENTER);
        appShell.add(createStatusBar(), BorderLayout.SOUTH);

        rootPanel.add(appShell, CARD_APP);
        registerKeyboardShortcuts();
    }

    // ── Navigation ────────────────────────────────────────────────────────

    private void showCard(String card) {
        rootLayout.show(rootPanel, card);
    }

    public void navigateTo(String cardName) {
        appContentLayout.show(appContentPanel, cardName);
        sidebarPanel.setActive(cardName);
        switch (cardName) {
            case CARD_DASHBOARD -> { dashboardPanel.refresh();  statusLabel.setText("Dashboard"); }
            case CARD_TRIPS     -> { tripsPanel.refresh();      statusLabel.setText("My Trips"); }
            case CARD_ITINERARY -> { itineraryPanel.refresh();  statusLabel.setText("Itinerary"); }
            case CARD_EXPENSES  -> { expensesPanel.refresh();   statusLabel.setText("Expenses"); }
            case CARD_LOGS      -> { logsPanel.refresh();       statusLabel.setText("Travel Diary"); }
            case CARD_SETTINGS  ->                              statusLabel.setText("Settings");
            default             ->                              statusLabel.setText("Ready");
        }
    }

    // ── Logout ────────────────────────────────────────────────────────────

    private void logout() {
        SessionManager.logout();
        setTitle("Travel Planner");
        rootPanel.removeAll();
        buildRoot();
        showCard(CARD_LEADERBOARD);
        rootPanel.revalidate();
        rootPanel.repaint();
    }

    // ── Switch Account ────────────────────────────────────────────────────
    // Opens a modal dialog — user can cancel with "← Back to App"
    // and their current session is NOT disrupted until they confirm a switch.

    private void switchAccount() {
        SwitchAccountDialog dlg = new SwitchAccountDialog(
                this,
                userService,
                newUser -> {
                    // Rebuild the app shell for the new user
                    onLoginSuccess(newUser);
                }
        );
        dlg.setVisible(true);
    }

    // ── Trip detail ───────────────────────────────────────────────────────

    private void openTripDetails(int tripId) {
        TripDetailDialog dlg = new TripDetailDialog(
                this, tripId, tripService, itineraryService,
                expenseService, travelLogService,
                () -> {
                    dashboardPanel.refresh();
                    tripsPanel.refresh();
                    itineraryPanel.refresh();
                    expensesPanel.refresh();
                    logsPanel.refresh();
                }
        );
        dlg.setVisible(true);
    }

    // ── Status bar ────────────────────────────────────────────────────────

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 65, 85)),
                BorderFactory.createEmptyBorder(5, 14, 5, 14)
        ));

        statusLabel = new JLabel("Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        userLabel = new JLabel(
                SessionManager.isLoggedIn()
                        ? "Logged in as: " + SessionManager.current().getUsername()
                        : "",
                SwingConstants.CENTER);
        userLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        userLabel.setForeground(new Color(150, 160, 180));

        clockLabel = new JLabel("", SwingConstants.RIGHT);
        clockLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(userLabel,   BorderLayout.CENTER);
        bar.add(clockLabel,  BorderLayout.EAST);
        return bar;
    }

    // ── Clock ─────────────────────────────────────────────────────────────

    private void startClock() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("HH:mm:ss");
        Timer t = new Timer(1000, e -> {
            if (clockLabel != null) clockLabel.setText(LocalTime.now().format(fmt));
        });
        t.setInitialDelay(0);
        t.start();
    }

    // ── Keyboard shortcuts (Ctrl+1–5, no tooltips shown in sidebar) ───────

    private void registerKeyboardShortcuts() {
        JRootPane rp = getRootPane();
        rp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_1, KeyEvent.CTRL_DOWN_MASK), "nav1");
        rp.getActionMap().put("nav1", GuiUtil.action(() -> navigateTo(CARD_DASHBOARD)));
        rp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_2, KeyEvent.CTRL_DOWN_MASK), "nav2");
        rp.getActionMap().put("nav2", GuiUtil.action(() -> navigateTo(CARD_TRIPS)));
        rp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_3, KeyEvent.CTRL_DOWN_MASK), "nav3");
        rp.getActionMap().put("nav3", GuiUtil.action(() -> navigateTo(CARD_ITINERARY)));
        rp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_4, KeyEvent.CTRL_DOWN_MASK), "nav4");
        rp.getActionMap().put("nav4", GuiUtil.action(() -> navigateTo(CARD_EXPENSES)));
        rp.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_5, KeyEvent.CTRL_DOWN_MASK), "nav5");
        rp.getActionMap().put("nav5", GuiUtil.action(() -> navigateTo(CARD_LOGS)));
    }
}