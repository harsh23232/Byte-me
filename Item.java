import java.util.ArrayList;
import java.util.List;

class Item {
    private String name;
    private double price;
    private String category;
    private boolean available;
    private List<Review> reviews;

    public Item(String name, double price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = true;
        this.reviews = new ArrayList<>();
    }

    // Getters and setters
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public String getCategory() {
        return category;
    }
    public boolean isAvailable() {
        return available;
    }

    public List<Review> getReviews() { return reviews; }

    public void setPrice(double price) {
        this.price = price;
    }
    public void setAvailable(boolean available) {
        this.available = available;
    }
    public void addReview(Review review) {
        reviews.add(review);
    }

    @Override
    public String toString() {
        return String.format("%s - ₹%.2f (%s) %s",
                name, price, category, available ? "Available" : "Not Available");
    }
}