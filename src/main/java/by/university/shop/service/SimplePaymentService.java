package by.university.shop.service;

import by.university.shop.model.Order;
import by.university.shop.model.Payment;

public class SimplePaymentService implements PaymentService {
    private int nextId = 1;

    @Override
    public Payment pay(Order order) {
        Payment payment = new Payment(nextId++, order);
        payment.setStatus(Payment.Status.COMPLETED);
        order.setStatus(Order.Status.PAID);
        return payment;
    }
}