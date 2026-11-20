package by.university.shop.service;

import by.university.shop.model.*;
import by.university.shop.repository.*;

public class OrderService {
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    // Constructor Injection: все зависимости приходят снаружи, сервис сам их не создаёт
    public OrderService(ProductRepository productRepository,
                        OrderRepository orderRepository,
                        PaymentService paymentService,
                        NotificationService notificationService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.paymentService = paymentService;
        this.notificationService = notificationService;
    }

    /**
     * Добавить товар в заказ.
     * ИНДИВИДУАЛЬНОЕ ЗАДАНИЕ: нельзя заказать больше, чем есть на складе
     * (учитывается и то, что этот товар уже лежит в заказе).
     */
    public void addItemToOrder(Order order, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Количество должно быть > 0");
        }

        Product product = productRepository.findById(productId);
        if (product == null) {
            throw new IllegalArgumentException("Товар не найден: " + productId);
        }

        int alreadyInOrder = quantityInOrder(order, productId);
        if (product.getStock() < alreadyInOrder + quantity) {
            throw new IllegalStateException(
                "Недостаточно товара \"" + product.getName() +
                "\" на складе. Доступно: " + product.getStock() +
                ", уже в заказе: " + alreadyInOrder + ", добавляем: " + quantity
            );
        }

        order.addItem(new OrderItem(product, quantity));
    }

    /**
     * Оформление заказа: проверка остатков, списание, оплата, уведомление.
     */
    public Payment checkout(Order order) {
        if (order.getStatus() != Order.Status.NEW) {
            throw new IllegalStateException("Заказ №" + order.getId() + " уже оформлен");
        }
        if (order.getItems().isEmpty()) {
            throw new IllegalStateException("Нельзя оформить пустой заказ");
        }

        // Сначала проверяем ВСЕ товары (пока склад мог измениться), и только потом списываем
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            if (product.getStock() < quantityInOrder(order, product.getId())) {
                throw new IllegalStateException(
                    "Недостаточно товара \"" + product.getName() + "\" на складе"
                );
            }
        }

        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();
            product.decreaseStock(item.getQuantity());
            productRepository.save(product);
        }

        Payment payment = paymentService.pay(order);
        orderRepository.save(order);

        notificationService.notify(
            "Заказ №" + order.getId() + " для " + order.getCustomer().getName() +
            " оформлен на сумму " + order.getTotal()
        );

        return payment;
    }

    // Сколько штук этого товара уже лежит в заказе
    private int quantityInOrder(Order order, int productId) {
        int sum = 0;
        for (OrderItem item : order.getItems()) {
            if (item.getProduct().getId() == productId) {
                sum += item.getQuantity();
            }
        }
        return sum;
    }
}
