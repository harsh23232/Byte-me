import java.io.IOException;
import java.util.*;

public class ByteMeSystem {
    private static Map<String, Item> menu;
    private static PriorityQueue<Order> pendingOrders;
    private static Map<String, List<Order>> orderHistory;
    private static Set<String> vipCustomers;
    private static Scanner scanner = new Scanner(System.in);
    private static String currentUser = null;


    public ByteMeSystem() {
        ByteMeGUI gui = new ByteMeGUI(this); // Pass 'this' instead of 'null'
        gui.setVisible(true);

        this.menu = new TreeMap<>();
        this.pendingOrders = new PriorityQueue<>((o1, o2) -> {
            if (o1.isVIP() != o2.isVIP()) {
                return o1.isVIP() ? -1 : 1;
            }
            return o1.getOrderTime().compareTo(o2.getOrderTime());
        });
        this.orderHistory = new HashMap<>();
        this.vipCustomers = new HashSet<>();
        initializeMenu();
    }


    private void initializeMenu() {
        addMenuItem("Masala Dosa", 60.0, "South Indian");
        addMenuItem("Samosa", 15.0, "Snacks");
        addMenuItem("Butter Chicken", 180.0, "Main Course");
        addMenuItem("Paneer Tikka", 150.0, "Starters");
        addMenuItem("Cold Coffee", 40.0, "Beverages");
        addMenuItem("Veg Biryani", 120.0, "Main Course");
        addMenuItem("Gulab Jamun", 30.0, "Desserts");
        addMenuItem("Chocolate Shake", 70.0, "Beverages");
        addMenuItem("French Fries", 80.0, "Snacks");
        addMenuItem("Chicken Sandwich", 90.0, "Snacks");
    }

    public void addMenuItem(String name, double price, String category) {
        menu.put(name, new Item(name, price, category));
    }

    public void removeMenuItem(String name) {
        menu.remove(name);
        for (Order order : pendingOrders) {
            if (order.getItems().keySet().stream()
                    .anyMatch(item -> item.getName().equals(name))) {
                order.setStatus("Denied");
            }
        }
    }

    public static void displayMenu() {
        System.out.println("\n=== BYTE ME MENU ===");
        menu.forEach((name, item) -> System.out.println(item));
    }
    public Map<String, Item> getMenu() {
        return menu;
    }

    public static Item getItem(String name) {
        return menu.get(name);
    }

    public static void displayMenuByCategory(String category) {
        System.out.println("\n=== " + category.toUpperCase() + " ===");
        menu.values().stream()
                .filter(item -> item.getCategory().equals(category))
                .forEach(System.out::println);
    }

    public static void displayMenuSortedByPrice() {
        System.out.println("\nMENU SORTED BY PRICE ");
        menu.values().stream()
                .sorted(Comparator.comparingDouble(Item::getPrice))
                .forEach(System.out::println);
    }

    public static Order createOrder(String customerName) {
        return new Order(customerName, vipCustomers.contains(customerName));
    }

    public static void placeOrder(Order order) throws IOException {
        pendingOrders.add(order);
        orderHistory.computeIfAbsent(order.getCustomerName(), k -> new ArrayList<>())
                .add(order);
        ByteMeIO.saveOrder(order);
    }

    public Order processNextOrder() {
        Order order = pendingOrders.poll();
        if (order != null) {
            order.setStatus("Processing");
            System.out.println("Processing order: " + order);
        }
        return order;
    }

    public static void makeCustomerVIP(String customerName) {

        vipCustomers.add(customerName);
    }

    public static void displayOrderHistory(String customerName) {
        List<Order> history = orderHistory.get(customerName);
        if (history != null && !history.isEmpty()) {
            System.out.println("\nOrder History for " + customerName + ":");
            history.forEach(System.out::println);
        } else {
            System.out.println("No order history found!");
        }
    }

    public static void addReview(String itemName, String customerName, String comment, int rating) {
        Item item = menu.get(itemName);
        if (item != null) {
            Review review = new Review(customerName, comment, rating);
            item.addReview(review);
        }
    }

    private static void displayMainMenu() {
        System.out.println("\nBYTE ME FOOD ORDERING SYSTEM");
        System.out.println("1. Login/Register");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");
    }

    private static void displayCustomerMenu() {
        System.out.println("\n=== Welcome " + currentUser + " ===");
        System.out.println("1. View Menu");
        System.out.println("2. Place Order");
        System.out.println("3. Track Order");
        System.out.println("4. View Order History");
        System.out.println("5. Become VIP Member");
        System.out.println("6. Add Review");
        System.out.println("7. Logout");
        System.out.print("8. view userdata ");
        System.out.print("Enter your choice: ");
    }

