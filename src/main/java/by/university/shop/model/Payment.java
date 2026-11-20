package by.university.shop.model;

public class Payment {
    public enum Status { PENDING, COMPLETED, FAILED }

    private final int id;
    private final Order order;
    private final double amount;
    private Status status = Status.PENDING;

    public Payment(int id, Order order) {
        this.id = id;
        this.order = order;
        this.amount = order.getTotal();
    }

    public int getId() { return id; }
    public Order getOrder() { return order; }
    public double getAmount() { return amount; }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }
}