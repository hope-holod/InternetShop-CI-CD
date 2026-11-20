package by.university.shop.service;

import by.university.shop.model.Order;
import by.university.shop.model.Payment;

public interface PaymentService {
    Payment pay(Order order);
}