    private static void handleViewMenu() {
        while (true) {
            System.out.println("\nMENU OPTIONS");
            System.out.println("1. View Full Menu");
            System.out.println("2. View by Category");
            System.out.println("3. View by Price (Low to High)");
            System.out.println("4. Search Item");
            System.out.println("5. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                displayMenu();
            } else if (choice == 2) {
                System.out.println("\nAvailable categories:");
                Set<String> categories = new HashSet<>();
                menu.values().forEach(item -> categories.add(item.getCategory()));
                categories.forEach(System.out::println);
                System.out.print("\nEnter category: ");
                String category = scanner.nextLine();
                displayMenuByCategory(category);
            } else if (choice == 3) {
                displayMenuSortedByPrice();
            } else if (choice == 4) {
                System.out.print("Enter item name to search: ");
                String searchTerm = scanner.nextLine().toLowerCase();
                menu.values().stream()
                        .filter(item -> item.getName().toLowerCase().contains(searchTerm))
                        .forEach(System.out::println);
            } else if (choice == 5) {
                break;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    // Add these methods to the ByteMeSystem class

    public Map<String, List<Order>> getAllOrders() {
        return orderHistory;
    }

    public Set<String> getVIPCustomers() {
        return vipCustomers;
    }

    // Optional: Add method to modify item price directly in the system
    public void updateItemPrice(String itemName, double newPrice) {
        Item item = menu.get(itemName);
        if (item != null) {
            item.setPrice(newPrice);
        }
    }

    // Optional: Add method to toggle item availability directly in the system
    public void toggleItemAvailability(String itemName) {
        Item item = menu.get(itemName);
        if (item != null) {
            item.setAvailable(!item.isAvailable());
        }
    }

    private static void handlePlaceOrder() throws IOException {
        Order order = createOrder(currentUser);

        while (true) {
            System.out.println("\n=== PLACE ORDER ===");
            displayMenu();
            System.out.println("\n1. Add Item to Cart");
            System.out.println("2. View Cart");
            System.out.println("3. Modify Cart");
            System.out.println("4. Add Special Request");
            System.out.println("5. Confirm Order");
            System.out.println("6. Cancel Order");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                System.out.print("Enter item name: ");
                String itemName = scanner.nextLine();
                Item item = menu.get(itemName);
                if (item != null && item.isAvailable()) {
                    System.out.print("Enter quantity: ");
                    int quantity = scanner.nextInt();
                    scanner.nextLine(); // consume newline
                    order.addItem(item, quantity);
                    System.out.println("Item added to cart!");
                } else {
                    System.out.println("Item not available!");
                }
            } else if (choice == 2) {
                System.out.println("\n=== YOUR CART ===");
                System.out.println(order);
            } else if (choice == 3) {
                if (order.getItems().isEmpty()) {
                    System.out.println("Cart is empty!");
                } else {
                    System.out.println("Current items in cart:");
                    order.getItems().forEach((i, q) ->
                            System.out.println(i.getName() + " - Quantity: " + q));
                    System.out.print("Enter item name to modify: ");
                    String modifyItem = scanner.nextLine();
                    Item itemToModify = menu.get(modifyItem);
                    if (itemToModify != null && order.getItems().containsKey(itemToModify)) {
                        System.out.print("Enter new quantity (0 to remove): ");
                        int newQuantity = scanner.nextInt();
                        scanner.nextLine(); // consume newline
                        if (newQuantity == 0) {
                            order.getItems().remove(itemToModify);
                        } else {
                            order.getItems().put(itemToModify, newQuantity);
                        }
                        System.out.println("Cart updated!");
                    } else {
                        System.out.println("Item not found in cart!");
                    }
                }
            } else if (choice == 4) {
                System.out.print("Enter special request: ");
                String request = scanner.nextLine();
                order.setSpecialRequests(request);
                System.out.println("Special request added!");
            } else if (choice == 5) {
                if (order.getItems().isEmpty()) {
                    System.out.println("Cart is empty! Cannot place order.");
                } else {
                    double totalAmount = order.getTotalAmount();
                    System.out.println("Your order total: ₹" + totalAmount);
                    System.out.print("Please enter the amount you would like to pay: ");
                    double amountPaid = scanner.nextDouble();
                    scanner.nextLine(); // consume newline

                    if (amountPaid >= totalAmount) {
                        placeOrder(order);
                        System.out.println("Order placed successfully!");
                        System.out.println("Change due: ₹" + (amountPaid - totalAmount));
                        return;
                    } else {
                        System.out.println("Insufficient payment. Please pay the full amount.");
                    }
                }
            } else if (choice == 6) {
                return;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }



    private static void handleTrackOrder() {
        List<Order> userOrders = orderHistory.get(currentUser);
        if (userOrders == null || userOrders.isEmpty()) {
            System.out.println("No orders found!");
            return;
        }

        System.out.println("\n=== YOUR ORDERS ===");
        userOrders.forEach(order ->
                System.out.println("Order #" + order.getOrderId() + " - Status: " + order.getStatus()));

        System.out.print("\nEnter order ID to view details (0 to go back): ");
        int orderId = scanner.nextInt();
        if (orderId != 0) {
            userOrders.stream()
                    .filter(order -> order.getOrderId() == orderId)
                    .findFirst()
                    .ifPresentOrElse(
                            System.out::println,
                            () -> System.out.println("Order not found!")
                    );
        }
    }

    private static void handleAddReview() {
        System.out.println("\n=== ADD REVIEW ===");
        List<Order> userOrders = orderHistory.get(currentUser);
        if (userOrders == null || userOrders.isEmpty()) {
            System.out.println("You haven't ordered anything yet!");
            return;
        }

        System.out.println("Items you've ordered:");
        Set<Item> orderedItems = new HashSet<>();
        userOrders.forEach(order -> orderedItems.addAll(order.getItems().keySet()));
        orderedItems.forEach(item -> System.out.println(item.getName()));

        System.out.print("\nEnter item name to review: ");
        String itemName = scanner.nextLine();
        Item item = menu.get(itemName);

        if (item != null && orderedItems.contains(item)) {
            System.out.print("Enter rating (1-5): ");
            int rating = scanner.nextInt();
            scanner.nextLine(); // consume newline
            System.out.print("Enter your review: ");
            String comment = scanner.nextLine();

            addReview(itemName, currentUser, comment, rating);
            System.out.println("Review added successfully!");
        } else {
            System.out.println("You can only review items you've ordered!");
        }
    }


    public static void main(String[] args) throws IOException {
        ByteMeSystem system = new ByteMeSystem();
        ByteMeAdminUI adminUI = new ByteMeAdminUI(system);
        Scanner scanner = new Scanner(System.in);
        Map<String, UserData> users = ByteMeIO.loadUsers();
        String username = "";
        String password = "";
        UserData userData = users.get(username);
        if (userData != null && userData.getPassword().equals(password)) {
            boolean isLoggedIn = true;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials!");
        }

        while (true) {
            System.out.println("\nWELCOME TO FOOD ORDERING SYSTEM");
            System.out.println("1. Continue as Customer");
            System.out.println("2. Continue as Admin");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int userType = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (userType == 1) { // Customer Flow
                while (true) {
                    if (currentUser == null) {
                        displayMainMenu();
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // consume newline

                        if (choice == 1) {
                            System.out.print("Enter your name: ");
                            currentUser = scanner.nextLine();
                            System.out.println("Welcome, " + currentUser + "!");
                        } else if (choice == 2) {
                            System.out.println("Thank you for using Byte Me! Goodbye!");
                            break;
                        }
                    } else {
                        displayCustomerMenu();
                        int choice = scanner.nextInt();
                        scanner.nextLine(); // consume newline

                        if (choice == 1) {
                            handleViewMenu();
                        } else if (choice == 2) {
                            handlePlaceOrder();
                        } else if (choice == 3) {
                            handleTrackOrder();
                        } else if (choice == 4) {
                            displayOrderHistory(currentUser);
                        } else if (choice == 5) {
                            makeCustomerVIP(currentUser);
                            System.out.println("Congratulations! You are now a VIP customer!");
                        } else if (choice == 6) {
                            handleAddReview();
                        } else if (choice == 7) {
                            currentUser = null;
                            System.out.println("Logged out successfully!");
                        } else if(choice == 8){
                            System.out.println(userData);
                        }
                        else {
                            System.out.println("Invalid choice!");
                        }
                    }
                }
            } else if (userType == 2) { // Admin Flow
                adminUI.start();
            } else if (userType == 3) { // Exit
                System.out.println("Thank you for using Byte Me! Goodbye!");
                scanner.close();
                return;
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }

    public boolean validateLogin(String nonexistentUser, String password) {
        return false;
    }
}