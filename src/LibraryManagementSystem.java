import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

/**
 * CSC2104 Object Oriented Programming - Mini Project
 * Manipal University Jaipur
 *
 * MAIN DESKTOP GUI APPLICATION — PREMIUM DARK THEME
 *
 * MODULE DEMONSTRATIONS IN THIS CLASS:
 * - Module 1: Modularity and separation of concerns - GUI delegates all business rules to LibraryService.
 * - Module 4: Type Conversion - Parsing text field values with Integer.parseInt(), handling NumberFormatException.
 * - Module 9: Non-static Inner Class (NavigationHandler) accessing enclosing class fields.
 * - Module 14: Exception handling - Capturing custom exceptions (BookUnavailableException, DuplicateBookException)
 *              and displaying intuitive graphical dialogs.
 * - Module 15: Multithreading - Background inventory auditing thread integrated with Swing without freezing UI.
 */
public class LibraryManagementSystem extends JFrame {

    // =========================================================================
    // MODERN DARK THEME COLOR PALETTE (HIGH DIFFERENTIATION & CONTRAST)
    // =========================================================================
    // Surfaces & Backgrounds
    public static final Color COLOR_BG = new Color(15, 23, 42);             // #0f172a Deep Slate Navy
    public static final Color COLOR_SIDEBAR = new Color(10, 15, 29);         // #0a0f1d Darkest Midnight
    public static final Color COLOR_SIDEBAR_HOVER = new Color(30, 41, 59);   // #1e293b Slate 800
    public static final Color COLOR_CARD = new Color(30, 41, 59);            // #1e293b Card Surface
    public static final Color COLOR_CARD_ALT = new Color(24, 34, 53);        // #182235 Alternating Row / Card
    public static final Color COLOR_CARD_BORDER = new Color(51, 65, 85);     // #334155 Subtle border

    // Typography
    public static final Color COLOR_TEXT_MAIN = new Color(248, 250, 252);    // #f8fafc Crisp White
    public static final Color COLOR_TEXT_MUTED = new Color(148, 163, 184);   // #94a3b8 Slate 400
    public static final Color COLOR_TEXT_DIM = new Color(100, 116, 139);     // #64748b Slate 500

    // Differentiated Vibrant Accents
    public static final Color COLOR_CYAN = new Color(56, 189, 248);          // #38bdf8 Electric Sky
    public static final Color COLOR_PRIMARY = new Color(59, 130, 246);       // #3b82f6 Modern Blue
    public static final Color COLOR_PRIMARY_DARK = new Color(37, 99, 235);   // #2563eb Royal Blue
    public static final Color COLOR_PURPLE = new Color(168, 85, 247);        // #a855f7 Vibrant Violet
    public static final Color COLOR_SUCCESS = new Color(16, 185, 129);       // #10b981 Emerald Green
    public static final Color COLOR_WARNING = new Color(245, 158, 11);       // #f59e0b Amber Gold
    public static final Color COLOR_DANGER = new Color(239, 68, 68);         // #ef4444 Rose Red
    public static final Color COLOR_CORAL = new Color(244, 63, 94);          // #f43f5e Coral Pink
    public static final Color COLOR_TEAL = new Color(20, 184, 166);          // #14b8a6 Teal

    // Terminal & Input Backgrounds
    public static final Color COLOR_TERMINAL_BG = new Color(10, 15, 29);     // #0a0f1d
    public static final Color COLOR_INPUT_BG = new Color(15, 23, 42);        // #0f172a
    public static final Color COLOR_INPUT_BORDER = new Color(71, 85, 105);   // #475569

    // Business Logic Service
    private final LibraryService libraryService;

    // Multithreading Background Task
    private BackgroundInventoryCheck backgroundThread;
    private BackgroundAlertTask alertTask;

    // GUI Layout Components
    private JPanel mainContentPanel;
    private CardLayout cardLayout;
    private JLabel statusLabel;
    private final List<JButton> navButtons = new ArrayList<>();

    // Dashboard Metric Labels
    private JLabel lblTotalTitles;
    private JLabel lblTotalCopies;
    private JLabel lblAvailableCopies;
    private JLabel lblIssuedCopies;
    private JLabel lblMembersCount;
    private DefaultTableModel dashboardTxTableModel;

    // Books View Components
    private JTable booksTable;
    private DefaultTableModel booksTableModel;
    private JTextField txtSearchBook;
    private JComboBox<String> cmbCategoryFilter;

    // Members View Components
    private JTable membersTable;
    private DefaultTableModel membersTableModel;
    private JTextField txtSearchMember;

    // Transactions View Components
    private JTable txTable;
    private DefaultTableModel txTableModel;

    // Reports View Components
    private JTextArea txtAuditLog;
    private JLabel lblThreadStatus;

    /**
     * Constructor initializing GUI and starting background services.
     */
    public LibraryManagementSystem(LibraryService service) {
        super("MUJ CSC2104 - Library Management System (OOP Dark Edition)");
        this.libraryService = service;

        initWindowSettings();
        initBackgroundThreads();
        initComponents();
        refreshAllData();

        // Welcome status
        updateStatusBar("Ready. MUJ CSC2104 Dark Mode System Active.");
    }

