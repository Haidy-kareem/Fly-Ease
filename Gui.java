package oopflightbookingsystem.src;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.util.ArrayList;

import static oopflightbookingsystem.src.UI.*;

public class Gui {

    private static BookingSystem bookingSystem;
    private static JFrame mainFrame;
    private static CardLayout cardLayout;
    private static JPanel rootPanel;
    private static JLabel statusBar;

    // ── Entry Point ────────────────────────────────────────────
    public static void main(String[] args) {
        String dataPath = System.getProperty("user.dir") + java.io.File.separator + "data";
        bookingSystem = new BookingSystem(dataPath);

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        UIManager.put("OptionPane.background", BG_CARD);
        UIManager.put("Panel.background", BG_CARD);
        UIManager.put("OptionPane.messageForeground", TEXT_PRIMARY);

        SwingUtilities.invokeLater(Gui::buildMainFrame);
    }

    // ── Frame Setup ───────────────────────────────────────────
    private static void buildMainFrame() {
        mainFrame = new JFrame("FlyEase — Flight Booking System");
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setSize(1180, 760);
        mainFrame.setMinimumSize(new Dimension(960, 640));
        mainFrame.getContentPane().setBackground(BG_TOP);

        cardLayout = new CardLayout();
        rootPanel  = new JPanel(cardLayout);

        rootPanel.add(buildWelcomePanel(),   "Welcome");
        rootPanel.add(buildLoginPanel(),     "Login");
        rootPanel.add(buildRegisterPanel(),  "Register");
        rootPanel.add(buildCustomerPanel(),  "Customer");
        rootPanel.add(buildAgentPanel(),     "Agent");
        rootPanel.add(buildAdminPanel(),     "Admin");

        statusBar = new JLabel(" Ready");
        statusBar.setFont(FONT_SMALL);
        statusBar.setForeground(TEXT_MUTED);
        statusBar.setBackground(new Color(5, 8, 16));
        statusBar.setOpaque(true);
        statusBar.setBorder(new EmptyBorder(6, 16, 6, 16));

        mainFrame.setLayout(new BorderLayout());
        mainFrame.add(rootPanel, BorderLayout.CENTER);
        mainFrame.add(statusBar, BorderLayout.SOUTH);
        mainFrame.setLocationRelativeTo(null);
        mainFrame.addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { bookingSystem.saveData(); }
        });
        mainFrame.setVisible(true);
        showScreen("Welcome");
    }

    private static void showScreen(String name) { cardLayout.show(rootPanel, name); }
    private static void setStatus(String msg) { statusBar.setText("   " + msg); }

    // ══════════════════════════════════════════════════════════
    //  WELCOME PANEL
    // ══════════════════════════════════════════════════════════
    private static JPanel buildWelcomePanel() {
        BackgroundPanel bg = new BackgroundPanel();
        bg.setLayout(new GridBagLayout());

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel badge = badgePanel(Icons.Type.PLANE, CYAN);
        badge.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(badge);
        content.add(Box.createVerticalStrut(18));

        GlowTitle title = new GlowTitle("Fly", "Ease", CYAN, FONT_TITLE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);

        JLabel sub = new JLabel("Your Gateway to Seamless Travel");
        sub.setFont(FONT_BODY);
        sub.setForeground(TEXT_MUTED);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(sub);
        content.add(Box.createVerticalStrut(36));

        JPanel rows = new JPanel();
        rows.setOpaque(false);
        rows.setLayout(new BoxLayout(rows, BoxLayout.Y_AXIS));
        rows.setAlignmentX(Component.CENTER_ALIGNMENT);

        rows.add(actionRow(Icons.Type.KEY, "LOGIN", "Access your account",
                CYAN, e -> showScreen("Login")));
        rows.add(Box.createVerticalStrut(14));
        rows.add(actionRow(Icons.Type.USER_PLUS, "REGISTER", "Create a new account",
                VIOLET, e -> showScreen("Register")));
        rows.add(Box.createVerticalStrut(14));
        rows.add(actionRow(Icons.Type.SEARCH, "SEARCH FLIGHTS", "Find flights by route and date",
                MINT, e -> showSearchFlightsDialog()));
        rows.add(Box.createVerticalStrut(14));
        rows.add(actionRow(Icons.Type.GLOBE, "ALL FLIGHTS", "Browse the full flight catalog",
                AMBER, e -> showAllFlightsDialog()));

        content.add(rows);
        content.add(Box.createVerticalStrut(26));

        GlowButton exitBtn = new GlowButton("Exit", ROSE, true);
        exitBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitBtn.addActionListener(e -> { bookingSystem.saveData(); System.exit(0); });
        content.add(exitBtn);

        GridBagConstraints gbc = new GridBagConstraints();
        bg.add(content, gbc);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(bg, BorderLayout.CENTER);
        return wrapper;
    }

    private static JPanel badgePanel(Icons.Type type, Color accent) {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 16, 16);
                g2.setColor(new Color(8, 12, 22));
                g2.fill(shape);
                g2.setColor(withAlpha(accent, 0.55f));
                g2.setStroke(new BasicStroke(1.6f));
                g2.draw(shape);
                g2.dispose();
            }
        };
        p.setOpaque(false);
        p.setPreferredSize(new Dimension(64, 64));
        p.setMaximumSize(new Dimension(64, 64));
        p.setLayout(new GridBagLayout());
        p.add(Icons.make(type, accent, 30));
        return p;
    }

    // ── Home action row (icon ▸ title/subtitle ▸ chevron, full width) ─
    private static JButton actionRow(Icons.Type type, String title, String subtitle, Color accent, ActionListener al) {
        JButton btn = new JButton() {
            private float hover = 0f;
            private Timer timer;
            {
                setOpaque(false);
                setContentAreaFilled(false);
                setBorderPainted(false);
                setFocusPainted(false);
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                timer = new Timer(15, e -> {
                    float target = getModel().isRollover() ? 1f : 0f;
                    hover += (target - hover) * 0.22f;
                    if (Math.abs(target - hover) < 0.01f) { hover = target; ((Timer)e.getSource()).stop(); }
                    repaint();
                });
                addChangeListener(e -> { if (!timer.isRunning()) timer.start(); });
            }
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 16, 16);
                g2.setColor(blend(BG_CARD, BG_CARD_HOVER, hover));
                g2.fill(shape);
                g2.setStroke(new BasicStroke(1.2f + hover * 0.8f));
                g2.setColor(withAlpha(accent, 0.28f + hover * 0.5f));
                g2.draw(shape);

                // accent bar on the leading edge, brightens on hover
                RoundRectangle2D bar = new RoundRectangle2D.Float(0, 10, 3.5f + hover * 1.5f, h - 20, 4, 4);
                g2.setColor(withAlpha(accent, 0.55f + hover * 0.45f));
                g2.fill(bar);

                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setLayout(new GridBagLayout());
        btn.setAlignmentX(Component.CENTER_ALIGNMENT);
        btn.setPreferredSize(new Dimension(620, 78));
        btn.setMaximumSize(new Dimension(620, 78));

        Icons.IconPanel icon = Icons.make(type, accent, 28);

        JPanel textBlock = new JPanel();
        textBlock.setOpaque(false);
        textBlock.setLayout(new BoxLayout(textBlock, BoxLayout.Y_AXIS));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_LABEL);
        titleLbl.setForeground(TEXT_PRIMARY);
        titleLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel subLbl = new JLabel(subtitle);
        subLbl.setFont(FONT_SMALL);
        subLbl.setForeground(TEXT_MUTED);
        subLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        textBlock.add(titleLbl);
        textBlock.add(Box.createVerticalStrut(3));
        textBlock.add(subLbl);

        Icons.IconPanel chevron = Icons.make(Icons.Type.CHEVRON_RIGHT, accent, 18);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridy = 0; gbc.anchor = GridBagConstraints.WEST;
        gbc.gridx = 0; gbc.insets = new Insets(0, 24, 0, 16);
        btn.add(icon, gbc);
        gbc.gridx = 1; gbc.weightx = 1; gbc.fill = GridBagConstraints.HORIZONTAL; gbc.insets = new Insets(0, 0, 0, 16);
        btn.add(textBlock, gbc);
        gbc.gridx = 2; gbc.weightx = 0; gbc.fill = GridBagConstraints.NONE; gbc.insets = new Insets(0, 0, 0, 22);
        btn.add(chevron, gbc);

        btn.addActionListener(al);
        return btn;
    }

    // ══════════════════════════════════════════════════════════
    //  LOGIN PANEL
    // ══════════════════════════════════════════════════════════
    private static JPanel buildLoginPanel() {
        BackgroundPanel bg = new BackgroundPanel();
        bg.setLayout(new GridBagLayout());

        GlassCard card = new GlassCard();
        card.setBorderColor(CYAN);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(440, 380));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8,10,8,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx=0; gbc.gridy=0;
        card.add(cardHeader(Icons.Type.KEY, "Sign In", CYAN), gbc);

        gbc.gridwidth = 1; gbc.insets = new Insets(10,10,4,10);
        gbc.gridy=1; gbc.gridx=0; card.add(fieldLabel("USERNAME"), gbc);
        gbc.gridy=2; gbc.gridx=0; gbc.gridwidth=2;
        JTextField userField = field();
        card.add(userField, gbc);

        gbc.gridwidth=1;
        gbc.gridy=3; gbc.gridx=0; card.add(fieldLabel("PASSWORD"), gbc);
        gbc.gridy=4; gbc.gridx=0; gbc.gridwidth=2;
        JPasswordField passField = passwordField();
        card.add(passField, gbc);

        gbc.gridy=5; gbc.gridx=0; gbc.gridwidth=2; gbc.insets=new Insets(22,10,4,10);
        GlowButton loginBtn = new GlowButton("Login", CYAN);
        card.add(loginBtn, gbc);

        gbc.gridy=6; gbc.insets=new Insets(6,10,4,10);
        LinkButton backBtn = new LinkButton("Back to Home", TEXT_MUTED);
        card.add(backBtn, gbc);

        ActionListener loginAction = e -> {
            String user = userField.getText().trim();
            String pass = new String(passField.getPassword());
            if (user.isEmpty() || pass.isEmpty()) { showError("Please enter username and password."); return; }
            boolean ok = bookingSystem.login(user, pass);
            if (ok) {
                User cu = bookingSystem.getCurrentUser();
                setStatus("Logged in as " + cu.getName() + " (" + cu.getRole() + ")");
                userField.setText(""); passField.setText("");
                switch (cu.getRole()) {
                    case "Customer":     showScreen("Customer"); break;
                    case "Agent":        showScreen("Agent");    break;
                    case "Administrator":showScreen("Admin");    break;
                }
            } else {
                showError("Invalid credentials or inactive account.");
            }
        };
        loginBtn.addActionListener(loginAction);
        passField.addActionListener(loginAction);
        backBtn.addActionListener(e -> showScreen("Welcome"));

        bg.add(card);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(bg, BorderLayout.CENTER);
        return wrapper;
    }

    private static JPanel cardHeader(Icons.Type type, String text, Color accent) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        Icons.IconPanel icon = Icons.make(type, accent, 30);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);
        icon.setMinimumSize(new Dimension(30, 30));
        icon.setMaximumSize(new Dimension(30, 30));
        JLabel l = new JLabel(text, SwingConstants.CENTER);
        l.setFont(FONT_HEAD);
        l.setForeground(TEXT_PRIMARY);
        l.setAlignmentX(Component.CENTER_ALIGNMENT);
        l.setBorder(new EmptyBorder(8,0,12,0));
        p.add(icon);
        p.add(l);
        return p;
    }

    // ══════════════════════════════════════════════════════════
    //  REGISTER PANEL
    // ══════════════════════════════════════════════════════════
    private static JPanel buildRegisterPanel() {
        BackgroundPanel bg = new BackgroundPanel();
        bg.setLayout(new GridBagLayout());

        GlassCard card = new GlassCard();
        card.setBorderColor(VIOLET);
        card.setLayout(new GridBagLayout());
        card.setPreferredSize(new Dimension(520, 600));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5,10,5,10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridwidth = 2;

        gbc.gridx=0; gbc.gridy=0;
        card.add(cardHeader(Icons.Type.USER_PLUS, "Create Account", VIOLET), gbc);

        gbc.gridwidth=1;
        String[] roles = {"Customer","Agent","Administrator"};
        JComboBox<String> roleBox = combo(roles);
        gbc.gridy=1; gbc.gridx=0; card.add(fieldLabel("ROLE"), gbc);
        gbc.gridx=1; card.add(roleBox, gbc);

        JTextField usernameF = field();
        gbc.gridy=2; gbc.gridx=0; card.add(fieldLabel("USERNAME"), gbc);
        gbc.gridx=1; card.add(usernameF, gbc);

        JPasswordField passF = passwordField();
        gbc.gridy=3; gbc.gridx=0; card.add(fieldLabel("PASSWORD"), gbc);
        gbc.gridx=1; card.add(passF, gbc);

        JTextField nameF = field();
        gbc.gridy=4; gbc.gridx=0; card.add(fieldLabel("FULL NAME"), gbc);
        gbc.gridx=1; card.add(nameF, gbc);

        JTextField emailF = field();
        gbc.gridy=5; gbc.gridx=0; card.add(fieldLabel("EMAIL"), gbc);
        gbc.gridx=1; card.add(emailF, gbc);

        JTextField contactF = field();
        gbc.gridy=6; gbc.gridx=0; card.add(fieldLabel("CONTACT"), gbc);
        gbc.gridx=1; card.add(contactF, gbc);

        JLabel extraLabel = fieldLabel("ADDRESS");
        JTextField extraF = field();
        gbc.gridy=7; gbc.gridx=0; card.add(extraLabel, gbc);
        gbc.gridx=1; card.add(extraF, gbc);

        JLabel extra2Label = fieldLabel("COMMISSION RATE");
        JTextField extra2F = field();
        extra2Label.setVisible(false); extra2F.setVisible(false);
        gbc.gridy=8; gbc.gridx=0; card.add(extra2Label, gbc);
        gbc.gridx=1; card.add(extra2F, gbc);

        roleBox.addActionListener(e -> {
            String r = (String) roleBox.getSelectedItem();
            extraLabel.setText("Customer".equals(r) ? "ADDRESS" : "Agent".equals(r) ? "DEPARTMENT" : "SECURITY LEVEL");
            extra2Label.setVisible("Agent".equals(r));
            extra2F.setVisible("Agent".equals(r));
        });

        gbc.gridy=9; gbc.gridx=0; gbc.gridwidth=2; gbc.insets=new Insets(18,10,4,10);
        GlowButton regBtn = new GlowButton("Create Account", VIOLET);
        card.add(regBtn, gbc);

        gbc.gridy=10; gbc.insets=new Insets(6,10,4,10);
        LinkButton backBtn = new LinkButton("Back to Home", TEXT_MUTED);
        card.add(backBtn, gbc);

        regBtn.addActionListener(e -> {
            String role    = (String) roleBox.getSelectedItem();
            String uname   = usernameF.getText().trim();
            String pass    = new String(passF.getPassword());
            String nm      = nameF.getText().trim();
            String em      = emailF.getText().trim();
            String ct      = contactF.getText().trim();
            String info1   = extraF.getText().trim();
            String info2   = extra2F.getText().trim();

            if (uname.isEmpty()||pass.isEmpty()||nm.isEmpty()||em.isEmpty()||ct.isEmpty()||info1.isEmpty()) {
                showError("All fields are required."); return;
            }
            if (!pass.matches("^(?=.*[A-Za-z])(?=.*\\d).{6,}$")) {
                showError("Password must be 6+ chars with letters and numbers."); return;
            }
            if ("Agent".equals(role) && info2.isEmpty()) {
                showError("Commission rate is required for Agents."); return;
            }
            boolean ok = bookingSystem.registerUser(uname, pass, nm, em, ct, role, info1, info2);
            if (ok) {
                showInfo("Account created successfully! You can now log in.");
                usernameF.setText(""); passF.setText(""); nameF.setText("");
                emailF.setText(""); contactF.setText(""); extraF.setText(""); extra2F.setText("");
                showScreen("Login");
            } else {
                showError("Registration failed. Username may already exist.");
            }
        });
        backBtn.addActionListener(e -> showScreen("Welcome"));

        bg.add(card);
        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(bg, BorderLayout.CENTER);
        return wrapper;
    }

    // ══════════════════════════════════════════════════════════
    //  DASHBOARDS
    // ══════════════════════════════════════════════════════════
    private static JPanel buildCustomerPanel() {
        return buildDashboard("Customer", CYAN, new Object[][]{
            {Icons.Type.SEARCH,  "Search Flights",   "searchFlights",  MINT},
            {Icons.Type.GLOBE,   "All Flights",       "allFlights",     CYAN},
            {Icons.Type.TICKET,  "Book a Flight",     "bookFlight",     VIOLET},
            {Icons.Type.FOLDER,  "My Bookings",       "myBookings",     AMBER},
            {Icons.Type.CANCEL,  "Cancel Booking",    "cancelBooking",  ROSE},
            {Icons.Type.CARD,    "Make Payment",       "payment",        MINT},
            {Icons.Type.RECEIPT, "E-Ticket",           "eTicket",        GOLD},
            {Icons.Type.LOGOUT,  "Logout",             "logout",         TEXT_MUTED},
        });
    }

    private static JPanel buildAgentPanel() {
        return buildDashboard("Agent", VIOLET, new Object[][]{
            {Icons.Type.SEARCH,  "Search Flights",       "searchFlights",     MINT},
            {Icons.Type.GLOBE,   "All Flights",           "allFlights",        CYAN},
            {Icons.Type.TICKET,  "Book for Customer",     "bookForCustomer",   VIOLET},
            {Icons.Type.FOLDER,  "Customer Bookings",     "customerBookings",  AMBER},
            {Icons.Type.CANCEL,  "Cancel Booking",        "cancelBooking",     ROSE},
            {Icons.Type.CARD,    "Process Payment",       "payment",           MINT},
            {Icons.Type.RECEIPT, "Generate Itinerary",    "eTicket",           GOLD},
            {Icons.Type.PLANE,   "Add Flight",            "addFlight",         CYAN},
            {Icons.Type.CLOCK,   "Update Schedule",       "updateSchedule",    VIOLET},
            {Icons.Type.CHART,   "Generate Report",       "report",            AMBER},
            {Icons.Type.LOGOUT,  "Logout",                "logout",            TEXT_MUTED},
        });
    }

    private static JPanel buildAdminPanel() {
        return buildDashboard("Admin", AMBER, new Object[][]{
            {Icons.Type.GLOBE,    "All Flights",      "allFlights",     CYAN},
            {Icons.Type.PLANE,    "Add Flight",        "addFlight",      VIOLET},
            {Icons.Type.CLOCK,    "Update Schedule",   "updateSchedule", MINT},
            {Icons.Type.USERS,    "All Users",          "allUsers",       CYAN},
            {Icons.Type.USER_PLUS,"Add User",           "addUser",        VIOLET},
            {Icons.Type.FOLDER,   "All Bookings",       "allBookings",    AMBER},
            {Icons.Type.CANCEL,   "Cancel Booking",     "cancelBooking",  ROSE},
            {Icons.Type.SHIELD,   "Manage Access",      "manageAccess",   GOLD},
            {Icons.Type.CHART,    "Generate Report",     "report",        MINT},
            {Icons.Type.LOGOUT,   "Logout",              "logout",        TEXT_MUTED},
        });
    }

    private static JPanel buildDashboard(String role, Color roleAccent, Object[][] items) {
        BackgroundPanel bg = new BackgroundPanel();
        bg.setLayout(new BorderLayout());

        JPanel nav = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(10, 14, 26));
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.setColor(withAlpha(roleAccent, 0.5f));
                g2.fillRect(0, getHeight()-2, getWidth(), 2);
                g2.dispose();
            }
        };
        nav.setOpaque(false);
        nav.setBorder(new EmptyBorder(14,24,14,24));

        JPanel navLeft = new JPanel();
        navLeft.setOpaque(false);
        navLeft.setLayout(new BoxLayout(navLeft, BoxLayout.X_AXIS));
        navLeft.add(Icons.make(Icons.Type.PLANE, CYAN, 26));
        navLeft.add(Box.createHorizontalStrut(10));
        JLabel navTitle = new JLabel("SkyBook");
        navTitle.setFont(new Font("Segoe UI", Font.BOLD, 19));
        navTitle.setForeground(TEXT_PRIMARY);
        navLeft.add(navTitle);
        nav.add(navLeft, BorderLayout.WEST);

        JLabel userLabel = new JLabel(role.toUpperCase() + " DASHBOARD");
        userLabel.setFont(FONT_LABEL);
        userLabel.setForeground(roleAccent);
        nav.add(userLabel, BorderLayout.EAST);

        int cols = 4;
        JPanel grid = new JPanel(new GridLayout(0, cols, 16, 16));
        grid.setOpaque(false);
        grid.setBorder(new EmptyBorder(30, 30, 30, 30));

        for (Object[] item : items) {
            Icons.Type icon  = (Icons.Type) item[0];
            String label      = (String) item[1];
            String action     = (String) item[2];
            Color accent      = (Color) item[3];
            grid.add(new ActionCard(icon, label, accent, e -> handleAction(action)));
        }

        JScrollPane sp = new JScrollPane(grid);
        sp.setBorder(null);
        sp.setOpaque(false);
        sp.getViewport().setOpaque(false);
        sp.getVerticalScrollBar().setUnitIncrement(16);

        bg.add(nav, BorderLayout.NORTH);
        bg.add(sp, BorderLayout.CENTER);

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.add(bg, BorderLayout.CENTER);
        return wrapper;
    }

    private static void handleAction(String action) {
        switch (action) {
            case "searchFlights":    showSearchFlightsDialog();         break;
            case "allFlights":       showAllFlightsDialog();            break;
            case "bookFlight":       showBookFlightDialog();            break;
            case "bookForCustomer":  showBookForCustomerDialog();       break;
            case "myBookings":       showMyBookingsDialog();            break;
            case "customerBookings": showCustomerBookingsDialog();      break;
            case "allBookings":      showAllBookingsDialog();           break;
            case "cancelBooking":    showCancelBookingDialog();         break;
            case "payment":          showPaymentDialog();               break;
            case "eTicket":          showETicketDialog();               break;
            case "addFlight":        showAddFlightDialog();             break;
            case "updateSchedule":   showUpdateScheduleDialog();        break;
            case "allUsers":         showAllUsersDialog();              break;
            case "addUser":          showAddUserDialog();               break;
            case "manageAccess":     showManageAccessDialog();          break;
            case "report":           showReportDialog();                break;
            case "logout":
                bookingSystem.logout();
                setStatus("Logged out");
                showScreen("Welcome");
                break;
        }
    }

    // ══════════════════════════════════════════════════════════
    //  DIALOG INFRASTRUCTURE — themed modal replacing JOptionPane
    // ══════════════════════════════════════════════════════════

    /** Builds a themed modal dialog shell: dark background, glow border, icon header. */
    private static JDialog buildDialogShell(String title, Icons.Type icon, Color accent) {
        JDialog dialog = new JDialog(mainFrame, true);
        dialog.setUndecorated(true);
        dialog.setLayout(new BorderLayout());

        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                int w = getWidth(), h = getHeight();
                RoundRectangle2D shape = new RoundRectangle2D.Float(1, 1, w - 2, h - 2, 18, 18);
                g2.setColor(BG_CARD);
                g2.fill(shape);
                g2.setStroke(new BasicStroke(1.5f));
                g2.setColor(withAlpha(accent, 0.55f));
                g2.draw(shape);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setBorder(new EmptyBorder(4,4,4,4));

        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(20, 26, 12, 26));
        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.X_AXIS));
        left.add(Icons.make(icon, accent, 24));
        left.add(Box.createHorizontalStrut(10));
        JLabel titleLbl = new JLabel(title);
        titleLbl.setFont(FONT_HEAD);
        titleLbl.setForeground(TEXT_PRIMARY);
        left.add(titleLbl);
        header.add(left, BorderLayout.WEST);

        JButton closeBtn = new JButton("\u2715");
        closeBtn.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        closeBtn.setForeground(TEXT_MUTED);
        closeBtn.setBorderPainted(false);
        closeBtn.setContentAreaFilled(false);
        closeBtn.setFocusPainted(false);
        closeBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        closeBtn.addActionListener(e -> dialog.dispose());
        header.add(closeBtn, BorderLayout.EAST);

        root.add(header, BorderLayout.NORTH);
        dialog.add(root);
        dialog.getRootPane().putClientProperty("shellPanel", root);
        Point[] dragStart = {null};
        header.addMouseListener(new MouseAdapter() {
            @Override public void mousePressed(MouseEvent e) { dragStart[0] = e.getPoint(); }
        });
        header.addMouseMotionListener(new MouseMotionAdapter() {
            @Override public void mouseDragged(MouseEvent e) {
                if (dragStart[0] == null) return;
                Point p = dialog.getLocation();
                dialog.setLocation(p.x + e.getX() - dragStart[0].x, p.y + e.getY() - dragStart[0].y);
            }
        });
        return dialog;
    }

    private static JPanel dialogBody() {
        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setBorder(new EmptyBorder(4, 26, 22, 26));
        return body;
    }

    private static JPanel dialogFormGrid(int rows) {
        JPanel form = new JPanel(new GridLayout(rows, 1, 0, 12));
        form.setOpaque(false);
        return form;
    }

    private static JPanel labeledRow(String label, JComponent field) {
        JPanel row = new JPanel();
        row.setOpaque(false);
        row.setLayout(new BoxLayout(row, BoxLayout.Y_AXIS));
        JLabel l = fieldLabel(label.toUpperCase());
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.add(l);
        row.add(Box.createVerticalStrut(5));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, field.getPreferredSize().height));
        row.add(field);
        return row;
    }

    private static JPanel dialogFooter(JDialog dialog, String confirmText, Color accent, Runnable onConfirm) {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 26, 22, 26));
        GlowButton cancel = new GlowButton("Cancel", TEXT_FAINT, true);
        cancel.addActionListener(e -> dialog.dispose());
        GlowButton confirm = new GlowButton(confirmText, accent);
        confirm.addActionListener(e -> onConfirm.run());
        footer.add(cancel);
        footer.add(confirm);
        return footer;
    }

    private static void showFormDialog(String title, Icons.Type icon, Color accent,
                                        JPanel form, String confirmText, Runnable onConfirm) {
        JDialog dialog = buildDialogShell(title, icon, accent);
        JPanel body = dialogBody();
        body.setLayout(new BorderLayout());
        body.add(form, BorderLayout.CENTER);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(body, BorderLayout.CENTER);
        center.add(dialogFooter(dialog, confirmText, accent, onConfirm), BorderLayout.SOUTH);

        ((JPanel) dialog.getRootPane().getClientProperty("shellPanel")).add(center, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private static void showTableDialog(String title, Icons.Type icon, Color accent, String[] columns, Object[][] data) {
        JDialog dialog = buildDialogShell(title, icon, accent);
        JTable table = new JTable(data, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        styleTable(table);
        table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Size each column to fit its widest content (header or cell) so nothing truncates.
        FontMetrics headFm = table.getTableHeader().getFontMetrics(FONT_LABEL);
        FontMetrics cellFm = table.getFontMetrics(FONT_BODY);
        for (int col = 0; col < columns.length; col++) {
            int width = headFm.stringWidth(columns[col]) + 36;
            for (int row = 0; row < data.length; row++) {
                Object val = data[row][col];
                int w = cellFm.stringWidth(val == null ? "" : val.toString()) + 28;
                if (w > width) width = w;
            }
            table.getColumnModel().getColumn(col).setPreferredWidth(Math.min(width, 320));
        }
        int totalWidth = 0;
        for (int col = 0; col < columns.length; col++) totalWidth += table.getColumnModel().getColumn(col).getPreferredWidth();

        JScrollPane sp = new JScrollPane(table);
        int dialogW = Math.max(560, Math.min(totalWidth + 30, 1080));
        sp.setPreferredSize(new Dimension(dialogW, 420));
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(new EmptyBorder(0,0,0,0));

        JPanel body = dialogBody();
        body.setLayout(new BorderLayout());
        body.add(sp, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 26, 20, 26));
        GlowButton closeBtn = new GlowButton("Close", accent, true);
        closeBtn.addActionListener(e -> dialog.dispose());
        footer.add(closeBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(body, BorderLayout.CENTER);
        center.add(footer, BorderLayout.SOUTH);

        ((JPanel) dialog.getRootPane().getClientProperty("shellPanel")).add(center, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private static void showScrollableTextDialog(String title, Icons.Type icon, Color accent, String text) {
        JDialog dialog = buildDialogShell(title, icon, accent);
        JTextArea area = new JTextArea(text);
        area.setEditable(false);
        area.setFont(new Font("Consolas", Font.PLAIN, 13));
        area.setBackground(BG_CARD);
        area.setForeground(TEXT_PRIMARY);
        area.setBorder(new EmptyBorder(14,14,14,14));

        JScrollPane sp = new JScrollPane(area);
        sp.setPreferredSize(new Dimension(620, 440));
        sp.getViewport().setBackground(BG_CARD);
        sp.setBorder(new LineBorder(BORDER_SOFT, 1));

        JPanel body = dialogBody();
        body.setLayout(new BorderLayout());
        body.add(sp, BorderLayout.CENTER);

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 26, 20, 26));
        GlowButton closeBtn = new GlowButton("Close", accent, true);
        closeBtn.addActionListener(e -> dialog.dispose());
        footer.add(closeBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(body, BorderLayout.CENTER);
        center.add(footer, BorderLayout.SOUTH);

        ((JPanel) dialog.getRootPane().getClientProperty("shellPanel")).add(center, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    /** Simple themed message dialog (replaces showError / showInfo JOptionPanes). */
    private static void showMessage(String title, Icons.Type icon, Color accent, String message) {
        JDialog dialog = buildDialogShell(title, icon, accent);
        JPanel body = dialogBody();
        body.setLayout(new BorderLayout());
        JTextArea text = new JTextArea(message);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setFont(FONT_BODY);
        text.setForeground(TEXT_PRIMARY);
        text.setBorder(new EmptyBorder(4,0,4,0));
        text.setFocusable(false);
        body.add(text, BorderLayout.CENTER);
        body.setPreferredSize(new Dimension(380, Math.max(60, body.getPreferredSize().height)));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 26, 20, 26));
        GlowButton okBtn = new GlowButton("OK", accent);
        okBtn.addActionListener(e -> dialog.dispose());
        footer.add(okBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(body, BorderLayout.CENTER);
        center.add(footer, BorderLayout.SOUTH);

        ((JPanel) dialog.getRootPane().getClientProperty("shellPanel")).add(center, BorderLayout.CENTER);
        dialog.setMinimumSize(new Dimension(420, 0));
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
    }

    private static void showError(String msg) { showMessage("Error", Icons.Type.ALERT, ROSE, msg); }
    private static void showInfo(String msg)  { showMessage("Success", Icons.Type.CHECK, MINT, msg); }

    /** Returns true if user confirmed. */
    private static boolean showConfirm(String title, String message, Color accent) {
        final boolean[] result = {false};
        JDialog dialog = buildDialogShell(title, Icons.Type.ALERT, accent);
        JPanel body = dialogBody();
        body.setLayout(new BorderLayout());
        JTextArea text = new JTextArea(message);
        text.setLineWrap(true);
        text.setWrapStyleWord(true);
        text.setEditable(false);
        text.setOpaque(false);
        text.setFont(FONT_BODY);
        text.setForeground(TEXT_PRIMARY);
        text.setFocusable(false);
        body.add(text, BorderLayout.CENTER);
        body.setPreferredSize(new Dimension(380, Math.max(60, body.getPreferredSize().height)));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 26, 20, 26));
        GlowButton noBtn = new GlowButton("No", TEXT_FAINT, true);
        noBtn.addActionListener(e -> { result[0] = false; dialog.dispose(); });
        GlowButton yesBtn = new GlowButton("Yes", accent);
        yesBtn.addActionListener(e -> { result[0] = true; dialog.dispose(); });
        footer.add(noBtn);
        footer.add(yesBtn);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(body, BorderLayout.CENTER);
        center.add(footer, BorderLayout.SOUTH);

        ((JPanel) dialog.getRootPane().getClientProperty("shellPanel")).add(center, BorderLayout.CENTER);
        dialog.setMinimumSize(new Dimension(420, 0));
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setVisible(true);
        return result[0];
    }

    /** Single-field input dialog (replaces JOptionPane.showInputDialog). */
    private static String showInputDialog(String title, Icons.Type icon, Color accent, String label) {
        final String[] result = {null};
        JDialog dialog = buildDialogShell(title, icon, accent);
        JPanel body = dialogBody();
        body.setLayout(new BorderLayout());
        JTextField input = field();
        JPanel row = labeledRow(label, input);
        body.add(row, BorderLayout.CENTER);
        body.setPreferredSize(new Dimension(380, body.getPreferredSize().height));

        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(6, 26, 20, 26));
        GlowButton cancel = new GlowButton("Cancel", TEXT_FAINT, true);
        cancel.addActionListener(e -> dialog.dispose());
        GlowButton ok = new GlowButton("OK", accent);
        Runnable submit = () -> { result[0] = input.getText().trim(); dialog.dispose(); };
        ok.addActionListener(e -> submit.run());
        input.addActionListener(e -> submit.run());
        footer.add(cancel);
        footer.add(ok);

        JPanel center = new JPanel(new BorderLayout());
        center.setOpaque(false);
        center.add(body, BorderLayout.CENTER);
        center.add(footer, BorderLayout.SOUTH);

        ((JPanel) dialog.getRootPane().getClientProperty("shellPanel")).add(center, BorderLayout.CENTER);
        dialog.setMinimumSize(new Dimension(420, 0));
        dialog.pack();
        dialog.setLocationRelativeTo(mainFrame);
        SwingUtilities.invokeLater(input::requestFocusInWindow);
        dialog.setVisible(true);
        return result[0];
    }

    // ══════════════════════════════════════════════════════════
    //  FLIGHT / BOOKING DIALOGS
    // ══════════════════════════════════════════════════════════

    private static String formatSeatInfo(Flight f, int classIndex) {
        if (f.seatClasses == null || f.seatClasses.size() <= classIndex) return "\u2013";
        String cls = f.seatClasses.get(classIndex);
        int avail = f.getAvailableSeats(cls);
        double price = f.getPrice(cls);
        return cls + ": " + avail + " / $" + String.format("%.0f", price);
    }

    private static void showAllFlightsDialog() {
        ArrayList<Flight> flights = bookingSystem.getAllFlights();
        String[] cols = {"ID","Flight No","Airline","Origin","Destination","Departure","Arrival","Type","Economy","Business"};
        Object[][] data = new Object[flights.size()][cols.length];
        for (int i = 0; i < flights.size(); i++) {
            Flight f = flights.get(i);
            data[i] = new Object[]{
                f.getFlightId(), f.getFlightNumberStr(), f.getAirline(),
                f.getOrigin(), f.getDestination(), f.getDepartureTime(), f.getArrivalTime(),
                f.getFlightType(),
                formatSeatInfo(f, 0),
                formatSeatInfo(f, 1)
            };
        }
        showTableDialog("All Flights", Icons.Type.GLOBE, CYAN, cols, data);
    }

    private static void showSearchFlightsDialog() {
        JTextField originF = field();
        JTextField destF   = field();
        JTextField dateF   = field();
        String[]   types   = {"Any","Domestic","International"};
        JComboBox<String> typeBox = combo(types);

        JPanel form = dialogFormGrid(4);
        form.add(labeledRow("Origin (blank = any)", originF));
        form.add(labeledRow("Destination (blank = any)", destF));
        form.add(labeledRow("Date YYYY-MM-DD (blank = any)", dateF));
        form.add(labeledRow("Flight Type", typeBox));

        showFormDialog("Search Flights", Icons.Type.SEARCH, MINT, form, "Search", () -> {
            String type = ((String) typeBox.getSelectedItem()).equals("Any") ? "" : (String) typeBox.getSelectedItem();
            ArrayList<Flight> results = bookingSystem.searchFlights(
                originF.getText().trim(), destF.getText().trim(), dateF.getText().trim(), type);

            String[] cols = {"ID","Flight No","Airline","Origin","Dest","Departure","Type","Avail Seats / Price"};
            Object[][] data = new Object[results.size()][cols.length];
            for (int i = 0; i < results.size(); i++) {
                Flight f = results.get(i);
                StringBuilder seats = new StringBuilder();
                if (f.seatClasses != null) {
                    for (int j = 0; j < f.seatClasses.size(); j++) {
                        String cls = f.seatClasses.get(j);
                        seats.append(cls).append(":").append(f.getAvailableSeats(cls))
                             .append("/$").append(String.format("%.0f",f.getPrice(cls)));
                        if (j < f.seatClasses.size()-1) seats.append("  ");
                    }
                }
                data[i] = new Object[]{f.getFlightId(), f.getFlightNumberStr(), f.getAirline(),
                        f.getOrigin(), f.getDestination(), f.getDepartureTime(), f.getFlightType(), seats.toString()};
            }
            if (results.isEmpty()) showInfo("No flights matched your search.");
            else showTableDialog("Search Results (" + results.size() + " found)", Icons.Type.SEARCH, MINT, cols, data);
        });
    }

    private static void showBookFlightDialog() {
        User cu = bookingSystem.getCurrentUser();
        if (!(cu instanceof Customer)) { showError("Only customers can book flights here."); return; }

        JTextField flightIdF  = field();
        JTextField passNameF  = field();
        JTextField passportF  = field();
        JTextField dobF       = field();
        JTextField requestsF  = field();
        JTextField seatClassF = field();

        JPanel form = dialogFormGrid(6);
        form.add(labeledRow("Flight ID", flightIdF));
        form.add(labeledRow("Passenger Name", passNameF));
        form.add(labeledRow("Passport Number", passportF));
        form.add(labeledRow("Date of Birth (YYYY-MM-DD)", dobF));
        form.add(labeledRow("Special Requests (optional)", requestsF));
        form.add(labeledRow("Seat Class (e.g. Economy)", seatClassF));

        showFormDialog("Book a Flight", Icons.Type.TICKET, VIOLET, form, "Book", () -> {
            try {
                int fid = Integer.parseInt(flightIdF.getText().trim());
                String[] pDetails = {passNameF.getText().trim(), passportF.getText().trim(),
                        dobF.getText().trim(), requestsF.getText().trim()};
                boolean ok = bookingSystem.bookFlight(fid, pDetails, seatClassF.getText().trim());
                if (ok) showInfo("Booking created successfully! Check 'My Bookings' for your booking ID.");
                else    showError("Booking failed. Check flight ID, seat class, and availability.");
            } catch (NumberFormatException ex) {
                showError("Invalid Flight ID. Please enter a number.");
            }
        });
    }

    private static void showBookForCustomerDialog() {
        JTextField custIdF   = field();
        JTextField flightIdF = field();
        JTextField passNameF = field();
        JTextField passportF = field();
        JTextField dobF      = field();
        JTextField reqF      = field();
        JTextField seatF     = field();

        JPanel form = dialogFormGrid(7);
        form.add(labeledRow("Customer ID", custIdF));
        form.add(labeledRow("Flight ID", flightIdF));
        form.add(labeledRow("Passenger Name", passNameF));
        form.add(labeledRow("Passport Number", passportF));
        form.add(labeledRow("Date of Birth", dobF));
        form.add(labeledRow("Special Requests", reqF));
        form.add(labeledRow("Seat Class", seatF));

        showFormDialog("Book Flight for Customer", Icons.Type.TICKET, VIOLET, form, "Book", () -> {
            try {
                int cid = Integer.parseInt(custIdF.getText().trim());
                int fid = Integer.parseInt(flightIdF.getText().trim());
                String[] pd = {passNameF.getText().trim(), passportF.getText().trim(),
                               dobF.getText().trim(), reqF.getText().trim()};
                boolean ok = bookingSystem.bookFlightForCustomer(cid, fid, pd, seatF.getText().trim());
                if (ok) showInfo("Booking created successfully for customer.");
                else    showError("Booking failed. Verify customer ID, flight ID, and seat class.");
            } catch (NumberFormatException ex) {
                showError("Please enter valid numeric IDs.");
            }
        });
    }

    private static void showMyBookingsDialog() {
        User cu = bookingSystem.getCurrentUser();
        if (cu == null) return;
        ArrayList<Booking> bookings = bookingSystem.getCustomerBookings(cu.getUserId());
        showBookingsTable("My Bookings", bookings);
    }

    private static void showCustomerBookingsDialog() {
        String input = showInputDialog("Customer Bookings", Icons.Type.FOLDER, AMBER, "Customer ID");
        if (input == null || input.isEmpty()) return;
        try {
            int cid = Integer.parseInt(input.trim());
            ArrayList<Booking> bookings = bookingSystem.getCustomerBookings(cid);
            showBookingsTable("Bookings for Customer #" + cid, bookings);
        } catch (NumberFormatException ex) { showError("Invalid Customer ID."); }
    }

    private static void showAllBookingsDialog() {
        ArrayList<Booking> bookings = bookingSystem.getAllBookings();
        showBookingsTable("All Bookings", bookings);
    }

    private static void showBookingsTable(String title, ArrayList<Booking> bookings) {
        String[] cols = {"ID","Customer ID","Flight ID","Passenger","Seat","Status","Payment","Price"};
        Object[][] data = new Object[bookings.size()][cols.length];
        for (int i = 0; i < bookings.size(); i++) {
            Booking b = bookings.get(i);
            Passenger p = b.getPassenger();
            data[i] = new Object[]{
                b.getBookingId(), b.getCustomerId(), b.getFlightId(),
                p != null ? p.getName() : "ID:" + b.getPassengerId(),
                b.getSeatSelection(), b.getStatus(), b.getPaymentStatus(),
                String.format("$%.2f", b.getTotalPrice())
            };
        }
        if (bookings.isEmpty()) showInfo("No bookings found.");
        else showTableDialog(title, Icons.Type.FOLDER, AMBER, cols, data);
    }

    private static void showCancelBookingDialog() {
        String input = showInputDialog("Cancel Booking", Icons.Type.CANCEL, ROSE, "Booking ID to cancel");
        if (input == null || input.isEmpty()) return;
        try {
            int bid = Integer.parseInt(input.trim());
            boolean confirmed = showConfirm("Confirm Cancellation",
                "Are you sure you want to cancel booking #" + bid + "?", ROSE);
            if (confirmed) {
                boolean ok = bookingSystem.cancelBooking(bid);
                if (ok) showInfo("Booking #" + bid + " cancelled.");
                else    showError("Failed to cancel. Check booking ID and authorization.");
            }
        } catch (NumberFormatException ex) { showError("Invalid Booking ID."); }
    }

    private static void showPaymentDialog() {
        JTextField bookingIdF = field();
        String[] methods = {"Credit Card","Bank Transfer"};
        JComboBox<String> methodBox = combo(methods);
        JTextField amountF = field();

        JPanel form = dialogFormGrid(3);
        form.add(labeledRow("Booking ID", bookingIdF));
        form.add(labeledRow("Payment Method", methodBox));
        form.add(labeledRow("Amount ($)", amountF));

        showFormDialog("Make Payment", Icons.Type.CARD, MINT, form, "Pay", () -> {
            try {
                int bid    = Integer.parseInt(bookingIdF.getText().trim());
                double amt = Double.parseDouble(amountF.getText().trim());
                String method = (String) methodBox.getSelectedItem();
                boolean ok = bookingSystem.processPayment(bid, method, amt);
                if (ok) showInfo("Payment processed! Booking confirmed.");
                else    showError("Payment failed or insufficient amount.");
            } catch (NumberFormatException ex) { showError("Invalid booking ID or amount."); }
        });
    }

    private static void showETicketDialog() {
        String input = showInputDialog("E-Ticket", Icons.Type.RECEIPT, GOLD, "Booking ID for itinerary/e-ticket");
        if (input == null || input.isEmpty()) return;
        try {
            int bid = Integer.parseInt(input.trim());
            String itinerary = bookingSystem.generateItinerary(bid);
            if (itinerary == null) { showError("Could not generate itinerary. Check booking ID and authorization."); return; }
            showScrollableTextDialog("Itinerary / E-Ticket", Icons.Type.RECEIPT, GOLD, itinerary);
        } catch (NumberFormatException ex) { showError("Invalid Booking ID."); }
    }

    private static void showAddFlightDialog() {
        JTextField flightNoF  = field();
        JTextField airlineF   = field();
        JTextField originF    = field();
        JTextField destF      = field();
        JTextField depF       = field();
        JTextField arrF       = field();
        String[] types = {"Domestic","International"};
        JComboBox<String> typeBox = combo(types);
        JTextField class1F    = field(); class1F.setText("Economy");
        JTextField seats1F    = field();
        JTextField price1F    = field();
        JTextField class2F    = field(); class2F.setText("Business");
        JTextField seats2F    = field();
        JTextField price2F    = field();

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridLayout(0, 2, 14, 12));
        form.add(labeledRow("Flight Number", flightNoF));
        form.add(labeledRow("Airline", airlineF));
        form.add(labeledRow("Origin", originF));
        form.add(labeledRow("Destination", destF));
        form.add(labeledRow("Departure (YYYY-MM-DD HH:MM)", depF));
        form.add(labeledRow("Arrival (YYYY-MM-DD HH:MM)", arrF));
        form.add(labeledRow("Flight Type", typeBox));
        form.add(labeledRow("Class 1 Name", class1F));
        form.add(labeledRow("Class 1 Total Seats", seats1F));
        form.add(labeledRow("Class 1 Price ($)", price1F));
        form.add(labeledRow("Class 2 Name (optional)", class2F));
        form.add(labeledRow("Class 2 Total Seats", seats2F));
        form.add(labeledRow("Class 2 Price ($)", price2F));

        JScrollPane scrollForm = new JScrollPane(form);
        scrollForm.setBorder(null);
        scrollForm.setOpaque(false);
        scrollForm.getViewport().setOpaque(false);
        scrollForm.setPreferredSize(new Dimension(620, 420));

        JPanel wrapForm = new JPanel(new BorderLayout());
        wrapForm.setOpaque(false);
        wrapForm.add(scrollForm, BorderLayout.CENTER);

        showFormDialog("Add New Flight", Icons.Type.PLANE, CYAN, wrapForm, "Add Flight", () -> {
            try {
                ArrayList<String> classes = new ArrayList<>();
                ArrayList<Integer> seats  = new ArrayList<>();
                ArrayList<Double> prices  = new ArrayList<>();

                classes.add(class1F.getText().trim());
                seats.add(Integer.parseInt(seats1F.getText().trim()));
                prices.add(Double.parseDouble(price1F.getText().trim()));

                if (!class2F.getText().trim().isEmpty() && !seats2F.getText().trim().isEmpty()) {
                    classes.add(class2F.getText().trim());
                    seats.add(Integer.parseInt(seats2F.getText().trim()));
                    prices.add(Double.parseDouble(price2F.getText().trim()));
                }

                String[] scArr = classes.toArray(new String[0]);
                int[] stArr = seats.stream().mapToInt(i->i).toArray();
                double[] prArr = prices.stream().mapToDouble(d->d).toArray();
                String type = (String) typeBox.getSelectedItem();

                Flight f = bookingSystem.addFlight(flightNoF.getText().trim(), airlineF.getText().trim(),
                        originF.getText().trim(), destF.getText().trim(),
                        depF.getText().trim(), arrF.getText().trim(),
                        type, scArr, stArr, prArr);
                if (f != null) showInfo("Flight " + f.getFlightNumberStr() + " added (ID: " + f.getFlightId() + ").");
                else            showError("Failed to add flight. Flight number may already exist.");
            } catch (NumberFormatException ex) {
                showError("Invalid seats or price. Please enter valid numbers.");
            }
        });
    }

    private static void showUpdateScheduleDialog() {
        JTextField flightNoF = field();
        JTextField newDepF   = field();
        JTextField newArrF   = field();

        JPanel form = dialogFormGrid(3);
        form.add(labeledRow("Flight Number", flightNoF));
        form.add(labeledRow("New Departure (blank = keep)", newDepF));
        form.add(labeledRow("New Arrival (blank = keep)", newArrF));

        showFormDialog("Update Flight Schedule", Icons.Type.CLOCK, VIOLET, form, "Update", () -> {
            boolean ok = bookingSystem.updateFlightSchedule(
                flightNoF.getText().trim(), newDepF.getText().trim(), newArrF.getText().trim());
            if (ok) showInfo("Schedule updated.");
            else    showError("Failed to update. Check flight number.");
        });
    }

    private static void showAllUsersDialog() {
        ArrayList<User> users = bookingSystem.getAllUsers();
        String[] cols = {"ID","Username","Name","Email","Contact","Role","Status"};
        Object[][] data = new Object[users.size()][cols.length];
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);
            data[i] = new Object[]{u.getUserId(), u.getUsername(), u.getName(),
                    u.getEmail(), u.getContactInfo(), u.getRole(),
                    u.isActive() ? "Active" : "Inactive"};
        }
        showTableDialog("All Users", Icons.Type.USERS, CYAN, cols, data);
    }

    private static void showAddUserDialog() {
        String[] roles = {"Customer","Agent","Administrator"};
        JComboBox<String> roleBox = combo(roles);
        JTextField usernameF = field();
        JTextField passF2    = field();
        JTextField nameF     = field();
        JTextField emailF    = field();
        JTextField contactF  = field();
        JTextField info1F    = field();
        JTextField info2F    = field();

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new GridLayout(0, 2, 14, 12));
        form.add(labeledRow("Role", roleBox));
        form.add(labeledRow("Username", usernameF));
        form.add(labeledRow("Password", passF2));
        form.add(labeledRow("Full Name", nameF));
        form.add(labeledRow("Email", emailF));
        form.add(labeledRow("Contact", contactF));
        form.add(labeledRow("Address/Dept/Security Level", info1F));
        form.add(labeledRow("Commission Rate (Agent only)", info2F));

        showFormDialog("Add New User", Icons.Type.USER_PLUS, VIOLET, form, "Create", () -> {
            boolean ok = bookingSystem.adminCreateUser(
                usernameF.getText().trim(), passF2.getText().trim(), nameF.getText().trim(),
                emailF.getText().trim(), contactF.getText().trim(),
                (String)roleBox.getSelectedItem(), info1F.getText().trim(), info2F.getText().trim());
            if (ok) showInfo("User created successfully.");
            else    showError("Failed to create user.");
        });
    }

    private static void showManageAccessDialog() {
        JTextField usernameF = field();
        String[] actions = {"Enable User","Disable User","Change Role"};
        JComboBox<String> actionBox = combo(actions);
        String[] newRoles = {"Customer","Agent","Administrator"};
        JComboBox<String> newRoleBox = combo(newRoles);

        JPanel form = dialogFormGrid(3);
        form.add(labeledRow("Username", usernameF));
        form.add(labeledRow("Action", actionBox));
        form.add(labeledRow("New Role (if changing role)", newRoleBox));

        showFormDialog("Manage User Access", Icons.Type.SHIELD, GOLD, form, "Apply", () -> {
            String uname = usernameF.getText().trim();
            String action = (String)actionBox.getSelectedItem();

            if ("Enable User".equals(action)) {
                bookingSystem.enableUser(uname, true);
                showInfo("User '" + uname + "' enabled.");
            } else if ("Disable User".equals(action)) {
                bookingSystem.enableUser(uname, false);
                showInfo("User '" + uname + "' disabled.");
            } else {
                boolean ok = bookingSystem.manageUserAccess(uname, (String)newRoleBox.getSelectedItem());
                if (ok) showInfo("Role updated.");
                else    showError("Role change failed.");
            }
        });
    }

    private static void showReportDialog() {
        String report = bookingSystem.getReportString();
        showScrollableTextDialog("System Report", Icons.Type.CHART, MINT, report);
    }
}

