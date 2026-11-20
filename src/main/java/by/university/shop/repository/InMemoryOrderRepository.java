package by.university.shop.repository;

import by.university.shop.model.Order;
import java.util.HashMap;
import java.util.Map;

public class InMemoryOrderRepository implements OrderRepository {
    private final Map<Integer, Order> storage = new HashMap<>();

    @Override
    public void save(Order order) {
        storage.put(order.getId(), order);
    }

    @Override
    public Order findById(int id) {
        return storage.get(id);
    }
}