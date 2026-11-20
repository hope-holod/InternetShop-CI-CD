package by.university.shop.repository;

import by.university.shop.model.Order;

public interface OrderRepository {
    void save(Order order);
    Order findById(int id);
}