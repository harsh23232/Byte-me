import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ByteMeGUI extends JFrame {
    private ByteMeSystem system;
    private JPanel mainPanel;
    private CardLayout layoutManager;
    private JTable menuTable;
    private JTable ordersTable;
    private DefaultTableModel menuTableModel;
    private DefaultTableModel ordersTableModel;

    public ByteMeGUI(ByteMeSystem system) {
        if (system == null) {
            throw new IllegalArgumentException("System reference cannot be null.");
        }
        this.system = system;

        // Configure main window
        setTitle("Byte Me - Food Ordering System");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Set up layout for swapping views
        layoutManager = new CardLayout();
        mainPanel = new JPanel(layoutManager);

        // Initialize individual views
        initializeMenuPanel();
        setupOrdersPanel();

        // Create navigation panel
        JPanel navigationPanel = new JPanel();
        JButton showMenuButton = new JButton("Menu");
        JButton showOrdersButton = new JButton("Orders");

        showMenuButton.addActionListener(e -> layoutManager.show(mainPanel, "MenuView"));
        showOrdersButton.addActionListener(e -> layoutManager.show(mainPanel, "OrderView"));

        navigationPanel.add(showMenuButton);
        navigationPanel.add(showOrdersButton);

        // Add panels to the frame
        add(navigationPanel, BorderLayout.NORTH);
        add(mainPanel, BorderLayout.CENTER);

        // Add a refresh button
        JButton refreshButton = new JButton("Update Data");
        refreshButton.addActionListener(e -> updateViewData());
        add(refreshButton, BorderLayout.SOUTH);
    }

    private void initializeMenuPanel() {
        JPanel menuPanel = new JPanel(new BorderLayout());

        // Configure the table model to define columns and non-editable cells
        menuTableModel = new DefaultTableModel(
                new Object[]{"Item Name", "Price (₹)", "Category", "Availability"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Prevent editing of any cells
            }
        };

        // Create the table and set its selection properties
        menuTable = new JTable(menuTableModel);
        menuTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION); // Allow selection of only one row

        // Add components to the menu panel
        JLabel headerLabel = new JLabel("Menu List", SwingConstants.CENTER);
        headerLabel.setFont(new Font("Arial", Font.BOLD, 14));

        menuPanel.add(headerLabel, BorderLayout.NORTH);
        menuPanel.add(new JScrollPane(menuTable), BorderLayout.CENTER);

        // Attach the panel to the main container
        mainPanel.add(menuPanel, "MenuView");
    }


    private void setupOrdersPanel() {
        JPanel ordersPanel = new JPanel(new BorderLayout());

        // Define order table structure
        ordersTableModel = new DefaultTableModel(
                new Object[]{"Order ID", "Customer Name", "Items Ordered", "Total Cost", "Order Status"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table rows non-editable
            }
        };

        ordersTable = new JTable(ordersTableModel);
        ordersTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Add components to the orders panel
        ordersPanel.add(new JLabel("Pending Orders", SwingConstants.CENTER), BorderLayout.NORTH);
        ordersPanel.add(new JScrollPane(ordersTable), BorderLayout.CENTER);

        mainPanel.add(ordersPanel, "OrderView");
    }

    public void updateViewData() {
        try {
            // Update menu data
            menuTableModel.setRowCount(0); // Clear existing rows
            Map<String, Item> menuItems = system.getMenu();
            if (menuItems != null) {
                var iterator = menuItems.values().iterator();
                while (iterator.hasNext()) {
                    Item item = iterator.next();
                    menuTableModel.addRow(new Object[]{
                            item.getName(),
                            item.getPrice(),
                            item.getCategory(),
                            item.isAvailable() ? "Available" : "Out of Stock"
                    });
                }
            }

            // Update orders data
            ordersTableModel.setRowCount(0); // Clear existing rows
            Map<String, List<Order>> orders = system.getAllOrders();
            if (orders != null) {
                var outerIterator = orders.values().iterator();
                while (outerIterator.hasNext()) {
                    List<Order> orderGroup = outerIterator.next();
                    var innerIterator = orderGroup.iterator();
                    while (innerIterator.hasNext()) {
                        Order order = innerIterator.next();
                        if ("Pending".equalsIgnoreCase(order.getStatus())) {
                            String itemsList = order.getItems().entrySet().stream()
                                    .map(e -> e.getKey().getName() + " x" + e.getValue())
                                    .collect(Collectors.joining(", "));
                            ordersTableModel.addRow(new Object[]{
                                    order.getOrderId(),
                                    order.getCustomerName(),
                                    itemsList,
                                    String.format("%.2f", order.getTotalAmount()),
                                    order.getStatus()
                            });
                        }
                    }
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Error updating data: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        ByteMeSystem system = new ByteMeSystem(); // Replace with actual implementation
        SwingUtilities.invokeLater(() -> new ByteMeGUI(system).setVisible(true));
    }
}
