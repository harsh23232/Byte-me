import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class Order {
    private static int orderCounter = 1;
    private int orderId;
    private String customerName;
    private Map<Item, Integer> items;
    private String status;
    private boolean isVIP;
    private String specialRequests;
    private LocalDateTime orderTime;

    public Order(String customerName, boolean isVIP) {
        this.orderId = orderCounter++;
        this.customerName = customerName;
        this.items = new HashMap<>();
        this.status = "Pending";
        this.isVIP = isVIP;
        this.orderTime = LocalDateTime.now();
    }

    // Getters and methods
    public int getOrderId() {
        return orderId;
    }
    public String getCustomerName() {
        return customerName;
    }
    public Map<Item, Integer> getItems() {
        return items;
    }
    public String getStatus() {
        return status;
    }
    public boolean isVIP() {
        return isVIP;
    }
    public LocalDateTime getOrderTime() {
        return orderTime;
    }
    public String getSpecialRequests() {
        return specialRequests;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public void setSpecialRequests(String specialRequests) {

        this.specialRequests = specialRequests;
    }

    public void addItem(Item item, int quantity) {

        items.put(item, items.getOrDefault(item, 0) + quantity);
    }

    public double getTotalAmount() {
        return items.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Order #%d (%s)\n", orderId, customerName));
        sb.append("Items:\n");
        items.forEach((item, quantity) ->
                sb.append(String.format("- %s x%d\n", item.getName(), quantity)));
        sb.append(String.format("Total: ₹%.2f\n", getTotalAmount()));
        sb.append(String.format("Status: %s\n", status));
        if (specialRequests != null && !specialRequests.isEmpty()) {
            sb.append(String.format("Special Requests: %s\n", specialRequests));
        }
        return sb.toString();
    }
}




