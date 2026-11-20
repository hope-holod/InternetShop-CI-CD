package by.university.shop.model;

public class OrderItem {
    private final Product product;
    private final int quantity;

    public OrderItem(Product product, int quantity) {
        if (quantity <= 0) throw new IllegalArgumentException("Количество должно быть > 0");
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public double getTotal() { return product.getPrice() * quantity; }
}