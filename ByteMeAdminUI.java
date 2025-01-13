import java.util.*;

public class ByteMeAdminUI {
    private ByteMeSystem system;
    private Scanner scanner;
    private String adminUsername;
    private String adminPassword;
    private boolean isLoggedIn;

    public ByteMeAdminUI(ByteMeSystem system) {
        this.system = system;
        this.scanner = new Scanner(System.in);
        this.adminUsername = "admin"; // In a real system, these would be stored securely
        this.adminPassword = "admin123";
        this.isLoggedIn = false;
    }

    public void start() {
        while (true) {
            if (!isLoggedIn) {
                displayLoginMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline

                if (choice == 1) {
                    handleLogin();
                } else if (choice == 2) {
                    System.out.println("Exiting admin interface...");
                    break;
                }
            } else {
                displayAdminMenu();
                int choice = scanner.nextInt();
                scanner.nextLine(); // consume newline
                if (!handleAdminChoice(choice)) {
                    break;
                }
            }
        }
    }

    private void displayLoginMenu() {
        System.out.println("\n ADMIN LOGIN");
        System.out.println("1. Login");
        System.out.println("2. Exit");
        System.out.print("Enter your choice: ");
    }

    private void handleLogin() {
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        if (username.equals(adminUsername) && password.equals(adminPassword)) {
            isLoggedIn = true;
            System.out.println("Login successful!");
        } else {
            System.out.println("Invalid credentials!");
        }
    }

    private void displayAdminMenu() {
        System.out.println("\nBYTE ME ADMIN PANEL ");
        System.out.println("1. Manage Menu");
        System.out.println("2. Manage Orders");
        System.out.println("3. View Customer Reviews");
        System.out.println("4. View VIP Customers");
        System.out.println("5. Logout");
        System.out.print("Enter your choice: ");
    }

    private boolean handleAdminChoice(int choice) {
        if (choice == 1) {
            handleMenuManagement();
        } else if (choice == 2) {
            handleOrderManagement();
        } else if (choice == 3) {
            viewCustomerReviews();
        } else if (choice == 4) {
            viewVIPCustomers();
        } else if (choice == 5) {
            handleLogout();
            return true;
        } else {
            System.out.println("Invalid choice!");
        }
        return true;
    }


