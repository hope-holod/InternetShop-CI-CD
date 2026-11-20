package by.university.shop.service;

import by.university.shop.model.Order;
import by.university.shop.model.Payment;

public class FailingPaymentService implements PaymentService {

    @Override
    public Payment pay(Order order) {
        throw new IllegalStateException(
                "Тестовая ошибка: платёж отклонён"
        );
    }
}
