class Review {
    private String customerName;
    private String comment;
    private int rating;

    public Review(String customerName, String comment, int rating) {
        this.customerName = customerName;
        this.comment = comment;
        this.rating = rating;
    }

    @Override
    public String toString() {
        return String.format("%s rated %d/5: %s", customerName, rating, comment);
    }
}