    private void handleMenuManagement() {
        while (true) {
            System.out.println("\nMENU MANAGEMENT");
            System.out.println("1. View Current Menu");
            System.out.println("2. Add New Item");
            System.out.println("3. Remove Item");
            System.out.println("4. Modify Item");
            System.out.println("5. Toggle Item Availability");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                displayMenu();
            } else if (choice == 2) {
                handleAddItem();
            } else if (choice == 3) {
                handleRemoveItem();
            } else if (choice == 4) {
                handleModifyItem();
            } else if (choice == 5) {
                handleToggleItemAvailability();
            } else if (choice == 6) {
                return; // Exit to main menu
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }


    private void displayMenu() {
        System.out.println("\nCURRENT MENU ");
        Map<String, Item> menu = system.getMenu();

        if (menu.isEmpty()) {
            System.out.println("The menu is currently empty!");
        } else {
            for (String itemName : menu.keySet()) {
                Item item = menu.get(itemName);
                System.out.println(item);
            }
        }
    }




    private void handleAddItem() {
        System.out.println("\nADD NEW ITEM ");
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();

        if (system.getItem(name) != null) {
            System.out.println("Item already exists!");
            return;
        }

        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // consume newline

        System.out.print("Enter category: ");
        String category = scanner.nextLine();

        system.addMenuItem(name, price, category);
        System.out.println("Item added successfully!");
    }

    private void handleRemoveItem() {
        System.out.println("\nREMOVE ITEM");
        displayMenu();
        System.out.print("Enter item name to remove: ");
        String name = scanner.nextLine();

        if (system.getItem(name) == null) {
            System.out.println("Item not found!");
        } else {
            system.removeMenuItem(name);
            System.out.println("Item removed successfully!");

        }
    }

    private void handleModifyItem() {
        System.out.println("\nMODIFY ITEM ");
        displayMenu();
        System.out.print("Enter item name to modify: ");
        String name = scanner.nextLine();

        Item item = system.getItem(name);
        if (item == null) {
            System.out.println("Item not found!");
        } else {
            System.out.println("Current price: ₹" + item.getPrice());
            System.out.print("Enter new price (0 to keep current): ");
            double newPrice = scanner.nextDouble();
            if (newPrice > 0) {
                item.setPrice(newPrice);
                System.out.println("Price updated successfully!");
            }

        }
    }

    private void handleToggleItemAvailability() {
        System.out.println("\nTOGGLE ITEM AVAILABILITY");
        displayMenu();
        System.out.print("Enter item name to toggle availability: ");
        String name = scanner.nextLine();

        Item item = system.getItem(name);
        if (item == null) {
            System.out.println("Item not found!");
        } else {
            item.setAvailable(!item.isAvailable());
            System.out.println("Item availability updated to: " +
                    (item.isAvailable() ? "Available" : "Not Available"));

        }
    }

    private void handleOrderManagement() {
        while (true) {
            System.out.println("\nORDER MANAGEMENT ");
            System.out.println("1. View Pending Orders");
            System.out.println("2. Process Next Order");
            System.out.println("3. View All Orders");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            if (choice == 1) {
                viewPendingOrders();
            } else if (choice == 2) {
                processNextOrder();
            } else if (choice == 3) {
                viewAllOrders();
            } else if (choice == 4) {
                return; // Exit the method to go back to the main menu
            } else {
                System.out.println("Invalid choice!");
            }
        }
    }


    private void viewPendingOrders() {
        Order nextOrder = system.processNextOrder(); // Peek without removing
        if (nextOrder == null) {
            System.out.println("No pending orders!");
        } else {
            System.out.println("\nPENDING ORDERS");
            System.out.println(nextOrder);

        }
    }

    private void processNextOrder() {
        Order order = system.processNextOrder();
        if (order == null) {
            System.out.println("No orders to process!");

        } else {
            System.out.println("\nPROCESSING ORDER");
            System.out.println(order);
            order.setStatus("Processing");
            System.out.println("Order status updated to: Processing");

        }
    }

    private void viewAllOrders() {
        System.out.println("\nALL ORDERS ");
        boolean hasOrders = false; // Renaming the flag for clarity

        // Retrieve all orders from the system
        Map<String, List<Order>> allOrders = system.getAllOrders();

        // Loop through each entry in the map
        for (Map.Entry<String, List<Order>> entry : allOrders.entrySet()) {
            String customer = entry.getKey();
            List<Order> orders = entry.getValue();

            // Check if the customer has orders
            if (!orders.isEmpty()) {
                System.out.println("\nCustomer: " + customer);
                for (Order order : orders) {
                    System.out.println(order); // Print each order for the customer
                }
                hasOrders = true; // Set flag to true if orders are found
            }
        }

        // If no orders were found, inform the user
        if (!hasOrders) {
            System.out.println("No orders found!");
        }
    }

    private void viewCustomerReviews() {
        System.out.println("\n=== CUSTOMER REVIEWS ===");
        boolean hasReviews = false; // Renamed for clarity

        // Loop through each item in the menu
        for (Item item : system.getMenu().values()) {
            List<Review> reviews = item.getReviews();

            // Check if the item has reviews
            if (!reviews.isEmpty()) {
                System.out.println("\nItem: " + item.getName());
                for (Review review : reviews) {
                    System.out.println(review); // Print each review for the item
                }
                hasReviews = true; // Set flag to true if reviews are found
            }
        }

        // If no reviews were found, inform the user
        if (!hasReviews) {
            System.out.println("No reviews found!");
        }
    }


    private void viewVIPCustomers() {
        Set<String> vipCustomers = system.getVIPCustomers();
        System.out.println("\nVIP CUSTOMERS");
        if (vipCustomers.isEmpty()) {
            System.out.println("No VIP customers found!");
        } else {
            vipCustomers.forEach(System.out::println);
        }
    }

    private void handleLogout() {
        isLoggedIn = false;
        System.out.println("Logged out successfully!");
    }

    public static void main(String[] args) {
        ByteMeSystem system = new ByteMeSystem();
        ByteMeAdminUI adminUI = new ByteMeAdminUI(system);
        adminUI.start();
    }
}