    private void initWindowSettings() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 780);
        setMinimumSize(new Dimension(1040, 680));
        setLocationRelativeTo(null); // Center on screen
    }

    /**
     * Module 15: Initializes Background Threads
     */
    private void initBackgroundThreads() {
        alertTask = new BackgroundAlertTask();
        Thread alertConsumerThread = new Thread(alertTask, "AlertConsumer-Worker");
        alertConsumerThread.setDaemon(true); // Daemon thread exits when application closes
        alertConsumerThread.start();

        // Background Inventory Check thread extending Thread
        backgroundThread = new BackgroundInventoryCheck(libraryService, new BackgroundInventoryCheck.InventoryListener() {
            @Override
            public void onStockAlert(String message, boolean isCritical) {
                // Safely update Swing GUI from background thread
                SwingUtilities.invokeLater(() -> {
                    if (txtAuditLog != null) {
                        txtAuditLog.append("[STOCK ALERT] " + message + "\n");
                    }
                    updateStatusBar(message);
                });
            }

            @Override
            public void onCheckCompleted(int outOfStock, int lowStock, long duration) {
                SwingUtilities.invokeLater(() -> {
                    if (lblThreadStatus != null) {
                        lblThreadStatus.setText("Thread State: " + backgroundThread.getState() +
                                                " | Last Scan: " + outOfStock + " out-of-stock, " +
                                                lowStock + " low-stock (" + duration + " ms)");
                    }
                });
            }
        }, 8000); // Check every 8 seconds

        backgroundThread.setAlertTask(alertTask);
        backgroundThread.setDaemon(true);
        backgroundThread.start(); // Module 15: start() invokes run() on a new thread
    }

    private void initComponents() {
        JPanel rootPanel = new JPanel(new BorderLayout());
        rootPanel.setBackground(COLOR_BG);

        // 1. Sidebar Navigation Panel (West)
        JPanel sidebarPanel = createSidebarPanel();
        rootPanel.add(sidebarPanel, BorderLayout.WEST);

        // 2. Center Card Panel
        cardLayout = new CardLayout();
        mainContentPanel = new JPanel(cardLayout);
        mainContentPanel.setBackground(COLOR_BG);

        // Add View Panels to CardLayout
        mainContentPanel.add(createDashboardPanel(), "DASHBOARD");
        mainContentPanel.add(createBooksPanel(), "BOOKS");
        mainContentPanel.add(createMembersPanel(), "MEMBERS");
        mainContentPanel.add(createIssueReturnPanel(), "ISSUE_RETURN");
        mainContentPanel.add(createReportsPanel(), "REPORTS");
        mainContentPanel.add(createSyllabusDemoPanel(), "SYLLABUS_DEMO");

        rootPanel.add(mainContentPanel, BorderLayout.CENTER);

        // 3. Status Bar (South)
        JPanel statusBar = createStatusBar();
        rootPanel.add(statusBar, BorderLayout.SOUTH);

        setContentPane(rootPanel);

        // Activate first nav button visually
        if (!navButtons.isEmpty()) {
            setActiveNavButton(navButtons.get(0));
        }
    }

    // =========================================================================
    // Module 9: NON-STATIC INNER CLASS (NavigationHandler)
    // =========================================================================

    /**
     * Module 9: Non-static Inner Class demonstration.
     * Handles sidebar navigation button actions.
     * Can directly access enclosing class instance variables (cardLayout, mainContentPanel, statusLabel).
     */
    private class NavigationHandler implements ActionListener {
        private final String targetCard;
        private final JButton button;

        public NavigationHandler(String targetCard, JButton button) {
            this.targetCard = targetCard;
            this.button = button;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            // Update active state visual
            setActiveNavButton(button);
            cardLayout.show(mainContentPanel, targetCard);
            refreshAllData();
            updateStatusBar("Navigated to " + targetCard.replace('_', ' ') + " view.");
        }
    }

    private void setActiveNavButton(JButton activeButton) {
        for (JButton btn : navButtons) {
            if (btn == activeButton) {
                btn.setBackground(COLOR_CARD);
                btn.setForeground(COLOR_CYAN);
                btn.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 4, 0, 0, COLOR_CYAN),
                        BorderFactory.createEmptyBorder(0, 16, 0, 10)
                ));
            } else {
                btn.setBackground(COLOR_SIDEBAR);
                btn.setForeground(COLOR_TEXT_MUTED);
                btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
            }
        }
    }

    // =========================================================================
    // SIDEBAR NAVIGATION
    // =========================================================================

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(COLOR_SIDEBAR);
        sidebar.setPreferredSize(new Dimension(245, 0));
        sidebar.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 1, COLOR_CARD_BORDER),
                new EmptyBorder(22, 16, 20, 16)
        ));

        // Brand / Logo Title with Differentiated Color Accent
        JPanel brandPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        brandPanel.setBackground(COLOR_SIDEBAR);
        brandPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblMuj = new JLabel("MUJ ");
        lblMuj.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblMuj.setForeground(COLOR_CYAN);

        JLabel lblLib = new JLabel("LIBRARY");
        lblLib.setFont(new Font("SansSerif", Font.BOLD, 22));
        lblLib.setForeground(COLOR_TEXT_MAIN);

        brandPanel.add(lblMuj);
        brandPanel.add(lblLib);

        JLabel courseSubtitle = new JLabel("CSC2104 OOP Dark Edition");
        courseSubtitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
        courseSubtitle.setForeground(COLOR_TEXT_DIM);
        courseSubtitle.setAlignmentX(Component.LEFT_ALIGNMENT);

        sidebar.add(brandPanel);
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(courseSubtitle);
        sidebar.add(Box.createVerticalStrut(28));

        // Navigation Buttons
        navButtons.clear();
        sidebar.add(createNavButton("📊 Dashboard", "DASHBOARD"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("📚 Books Management", "BOOKS"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("👥 Member Management", "MEMBERS"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("🔄 Book Issue & Return", "ISSUE_RETURN"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("📈 Reports & Backup", "REPORTS"));
        sidebar.add(Box.createVerticalStrut(6));
        sidebar.add(createNavButton("🎓 Syllabus & Viva Tools", "SYLLABUS_DEMO"));

        sidebar.add(Box.createVerticalGlue());

        // Sidebar Footer Card
        JPanel footerCard = new JPanel(new BorderLayout(4, 4));
        footerCard.setBackground(COLOR_CARD);
        footerCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                new EmptyBorder(10, 10, 10, 10)
        ));
        footerCard.setMaximumSize(new Dimension(215, 60));
        footerCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel lblStudent = new JLabel("Manipal University Jaipur");
        lblStudent.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblStudent.setForeground(COLOR_CYAN);

        JLabel lblSemester = new JLabel("B.Tech CSE - 3rd Semester");
        lblSemester.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblSemester.setForeground(COLOR_TEXT_MUTED);

        footerCard.add(lblStudent, BorderLayout.NORTH);
        footerCard.add(lblSemester, BorderLayout.CENTER);

        sidebar.add(footerCard);

        return sidebar;
    }

    private JButton createNavButton(String text, String cardName) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btn.setForeground(COLOR_TEXT_MUTED);
        btn.setBackground(COLOR_SIDEBAR);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(true);
        btn.setHorizontalAlignment(SwingConstants.LEFT);
        btn.setMaximumSize(new Dimension(215, 42));
        btn.setPreferredSize(new Dimension(215, 42));
        btn.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 10));
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Module 9: Non-static inner class instantiated with outer class reference
        btn.addActionListener(new NavigationHandler(cardName, btn));
        navButtons.add(btn);
        return btn;
    }

    // =========================================================================
    // 1. DASHBOARD VIEW (MODULE 2, 5) - VIBRANT METRIC CARDS
    // =========================================================================

    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(22, 22, 22, 22));

        // Header Title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(COLOR_BG);

        JLabel title = new JLabel("System Overview & Summary Statistics");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        title.setForeground(COLOR_TEXT_MAIN);

        JLabel sub = new JLabel("Live real-time counters and active 14-day loan monitor");
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(COLOR_TEXT_MUTED);

        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(sub, BorderLayout.CENTER);
        panel.add(headerPanel, BorderLayout.NORTH);

        // Metric Cards Grid (5 Cards with Rich Differentiation)
        JPanel cardsGrid = new JPanel(new GridLayout(1, 5, 12, 0));
        cardsGrid.setBackground(COLOR_BG);
        cardsGrid.setPreferredSize(new Dimension(0, 105));

        lblTotalTitles = new JLabel("0", SwingConstants.CENTER);
        lblTotalCopies = new JLabel("0", SwingConstants.CENTER);
        lblAvailableCopies = new JLabel("0", SwingConstants.CENTER);
        lblIssuedCopies = new JLabel("0", SwingConstants.CENTER);
        lblMembersCount = new JLabel("0", SwingConstants.CENTER);

        cardsGrid.add(createMetricCard("TOTAL TITLES", "📚", lblTotalTitles, COLOR_CYAN, "Unique Catalog"));
        cardsGrid.add(createMetricCard("TOTAL COPIES", "📦", lblTotalCopies, COLOR_PURPLE, "Physical Stock"));
        cardsGrid.add(createMetricCard("AVAILABLE", "✅", lblAvailableCopies, COLOR_SUCCESS, "Ready to Loan"));
        cardsGrid.add(createMetricCard("ISSUED", "🔄", lblIssuedCopies, COLOR_WARNING, "Active Borrows"));
        cardsGrid.add(createMetricCard("MEMBERS", "👥", lblMembersCount, COLOR_CORAL, "Students & Faculty"));

        // Center: Active Transactions Table
        JPanel centerPanel = new JPanel(new BorderLayout(10, 10));
        centerPanel.setBackground(COLOR_CARD);
        centerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                new EmptyBorder(16, 16, 16, 16)
        ));

        JPanel tblHeaderPanel = new JPanel(new BorderLayout());
        tblHeaderPanel.setBackground(COLOR_CARD);

        JLabel tblHeader = new JLabel("Recent & Active Issue Transactions");
        tblHeader.setFont(new Font("SansSerif", Font.BOLD, 15));
        tblHeader.setForeground(COLOR_TEXT_MAIN);

        JLabel tblSub = new JLabel("Transactions with 14-day auto due date");
        tblSub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        tblSub.setForeground(COLOR_TEXT_MUTED);

        tblHeaderPanel.add(tblHeader, BorderLayout.NORTH);
        tblHeaderPanel.add(tblSub, BorderLayout.CENTER);
        centerPanel.add(tblHeaderPanel, BorderLayout.NORTH);

        String[] cols = {"Tx ID", "Book ID", "Member ID", "Issue Date", "Due Date", "Return Date", "Status"};
        dashboardTxTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable dbTable = new JTable(dashboardTxTableModel);
        styleTable(dbTable);
        centerPanel.add(createStyledScrollPane(dbTable), BorderLayout.CENTER);

        // Combine into main container
        JPanel content = new JPanel(new BorderLayout(0, 15));
        content.setBackground(COLOR_BG);
        content.add(cardsGrid, BorderLayout.NORTH);
        content.add(centerPanel, BorderLayout.CENTER);

        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createMetricCard(String title, String icon, JLabel valueLabel, Color accentColor, String subtitle) {
        JPanel card = new JPanel(new BorderLayout(4, 4));
        card.setBackground(COLOR_CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, accentColor),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                        new EmptyBorder(12, 10, 10, 10)
                )
        ));

        // Header with Icon
        JLabel lblTitle = new JLabel(icon + " " + title, SwingConstants.CENTER);
        lblTitle.setFont(new Font("SansSerif", Font.BOLD, 11));
        lblTitle.setForeground(accentColor);

        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        valueLabel.setForeground(accentColor);

        JLabel lblSub = new JLabel(subtitle, SwingConstants.CENTER);
        lblSub.setFont(new Font("SansSerif", Font.PLAIN, 10));
        lblSub.setForeground(COLOR_TEXT_DIM);

        card.add(lblTitle, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        card.add(lblSub, BorderLayout.SOUTH);
        return card;
    }

    // =========================================================================
    // 2. BOOKS MANAGEMENT VIEW (MODULE 2, 4, 5, 7, 8, 14)
    // =========================================================================

    private JPanel createBooksPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Search and Action Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setBackground(COLOR_BG);

        JLabel lblSearch = new JLabel("Search Books:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSearch.setForeground(COLOR_TEXT_MAIN);

        txtSearchBook = new JTextField(15);
        txtSearchBook.setPreferredSize(new Dimension(170, 34));
        styleTextField(txtSearchBook);

        JButton btnSearch = createStyledButton("Search", COLOR_PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterBooksTable());

        JButton btnReset = createStyledButton("Reset", COLOR_CARD_ALT, COLOR_TEXT_MAIN);
        btnReset.addActionListener(e -> {
            txtSearchBook.setText("");
            if (cmbCategoryFilter != null) cmbCategoryFilter.setSelectedIndex(0);
            refreshBooksTable();
        });

        JLabel lblCat = new JLabel("Category:");
        lblCat.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblCat.setForeground(COLOR_TEXT_MAIN);

        String[] categories = {"All", "Computer Science", "Software Engineering", "Mathematics", "Physics", "General"};
        cmbCategoryFilter = new JComboBox<>(categories);
        cmbCategoryFilter.setPreferredSize(new Dimension(160, 34));
        styleComboBox(cmbCategoryFilter);
        cmbCategoryFilter.addActionListener(e -> filterBooksTable());

        JButton btnAddBook = createStyledButton("+ Add Book", COLOR_SUCCESS, Color.WHITE);
        btnAddBook.addActionListener(e -> showAddBookDialog());

        JButton btnEditBook = createStyledButton("Edit Selected", COLOR_PURPLE, Color.WHITE);
        btnEditBook.addActionListener(e -> showEditBookDialog());

        JButton btnDeleteBook = createStyledButton("Delete Selected", COLOR_DANGER, Color.WHITE);
        btnDeleteBook.addActionListener(e -> deleteSelectedBook());

        topBar.add(lblSearch);
        topBar.add(txtSearchBook);
        topBar.add(lblCat);
        topBar.add(cmbCategoryFilter);
        topBar.add(btnSearch);
        topBar.add(btnReset);
        topBar.add(Box.createHorizontalStrut(15));
        topBar.add(btnAddBook);
        topBar.add(btnEditBook);
        topBar.add(btnDeleteBook);

        panel.add(topBar, BorderLayout.NORTH);

        // Books Table (Module 5: Populated from 2D Array Matrix)
        String[] cols = {"Book ID", "Title", "Author", "Category", "Total Copies", "Available Copies", "Issued Copies"};
        booksTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        booksTable = new JTable(booksTableModel);
        styleTable(booksTable);
        panel.add(createStyledScrollPane(booksTable), BorderLayout.CENTER);

        return panel;
    }

    private void refreshBooksTable() {
        booksTableModel.setRowCount(0);
        String[][] matrix = libraryService.getInventoryReportMatrix();
        for (String[] row : matrix) {
            booksTableModel.addRow(row);
        }
    }

    private void filterBooksTable() {
        String keyword = txtSearchBook.getText().trim();
        String cat = (String) cmbCategoryFilter.getSelectedItem();
        // Module 7: Invokes overloaded search method
        Book[] results = libraryService.searchBooks(keyword, cat);

        booksTableModel.setRowCount(0);
        for (Book b : results) {
            if (b != null) {
                booksTableModel.addRow(new Object[]{
                        b.getBookId(), b.getTitle(), b.getAuthor(), b.getCategory(),
                        b.getTotalCopies(), b.getAvailableCopies(), b.getIssuedCopies()
                });
            }
        }
        updateStatusBar("Filtered book search returned " + results.length + " matching record(s).");
    }

    /**
     * Dialog for adding a new book.
     * Demonstrates Module 4 (parsing integer), Module 14 (DuplicateBookException handling).
     */
    private void showAddBookDialog() {
        JTextField txtId = new JTextField();
        JTextField txtTitle = new JTextField();
        JTextField txtAuthor = new JTextField();
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{"Computer Science", "Software Engineering", "Mathematics", "Physics", "General"});
        JSpinner spnCopies = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));

        styleTextField(txtId);
        styleTextField(txtTitle);
        styleTextField(txtAuthor);
        styleComboBox(cmbCat);

        JPanel form = createFormPanel(5, 2);
        form.add(createFormLabel("Book ID (e.g. B106):"));
        form.add(txtId);
        form.add(createFormLabel("Title:"));
        form.add(txtTitle);
        form.add(createFormLabel("Author:"));
        form.add(txtAuthor);
        form.add(createFormLabel("Category:"));
        form.add(cmbCat);
        form.add(createFormLabel("Total Copies:"));
        form.add(spnCopies);

        int result = JOptionPane.showConfirmDialog(this, form, "Add New Book Record", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String id = txtId.getText().trim();
            String title = txtTitle.getText().trim();
            String author = txtAuthor.getText().trim();
            String cat = (String) cmbCat.getSelectedItem();
            int copies = (Integer) spnCopies.getValue();

            if (id.isEmpty() || title.isEmpty() || author.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields are required!", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                // Module 6: Parameterized constructor
                Book newBook = new Book(id, title, author, cat, copies);
                // Module 14: May throw DuplicateBookException
                libraryService.addBook(newBook);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Book '" + title + "' added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                updateStatusBar("Book added: " + id + " - " + title);
            } catch (DuplicateBookException dbe) {
                // Module 14: Catch custom checked exception
                JOptionPane.showMessageDialog(this, dbe.getMessage(), "Duplicate ID Detected", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error adding book: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void showEditBookDialog() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to edit.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) booksTableModel.getValueAt(selectedRow, 0);
        Book book = libraryService.findBook(bookId);
        if (book == null) return;

        JTextField txtTitle = new JTextField(book.getTitle());
        JTextField txtAuthor = new JTextField(book.getAuthor());
        JComboBox<String> cmbCat = new JComboBox<>(new String[]{"Computer Science", "Software Engineering", "Mathematics", "Physics", "General"});
        cmbCat.setSelectedItem(book.getCategory());
        JSpinner spnCopies = new JSpinner(new SpinnerNumberModel(book.getTotalCopies(), book.getIssuedCopies(), 500, 1));

        styleTextField(txtTitle);
        styleTextField(txtAuthor);
        styleComboBox(cmbCat);

        JPanel form = createFormPanel(5, 2);
        form.add(createFormLabel("Book ID:"));
        JLabel lblIdVal = new JLabel(book.getBookId());
        lblIdVal.setForeground(COLOR_CYAN);
        lblIdVal.setFont(new Font("SansSerif", Font.BOLD, 13));
        form.add(lblIdVal);
        form.add(createFormLabel("Title:"));
        form.add(txtTitle);
        form.add(createFormLabel("Author:"));
        form.add(txtAuthor);
        form.add(createFormLabel("Category:"));
        form.add(cmbCat);
        form.add(createFormLabel("Total Copies (min: " + book.getIssuedCopies() + "):"));
        form.add(spnCopies);

        int result = JOptionPane.showConfirmDialog(this, form, "Edit Book Details", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            book.setTitle(txtTitle.getText().trim());
            book.setAuthor(txtAuthor.getText().trim());
            book.setCategory((String) cmbCat.getSelectedItem());
            book.setTotalCopies((Integer) spnCopies.getValue());

            libraryService.updateBook(book);
            refreshAllData();
            JOptionPane.showMessageDialog(this, "Book updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedBook() {
        int selectedRow = booksTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a book from the table to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String bookId = (String) booksTableModel.getValueAt(selectedRow, 0);
        String title = (String) booksTableModel.getValueAt(selectedRow, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete book '" + title + "' (ID: " + bookId + ")?\nThis action cannot be undone.",
                "Confirm Book Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                // Module 14: Exception if active issues exist
                boolean deleted = libraryService.deleteBook(bookId);
                if (deleted) {
                    refreshAllData();
                    JOptionPane.showMessageDialog(this, "Book deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                    updateStatusBar("Book deleted: " + bookId + " (Memory eligible for GC)");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Deletion Prevented", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 3. MEMBERS MANAGEMENT (MODULE 10: INHERITANCE, MODULE 11: POLYMORPHISM)
    // =========================================================================

    private JPanel createMembersPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        topBar.setBackground(COLOR_BG);

        JLabel lblSearch = new JLabel("Search Members:");
        lblSearch.setFont(new Font("SansSerif", Font.BOLD, 13));
        lblSearch.setForeground(COLOR_TEXT_MAIN);

        txtSearchMember = new JTextField(15);
        txtSearchMember.setPreferredSize(new Dimension(170, 34));
        styleTextField(txtSearchMember);

        JButton btnSearch = createStyledButton("Search", COLOR_PRIMARY, Color.WHITE);
        btnSearch.addActionListener(e -> filterMembersTable());

        JButton btnReset = createStyledButton("Reset", COLOR_CARD_ALT, COLOR_TEXT_MAIN);
        btnReset.addActionListener(e -> {
            txtSearchMember.setText("");
            refreshMembersTable();
        });

        JButton btnAddMember = createStyledButton("+ Register Member", COLOR_SUCCESS, Color.WHITE);
        btnAddMember.addActionListener(e -> showRegisterMemberDialog());

        JButton btnDeleteMember = createStyledButton("Delete Member", COLOR_DANGER, Color.WHITE);
        btnDeleteMember.addActionListener(e -> deleteSelectedMember());

        topBar.add(lblSearch);
        topBar.add(txtSearchMember);
        topBar.add(btnSearch);
        topBar.add(btnReset);
        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(btnAddMember);
        topBar.add(btnDeleteMember);

        panel.add(topBar, BorderLayout.NORTH);

        // Members Table with Polymorphic Role Description column
        String[] cols = {"Member ID", "Name", "Contact", "Type", "Borrow Quota", "Polymorphic Role Description"};
        membersTableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) {
                return false;
            }
        };

        membersTable = new JTable(membersTableModel);
        styleTable(membersTable);
        panel.add(createStyledScrollPane(membersTable), BorderLayout.CENTER);

        return panel;
    }

    private void refreshMembersTable() {
        membersTableModel.setRowCount(0);
        String[][] matrix = libraryService.getMemberTableMatrix();
        for (String[] row : matrix) {
            membersTableModel.addRow(row);
        }
    }

    private void filterMembersTable() {
        String kw = txtSearchMember.getText().trim();
        Member[] results = libraryService.searchMembers(kw);
        membersTableModel.setRowCount(0);
        for (Member m : results) {
            if (m != null) {
                membersTableModel.addRow(new Object[]{
                        m.getId(), m.getName(), m.getContact(),
                        (m instanceof StudentMember ? "Student" : "Standard"),
                        m.getMaxBooksAllowed(),
                        // Module 11: Dynamic method dispatch in action
                        m.getRoleDescription()
                });
            }
        }
    }

    /**
     * Dialog to register Standard Member or StudentMember (Demonstrating Module 10 Inheritance).
     */
    private void showRegisterMemberDialog() {
        JTextField txtId = new JTextField();
        JTextField txtName = new JTextField();
        JTextField txtContact = new JTextField();
        JRadioButton rdoStandard = new JRadioButton("Standard Member", true);
        JRadioButton rdoStudent = new JRadioButton("Student Member (MUJ)");
        rdoStandard.setForeground(COLOR_TEXT_MAIN);
        rdoStudent.setForeground(COLOR_CYAN);
        rdoStandard.setBackground(COLOR_CARD);
        rdoStudent.setBackground(COLOR_CARD);

        ButtonGroup grp = new ButtonGroup();
        grp.add(rdoStandard);
        grp.add(rdoStudent);

        JTextField txtRegNo = new JTextField("239301000");
        JTextField txtDept = new JTextField("CSE");
        JSpinner spnSem = new JSpinner(new SpinnerNumberModel(3, 1, 8, 1));

        styleTextField(txtId);
        styleTextField(txtName);
        styleTextField(txtContact);
        styleTextField(txtRegNo);
        styleTextField(txtDept);

        txtRegNo.setEnabled(false);
        txtDept.setEnabled(false);
        spnSem.setEnabled(false);

        rdoStudent.addActionListener(e -> {
            txtRegNo.setEnabled(true);
            txtDept.setEnabled(true);
            spnSem.setEnabled(true);
        });

        rdoStandard.addActionListener(e -> {
            txtRegNo.setEnabled(false);
            txtDept.setEnabled(false);
            spnSem.setEnabled(false);
        });

        JPanel form = createFormPanel(8, 2);
        form.add(createFormLabel("Member Type:"));
        JPanel typePnl = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        typePnl.setBackground(COLOR_CARD);
        typePnl.add(rdoStandard);
        typePnl.add(rdoStudent);
        form.add(typePnl);

        form.add(createFormLabel("Member ID (e.g. S303 / M203):"));
        form.add(txtId);
        form.add(createFormLabel("Full Name:"));
        form.add(txtName);
        form.add(createFormLabel("Contact / Email:"));
        form.add(txtContact);
        form.add(createFormLabel("Student Reg No:"));
        form.add(txtRegNo);
        form.add(createFormLabel("Department:"));
        form.add(txtDept);
        form.add(createFormLabel("Semester:"));
        form.add(spnSem);

        int result = JOptionPane.showConfirmDialog(this, form, "Register New Member (Module 10 Inheritance)",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            String id = txtId.getText().trim();
            String name = txtName.getText().trim();
            String contact = txtContact.getText().trim();

            if (id.isEmpty() || name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "ID and Name cannot be empty!", "Validation", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Member newMember;
                if (rdoStudent.isSelected()) {
                    String regNo = txtRegNo.getText().trim();
                    String dept = txtDept.getText().trim();
                    int sem = (Integer) spnSem.getValue();
                    // Module 10: Instantiate StudentMember (multilevel child)
                    newMember = new StudentMember(id, name, contact, "2024-08-01", 4, regNo, dept, sem);
                } else {
                    // Module 10: Instantiate Member
                    newMember = new Member(id, name, contact, "2024-01-01", 2);
                }

                libraryService.registerMember(newMember);
                refreshAllData();
                JOptionPane.showMessageDialog(this, "Member registered successfully!\nRole: " + newMember.getRoleDescription(),
                        "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void deleteSelectedMember() {
        int row = membersTable.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Please select a member to delete.", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String memId = (String) membersTableModel.getValueAt(row, 0);
        String name = (String) membersTableModel.getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(this,
                "Delete member '" + name + "' (" + memId + ")?\nMembers with active borrowed books cannot be deleted.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean ok = libraryService.deleteMember(memId);
                if (ok) {
                    refreshAllData();
                    JOptionPane.showMessageDialog(this, "Member deleted successfully.", "Deleted", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Deletion Blocked", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // 4. BOOK ISSUE & RETURN VIEW (MODULE 2, 8, 14)
    // =========================================================================

    private JPanel createIssueReturnPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 2, 22, 0));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(25, 25, 25, 25));

        // LEFT CARD: ISSUE BOOK
        JPanel issueCard = new JPanel(new BorderLayout(15, 15));
        issueCard.setBackground(COLOR_CARD);
        issueCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, COLOR_PRIMARY),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                        new EmptyBorder(22, 22, 22, 22)
                )
        ));

        JLabel lblIssueTitle = new JLabel("📖 Issue Book (14-Day Loan Policy)");
        lblIssueTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblIssueTitle.setForeground(COLOR_CYAN);
        issueCard.add(lblIssueTitle, BorderLayout.NORTH);

        JPanel issueForm = new JPanel(new GridBagLayout());
        issueForm.setBackground(COLOR_CARD);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 8, 10, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JComboBox<String> cmbIssueMember = new JComboBox<>();
        JComboBox<String> cmbIssueBook = new JComboBox<>();
        styleComboBox(cmbIssueMember);
        styleComboBox(cmbIssueBook);

        JLabel lblLoanInfo = new JLabel("⚡ Standard loan duration: 14 days (Automatic calculation)");
        lblLoanInfo.setForeground(COLOR_TEXT_MUTED);
        lblLoanInfo.setFont(new Font("SansSerif", Font.ITALIC, 11));

        gbc.gridx = 0; gbc.gridy = 0;
        issueForm.add(createFormLabel("Select Member:"), gbc);
        gbc.gridx = 1;
        issueForm.add(cmbIssueMember, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        issueForm.add(createFormLabel("Select Book:"), gbc);
        gbc.gridx = 1;
        issueForm.add(cmbIssueBook, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        issueForm.add(lblLoanInfo, gbc);

        JButton btnIssue = createStyledButton("Confirm Issue Transaction", COLOR_PRIMARY, Color.WHITE);
        btnIssue.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnIssue.setPreferredSize(new Dimension(0, 40));
        gbc.gridy = 3;
        issueForm.add(btnIssue, gbc);

        issueCard.add(issueForm, BorderLayout.CENTER);

        // RIGHT CARD: RETURN BOOK
        JPanel returnCard = new JPanel(new BorderLayout(15, 15));
        returnCard.setBackground(COLOR_CARD);
        returnCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(3, 0, 0, 0, COLOR_SUCCESS),
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                        new EmptyBorder(22, 22, 22, 22)
                )
        ));

        JLabel lblReturnTitle = new JLabel("↩ Return Book & Restore Inventory");
        lblReturnTitle.setFont(new Font("SansSerif", Font.BOLD, 18));
        lblReturnTitle.setForeground(COLOR_SUCCESS);
        returnCard.add(lblReturnTitle, BorderLayout.NORTH);

        JPanel returnForm = new JPanel(new GridBagLayout());
        returnForm.setBackground(COLOR_CARD);

        JComboBox<String> cmbReturnTx = new JComboBox<>();
        styleComboBox(cmbReturnTx);

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 1;
        returnForm.add(createFormLabel("Active Transaction:"), gbc);
        gbc.gridx = 1;
        returnForm.add(cmbReturnTx, gbc);

        JLabel lblReturnHelp = new JLabel("Select an active transaction to return copy to inventory.");
        lblReturnHelp.setForeground(COLOR_TEXT_MUTED);
        lblReturnHelp.setFont(new Font("SansSerif", Font.ITALIC, 11));
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 2;
        returnForm.add(lblReturnHelp, gbc);

        JButton btnReturn = createStyledButton("Process Return", COLOR_SUCCESS, Color.WHITE);
        btnReturn.setFont(new Font("SansSerif", Font.BOLD, 13));
        btnReturn.setPreferredSize(new Dimension(0, 40));
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        returnForm.add(btnReturn, gbc);

        returnCard.add(returnForm, BorderLayout.CENTER);

        // Populate dropdowns helper
        Runnable populateCombos = () -> {
            cmbIssueMember.removeAllItems();
            Member[] members = libraryService.getAllMembers();
            for (Member m : members) {
                if (m != null) {
                    cmbIssueMember.addItem(m.getId() + " - " + m.getName() + " (" + (m instanceof StudentMember ? "Student" : "Standard") + ")");
                }
            }

            cmbIssueBook.removeAllItems();
            Book[] books = libraryService.getAllBooks();
            for (Book b : books) {
                if (b != null) {
                    cmbIssueBook.addItem(b.getBookId() + " - " + b.getTitle() + " [Avail: " + b.getAvailableCopies() + "/" + b.getTotalCopies() + "]");
                }
            }

            cmbReturnTx.removeAllItems();
            IssueTransaction[] txs = libraryService.getAllTransactions();
            for (IssueTransaction tx : txs) {
                if (tx != null && !tx.isReturned()) {
                    cmbReturnTx.addItem(tx.getTransactionId() + " | Book: " + tx.getBookId() + " | Member: " + tx.getMemberId() + " (Due: " + tx.getDueDate() + ")");
                }
            }
        };

        // Populate initially and attach listeners
        populateCombos.run();

        btnIssue.addActionListener(e -> {
            String selectedMemberStr = (String) cmbIssueMember.getSelectedItem();
            String selectedBookStr = (String) cmbIssueBook.getSelectedItem();

            if (selectedMemberStr == null || selectedBookStr == null) {
                JOptionPane.showMessageDialog(this, "Please select both a member and a book.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String memId = selectedMemberStr.split(" - ")[0].trim();
            String bId = selectedBookStr.split(" - ")[0].trim();

            try {
                // Module 14: Custom BookUnavailableException may be thrown
                IssueTransaction tx = libraryService.issueBook(memId, bId);
                refreshAllData();
                populateCombos.run();
                JOptionPane.showMessageDialog(this,
                        "Book issued successfully!\nTransaction ID: " + tx.getTransactionId() +
                        "\nIssue Date: " + tx.getIssueDate() + "\nDue Date (14 Days): " + tx.getDueDate(),
                        "Transaction Completed", JOptionPane.INFORMATION_MESSAGE);
                updateStatusBar("Transaction created: " + tx.getTransactionId());
            } catch (BookUnavailableException bue) {
                // Module 14: Catch custom checked exception
                JOptionPane.showMessageDialog(this, bue.getMessage(), "Book Unavailable (Module 14 Exception)", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Issue Rejected", JOptionPane.ERROR_MESSAGE);
            }
        });

        btnReturn.addActionListener(e -> {
            String selectedTxStr = (String) cmbReturnTx.getSelectedItem();
            if (selectedTxStr == null) {
                JOptionPane.showMessageDialog(this, "No active transaction selected.", "Selection Required", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String txId = selectedTxStr.split(" \\| ")[0].trim();
            int confirm = JOptionPane.showConfirmDialog(this, "Confirm return of book for " + txId + "?", "Return Confirmation", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    boolean ok = libraryService.returnBook(txId);
                    if (ok) {
                        refreshAllData();
                        populateCombos.run();
                        JOptionPane.showMessageDialog(this, "Book returned successfully and copy restored to inventory!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        updateStatusBar("Transaction returned: " + txId);
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Return Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(issueCard);
        panel.add(returnCard);
        return panel;
    }

    // =========================================================================
    // 5. REPORTS, MULTITHREADING & BACKUP VIEW (MODULE 5, 13, 15)
    // =========================================================================

    private JPanel createReportsPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Top Control Bar
        JPanel topBar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        topBar.setBackground(COLOR_BG);

        JButton btnRunAudit = createStyledButton("Run Inventory Auditor (Module 9 Inner Class)", COLOR_PRIMARY, Color.WHITE);
        btnRunAudit.addActionListener(e -> {
            // Module 9: Instantiate non-static inner class
            LibraryService.InventoryAuditor auditor = libraryService.createAuditor();
            String auditSummary = auditor.generateAuditSummary();
            txtAuditLog.append("[INNER CLASS AUDIT] " + auditSummary + "\n");
            JOptionPane.showMessageDialog(this, auditSummary, "Inventory Auditor Result", JOptionPane.INFORMATION_MESSAGE);
        });

        JButton btnBackup = createStyledButton("Create Binary Backup (Module 13)", COLOR_PURPLE, Color.WHITE);
        btnBackup.addActionListener(e -> {
            try {
                // Module 13: FileInputStream / FileOutputStream byte stream
                LibraryFileManager.FileOperationResult res = libraryService.createBinaryBackup();
                txtAuditLog.append("[BYTE STREAM BACKUP] " + res.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, res.getMessage(), "Binary Backup (FileInputStream/FileOutputStream)", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ioEx) {
                JOptionPane.showMessageDialog(this, ioEx.getMessage(), "Backup Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnRestore = createStyledButton("Restore Binary Backup", COLOR_WARNING, Color.WHITE);
        btnRestore.addActionListener(e -> {
            try {
                LibraryFileManager.FileOperationResult res = libraryService.restoreBinaryBackup();
                refreshAllData();
                txtAuditLog.append("[BYTE STREAM RESTORE] " + res.getMessage() + "\n");
                JOptionPane.showMessageDialog(this, res.getMessage(), "Binary Restore Complete", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ioEx) {
                JOptionPane.showMessageDialog(this, ioEx.getMessage(), "Restore Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton btnClearLog = createStyledButton("Clear Log", COLOR_CARD_ALT, COLOR_TEXT_MUTED);
        btnClearLog.addActionListener(e -> txtAuditLog.setText(""));

        topBar.add(btnRunAudit);
        topBar.add(btnBackup);
        topBar.add(btnRestore);
        topBar.add(btnClearLog);

        // Multithreading Control Sub-Panel
        JPanel threadControlCard = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        threadControlCard.setBackground(COLOR_CARD);
        threadControlCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1),
                new EmptyBorder(8, 12, 8, 12)
        ));

        lblThreadStatus = new JLabel("Thread State: RUNNABLE");
        lblThreadStatus.setFont(new Font("SansSerif", Font.BOLD, 12));
        lblThreadStatus.setForeground(COLOR_CYAN);

        JButton btnThreadPriority = createStyledButton("Toggle Priority (Module 15)", COLOR_TEAL, Color.WHITE);
        btnThreadPriority.addActionListener(e -> {
            if (backgroundThread != null) {
                int curr = backgroundThread.getPriority();
                int next = (curr == Thread.NORM_PRIORITY) ? Thread.MAX_PRIORITY : Thread.NORM_PRIORITY;
                backgroundThread.setPriority(next);
                txtAuditLog.append("[MULTITHREADING] Thread priority changed from " + curr + " to " + next + "\n");
                updateStatusBar("Thread priority updated to " + next);
            }
        });

        JButton btnThreadJoinDemo = createStyledButton("Test Thread.join() (Module 15)", COLOR_CORAL, Color.WHITE);
        btnThreadJoinDemo.addActionListener(e -> {
            // Spawn temporary inventory check thread and call join()
            new Thread(() -> {
                BackgroundInventoryCheck temp = new BackgroundInventoryCheck(libraryService, null, 1000);
                temp.start();
                try {
                    Thread.sleep(300);
                    temp.stopCheck();
                    temp.join(); // Module 15: join()
                    SwingUtilities.invokeLater(() -> {
                        txtAuditLog.append("[MULTITHREADING] Thread.join() test successful. Thread terminated.\n");
                        JOptionPane.showMessageDialog(this, "Thread.join() demonstration succeeded!\nThread finished and joined caller thread.", "Join Test", JOptionPane.INFORMATION_MESSAGE);
                    });
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
            }).start();
        });

        threadControlCard.add(lblThreadStatus);
        threadControlCard.add(Box.createHorizontalStrut(15));
        threadControlCard.add(btnThreadPriority);
        threadControlCard.add(btnThreadJoinDemo);

        // North layout combining top bars
        JPanel northContainer = new JPanel(new BorderLayout(0, 10));
        northContainer.setBackground(COLOR_BG);
        northContainer.add(topBar, BorderLayout.NORTH);
        northContainer.add(threadControlCard, BorderLayout.SOUTH);

        panel.add(northContainer, BorderLayout.NORTH);

        // Center Log Area
        txtAuditLog = new JTextArea();
        txtAuditLog.setFont(new Font("Monospaced", Font.PLAIN, 12));
        txtAuditLog.setEditable(false);
        txtAuditLog.setBackground(COLOR_TERMINAL_BG);
        txtAuditLog.setForeground(new Color(52, 211, 153)); // Matrix soft emerald
        txtAuditLog.setCaretColor(COLOR_CYAN);
        txtAuditLog.setText("=== SYSTEM AUDIT, BACKGROUND THREAD & FILE LOG ===\n");

        panel.add(createStyledScrollPane(txtAuditLog), BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // 6. SYLLABUS DEMO & VIVA VERIFICATION PANEL (MODULES 1 TO 16)
    // =========================================================================

    private JPanel createSyllabusDemoPanel() {
        JPanel panel = new JPanel(new BorderLayout(15, 15));
        panel.setBackground(COLOR_BG);
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));

        JLabel title = new JLabel("MUJ CSC2104 Syllabus Live Verification & Viva Tools");
        title.setFont(new Font("SansSerif", Font.BOLD, 18));
        title.setForeground(COLOR_TEXT_MAIN);
        panel.add(title, BorderLayout.NORTH);

        JTextArea txtOutput = new JTextArea();
        txtOutput.setFont(new Font("Monospaced", Font.PLAIN, 13));
        txtOutput.setEditable(false);
        txtOutput.setBackground(COLOR_TERMINAL_BG);
        txtOutput.setForeground(COLOR_CYAN);
        txtOutput.setBorder(new EmptyBorder(12, 12, 12, 12));
        txtOutput.setText("Click any test button below to execute the syllabus topic live in the JVM and view proof of implementation:\n\n");

        JPanel buttonsGrid = new JPanel(new GridLayout(2, 4, 10, 10));
        buttonsGrid.setBackground(COLOR_BG);

        JButton btn1 = createStyledButton("1. Polymorphic Dispatch", COLOR_PRIMARY, Color.WHITE);
        btn1.addActionListener(e -> {
            txtOutput.append("--- MODULE 11: METHOD OVERRIDING & RUNTIME POLYMORPHISM ---\n");
            String[] poly = libraryService.demonstratePolymorphism();
            for (String s : poly) {
                txtOutput.append("  [Dynamic Dispatch]: " + s + "\n");
            }
            txtOutput.append("Notice how StudentMember executes its specialized getRoleDescription() through a Member reference.\n\n");
        });

        JButton btn2 = createStyledButton("2. Autoboxing & Casting", COLOR_PURPLE, Color.WHITE);
        btn2.addActionListener(e -> {
            txtOutput.append("--- MODULE 4: WRAPPER CLASSES, AUTOBOXING & CASTING ---\n");
            txtOutput.append("  " + libraryService.demonstrateAutoboxingAndUnboxing() + "\n");
            Book sample = libraryService.findBook("B101");
            if (sample != null) {
                txtOutput.append("  Explicit Casting Availability Ratio: " + sample.getAvailabilityPercentage() + "%\n");
            }
            txtOutput.append("\n");
        });

        JButton btn3 = createStyledButton("3. Pass-by-Value Demo", COLOR_SUCCESS, Color.WHITE);
        btn3.addActionListener(e -> {
            txtOutput.append("--- MODULE 7: ARGUMENT PASSING (PASS-BY-VALUE) ---\n");
            Book sample = libraryService.findBook("B101");
            int primitive = 100;
            String report = libraryService.demonstratePassByValue(primitive, sample);
            txtOutput.append("  " + report + "\n");
            txtOutput.append("  Caller's primitive variable remains untouched: " + primitive + "\n\n");
        });

        JButton btn4 = createStyledButton("4. Recursive Search", COLOR_WARNING, Color.WHITE);
        btn4.addActionListener(e -> {
            txtOutput.append("--- MODULE 7: RECURSIVE BINARY SEARCH ALGORITHM ---\n");
            Book[] all = libraryService.getAllBooks();
            // Sort
            for (int i = 0; i < all.length - 1; i++) {
                for (int j = 0; j < all.length - i - 1; j++) {
                    if (all[j] != null && all[j + 1] != null &&
                        all[j].getBookId().compareToIgnoreCase(all[j + 1].getBookId()) > 0) {
                        Book temp = all[j];
                        all[j] = all[j + 1];
                        all[j + 1] = temp;
                    }
                }
            }
            Book found = libraryService.recursiveBinarySearch(all, 0, all.length - 1, "B102");
            txtOutput.append("  Recursive search for 'B102': " + (found != null ? found.toString() : "Not found") + "\n\n");
        });

        JButton btn5 = createStyledButton("5. Custom Exceptions", COLOR_DANGER, Color.WHITE);
        btn5.addActionListener(e -> {
            txtOutput.append("--- MODULE 14: CUSTOM EXCEPTION HANDLING ---\n");
            try {
                txtOutput.append("  Attempting to add duplicate book B101...\n");
                libraryService.addBook(new Book("B101", "Duplicate Test", "Author", "General", 1));
            } catch (DuplicateBookException dbe) {
                txtOutput.append("  [CAUGHT EXPECTED]: " + dbe.getClass().getName() + " -> " + dbe.getMessage() + "\n\n");
            } catch (Exception ex) {
                txtOutput.append("  [EXCEPTION]: " + ex.getMessage() + "\n\n");
            }
        });

        JButton btn6 = createStyledButton("6. Start vs Run (Mod 15)", COLOR_TEAL, Color.WHITE);
        btn6.addActionListener(e -> {
            txtOutput.append("--- MODULE 15: THREAD start() vs run() ---\n");
            txtOutput.append(BackgroundInventoryCheck.explainStartVsRun() + "\n\n");
        });

        JButton btn7 = createStyledButton("7. Static vs Inner Class", new Color(99, 102, 241), Color.WHITE);
        btn7.addActionListener(e -> {
            txtOutput.append("--- MODULE 9: NESTED & INNER CLASSES ---\n");
            txtOutput.append("  Static Nested Class: LibraryFileManager.FileOperationResult (No reference to outer instance)\n");
            txtOutput.append("  Non-Static Inner Class: LibraryService.InventoryAuditor (Holds implicit reference to outer LibraryService)\n\n");
        });

        JButton btn8 = createStyledButton("8. Terminal Console Mode", COLOR_CORAL, Color.WHITE);
        btn8.addActionListener(e -> {
            txtOutput.append("--- MODULE 13 & 3: INTERACTIVE CONSOLE UTILITY ---\n");
            txtOutput.append("  To test the Scanner & do-while loop in terminal, run:\n");
            txtOutput.append("  java -cp bin Main --console\n\n");
            JOptionPane.showMessageDialog(this,
                    "Interactive Console Mode can be run directly from terminal:\n\njava -cp bin Main --console\n\nThis runs the Scanner, BufferedReader, and do-while loop menu.",
                    "Console Mode Info", JOptionPane.INFORMATION_MESSAGE);
        });

        buttonsGrid.add(btn1);
        buttonsGrid.add(btn2);
        buttonsGrid.add(btn3);
        buttonsGrid.add(btn4);
        buttonsGrid.add(btn5);
        buttonsGrid.add(btn6);
        buttonsGrid.add(btn7);
        buttonsGrid.add(btn8);

        panel.add(buttonsGrid, BorderLayout.NORTH);
        panel.add(createStyledScrollPane(txtOutput), BorderLayout.CENTER);

        return panel;
    }

    // =========================================================================
    // STATUS BAR (MODULE 2)
    // =========================================================================

    private JPanel createStatusBar() {
        JPanel bar = new JPanel(new BorderLayout());
        bar.setPreferredSize(new Dimension(0, 28));
        bar.setBackground(COLOR_SIDEBAR);
        bar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_CARD_BORDER));

        statusLabel = new JLabel("  ● System Ready");
        statusLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        statusLabel.setForeground(COLOR_SUCCESS);

        JLabel lblCopy = new JLabel("MUJ CSE CSC2104 | Dark Edition Active   ");
        lblCopy.setFont(new Font("SansSerif", Font.PLAIN, 11));
        lblCopy.setForeground(COLOR_TEXT_DIM);

        bar.add(statusLabel, BorderLayout.WEST);
        bar.add(lblCopy, BorderLayout.EAST);
        return bar;
    }

    public void updateStatusBar(String msg) {
        if (statusLabel != null) {
            statusLabel.setText("  ● " + msg);
        }
    }

    // =========================================================================
    // DATA REFRESHING & STYLING HELPERS
    // =========================================================================

    public void refreshAllData() {
        // Refresh metric labels
        if (lblTotalTitles != null) {
            lblTotalTitles.setText(String.valueOf(libraryService.getTotalBookTitles()));
            lblTotalCopies.setText(String.valueOf(libraryService.getTotalBookCopies()));
            lblAvailableCopies.setText(String.valueOf(libraryService.getTotalAvailableCopies()));
            lblIssuedCopies.setText(String.valueOf(libraryService.getTotalIssuedCopies()));
            lblMembersCount.setText(String.valueOf(libraryService.getRegisteredMemberCount()));
        }

        // Refresh dashboard table
        if (dashboardTxTableModel != null) {
            dashboardTxTableModel.setRowCount(0);
            String[][] txs = libraryService.getTransactionTableMatrix(false);
            for (String[] tx : txs) {
                dashboardTxTableModel.addRow(tx);
            }
        }

        // Refresh books table
        if (booksTableModel != null) {
            refreshBooksTable();
        }

        // Refresh members table
        if (membersTableModel != null) {
            refreshMembersTable();
        }
    }

    private JButton createStyledButton(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setFont(new Font("SansSerif", Font.BOLD, 12));
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setOpaque(true);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bg.darker(), 1),
                BorderFactory.createEmptyBorder(7, 14, 7, 14)
        ));
        return btn;
    }

    private void styleTextField(JTextField field) {
        field.setBackground(COLOR_INPUT_BG);
        field.setForeground(COLOR_TEXT_MAIN);
        field.setCaretColor(COLOR_CYAN);
        field.setFont(new Font("SansSerif", Font.PLAIN, 13));
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_INPUT_BORDER, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)
        ));
    }

    private <T> void styleComboBox(JComboBox<T> box) {
        box.setBackground(COLOR_INPUT_BG);
        box.setForeground(COLOR_TEXT_MAIN);
        box.setFont(new Font("SansSerif", Font.PLAIN, 12));
        box.setBorder(BorderFactory.createLineBorder(COLOR_INPUT_BORDER, 1));
        box.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(javax.swing.JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                list.setBackground(COLOR_CARD);
                list.setSelectionBackground(COLOR_PRIMARY);
                list.setSelectionForeground(Color.WHITE);
                if (isSelected) {
                    setBackground(COLOR_PRIMARY);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(COLOR_CARD);
                    setForeground(COLOR_TEXT_MAIN);
                }
                setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
                return this;
            }
        });
    }

    private JScrollPane createStyledScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.getViewport().setBackground(COLOR_CARD);
        sp.setBackground(COLOR_CARD);
        sp.setBorder(BorderFactory.createLineBorder(COLOR_CARD_BORDER, 1));
        return sp;
    }

    private JPanel createFormPanel(int rows, int cols) {
        JPanel form = new JPanel(new GridLayout(rows, cols, 10, 10));
        form.setBackground(COLOR_CARD);
        form.setBorder(new EmptyBorder(10, 10, 10, 10));
        return form;
    }

    private JLabel createFormLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 12));
        lbl.setForeground(COLOR_TEXT_MAIN);
        return lbl;
    }

    private void styleTable(JTable table) {
        table.setRowHeight(32);
        table.setFont(new Font("SansSerif", Font.PLAIN, 12));
        table.setBackground(COLOR_CARD);
        table.setForeground(COLOR_TEXT_MAIN);
        table.setShowGrid(true);
        table.setGridColor(COLOR_CARD_BORDER);
        table.setSelectionBackground(COLOR_PRIMARY_DARK);
        table.setSelectionForeground(Color.WHITE);

        // Header styling
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.getTableHeader().setBackground(COLOR_SIDEBAR);
        table.getTableHeader().setForeground(COLOR_CYAN);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, COLOR_CARD_BORDER));
        table.getTableHeader().setReorderingAllowed(false);

        // Custom Cell Renderer with Dark Alternating Rows and Status Pill Highlighting
        DefaultTableCellRenderer darkCellRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable tbl, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);

                if (!isSelected) {
                    // Alternating Dark Rows
                    c.setBackground((row % 2 == 0) ? COLOR_CARD : COLOR_CARD_ALT);
                    c.setForeground(COLOR_TEXT_MAIN);

                    String colName = tbl.getColumnName(col);
                    String valStr = (value != null) ? value.toString() : "";

                    // Color Differentiation for Status, Quantities, and Badges
                    if (colName.contains("Status")) {
                        if ("ACTIVE".equalsIgnoreCase(valStr)) {
                            c.setForeground(COLOR_SUCCESS);
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else if ("RETURNED".equalsIgnoreCase(valStr)) {
                            c.setForeground(COLOR_TEXT_MUTED);
                        }
                    } else if (colName.contains("Available")) {
                        try {
                            int num = Integer.parseInt(valStr);
                            if (num == 0) {
                                c.setForeground(COLOR_DANGER);
                                setFont(getFont().deriveFont(Font.BOLD));
                            } else if (num < 2) {
                                c.setForeground(COLOR_WARNING);
                                setFont(getFont().deriveFont(Font.BOLD));
                            } else {
                                c.setForeground(COLOR_SUCCESS);
                            }
                        } catch (Exception ignored) {}
                    } else if (colName.contains("Issued")) {
                        c.setForeground(COLOR_WARNING);
                    } else if (colName.contains("Type")) {
                        if ("Student".equalsIgnoreCase(valStr)) {
                            c.setForeground(COLOR_CYAN);
                            setFont(getFont().deriveFont(Font.BOLD));
                        } else {
                            c.setForeground(COLOR_TEXT_MUTED);
                        }
                    } else if (colName.contains("ID")) {
                        c.setForeground(COLOR_CYAN);
                    }
                } else {
                    c.setBackground(COLOR_PRIMARY_DARK);
                    c.setForeground(Color.WHITE);
                }

                String colName = tbl.getColumnName(col);
                if (colName.contains("ID") || colName.contains("Copies") || colName.contains("Date") ||
                    colName.contains("Quota") || colName.contains("Status") || colName.contains("Type")) {
                    setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    setHorizontalAlignment(SwingConstants.LEFT);
                }
                return c;
            }
        };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(darkCellRenderer);
        }
    }
}
