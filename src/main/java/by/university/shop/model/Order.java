package by.university.shop.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Order {
    public enum Status { NEW, PAID, CANCELLED }

    private final int id;
    private final Customer customer;
    private final List<OrderItem> items = new ArrayList<>();
    private Status status = Status.NEW;

    public Order(int id, Customer customer) {
        this.id = id;
        this.customer = customer;
    }

    public int getId() { return id; }
    public Customer getCustomer() { return customer; }
    public List<OrderItem> getItems() { return Collections.unmodifiableList(items); }
    public Status getStatus() { return status; }
    public void setStatus(Status status) { this.status = status; }

    public void addItem(OrderItem item) {
        items.add(item);
    }

    public double getTotal() {
        return items.stream().mapToDouble(OrderItem::getTotal).sum();
    }
}