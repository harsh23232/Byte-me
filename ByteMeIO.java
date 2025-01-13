import java.io.*;
import java.util.*;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

public class ByteMeIO {
    private static final String USER_FILE = "users.txt";
    private static final String ORDER_HISTORY_FILE = "order_history.txt";

    public static void saveUser(String username, String password, boolean isVIP) throws IOException {
        FileWriter fileWriter = new FileWriter(USER_FILE, true);
        PrintWriter writer = new PrintWriter(fileWriter);
        writer.println(username + "," + password + "," + isVIP);
        writer.close();
    }

    public static Map<String, UserData> loadUsers() throws IOException {
        Map<String, UserData> users = new HashMap<>();
        FileReader fileReader = new FileReader(USER_FILE);
        BufferedReader reader = new BufferedReader(fileReader);

        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) {
                users.put(parts[0], new UserData(parts[0], parts[1], Boolean.parseBoolean(parts[2])));
            }
        }
        reader.close();
        return users;
    }

    public static UserData getUserDetails(String username) throws IOException {
        Map<String, UserData> users = loadUsers();
        return users.get(username);
    }

    public static void saveOrder(Order order) throws IOException {
        FileWriter fileWriter = new FileWriter(ORDER_HISTORY_FILE, true);
        PrintWriter writer = new PrintWriter(fileWriter);
        writer.println(serializeOrder(order));
        writer.close();
    }

    public static List<Order> loadOrderHistory(String username) throws IOException {
        List<Order> orders = new ArrayList<>();
        FileReader fileReader = new FileReader(ORDER_HISTORY_FILE);
        BufferedReader reader = new BufferedReader(fileReader);

        String line;
        while ((line = reader.readLine()) != null) {
            Order order = deserializeOrder(line);
            if (order != null && order.getCustomerName().equals(username)) {
                orders.add(order);
            }
        }
        reader.close();
        return orders;
    }

    private static String serializeOrder(Order order) {
        StringBuilder sb = new StringBuilder();
        sb.append(order.getOrderId()).append(",");
        sb.append(order.getCustomerName()).append(",");
        sb.append(order.getStatus()).append(",");
        sb.append(order.isVIP()).append(",");
        sb.append(order.getOrderTime()).append(",");
        sb.append(order.getItems().entrySet().stream()
                .map(e -> e.getKey().getName() + ":" + e.getValue())
                .collect(Collectors.joining(";")));
        return sb.toString();
    }

    private static Order deserializeOrder(String line) {
        String[] parts = line.split(",");
        Order order = new Order(parts[1], Boolean.parseBoolean(parts[3]));
        order.setStatus(parts[2]);
        String[] items = parts[5].split(";");
        for (String item : items) {
            String[] itemParts = item.split(":");
            Item menuItem = ByteMeSystem.getItem(itemParts[0]);
            if (menuItem != null) {
                order.addItem(menuItem, Integer.parseInt(itemParts[1]));
            }
        }
        return order;
    }
}

class UserData implements Serializable {
    private String username;
    private String password;
    private boolean isVIP;

    public UserData(String username, String password, boolean isVIP) {
        this.username = username;
        this.password = password;
        this.isVIP = isVIP;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public boolean isVIP() {
        return isVIP;
    }

    @Override
    public String toString() {
        return "Username: " + username + "\nVIP Status: " + (isVIP ? "Yes" : "No");
    }
}
