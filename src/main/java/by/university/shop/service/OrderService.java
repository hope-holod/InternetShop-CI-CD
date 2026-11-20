
package by.university.shop.service;

import by.university.shop.model.Order;
import by.university.shop.model.OrderItem;
import by.university.shop.model.Payment;
import by.university.shop.model.Product;
import by.university.shop.repository.OrderRepository;
import by.university.shop.repository.ProductRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OrderService {

    private static final Logger LOGGER =
        LoggerFactory.getLogger(OrderService.class);

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;
    private final NotificationService notificationService;

    // Constructor Injection
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
     * Нельзя заказать больше, чем есть на складе.
     */
    public void addItemToOrder(Order order, int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Количество должно быть > 0"
            );
        }

        Product product = productRepository.findById(productId);

        if (product == null) {
            throw new IllegalArgumentException(
                    "Товар не найден: " + productId
            );
        }

        int alreadyInOrder = quantityInOrder(order, productId);

        if (product.getStock() < alreadyInOrder + quantity) {
            LOGGER.warn(
                    "Недостаточно товара {}: доступно {}, уже в заказе {}, запрошено {}",
                    product.getName(),
                    product.getStock(),
                    alreadyInOrder,
                    quantity
            );

            throw new IllegalStateException(
                    "Недостаточно товара \"" + product.getName() +
                    "\" на складе. Доступно: " + product.getStock() +
                    ", уже в заказе: " + alreadyInOrder +
                    ", добавляем: " + quantity
            );
        }

        order.addItem(new OrderItem(product, quantity));
    }

    /**
     * Оформление заказа: проверка остатков, списание, оплата, уведомление.
     */
    public Payment checkout(Order order) {

        LOGGER.info("Получен заказ №{}", order.getId());

        if (order.getStatus() != Order.Status.NEW) {
            throw new IllegalStateException(
                    "Заказ №" + order.getId() + " уже оформлен"
            );
        }

        if (order.getItems().isEmpty()) {
            throw new IllegalStateException(
                    "Нельзя оформить пустой заказ"
            );
        }

        // Проверяем наличие всех товаров до списания
        for (OrderItem item : order.getItems()) {
            Product product = item.getProduct();

            if (product.getStock() <
                    quantityInOrder(order, product.getId())) {

                LOGGER.warn(
                        "Недостаточно товара {} на складе",
                        product.getName()
                );

                throw new IllegalStateException(
                        "Недостаточно товара \"" +
                        product.getName() + "\" на складе"
                );
            }
        }

        try {
            // Списание товаров
            for (OrderItem item : order.getItems()) {
                Product product = item.getProduct();
                product.decreaseStock(item.getQuantity());
                productRepository.save(product);
            }

            // Оплата
            Payment payment = paymentService.pay(order);

            // Сохранение заказа
            orderRepository.save(order);

            // Уведомление покупателя
            notificationService.notify(
                    "Заказ №" + order.getId() +
                    " для " + order.getCustomer().getName() +
                    " оформлен на сумму " + order.getTotal()
            );

            LOGGER.info(
                    "Заказ №{} успешно оформлен. Сумма: {}",
                    order.getId(),
                    order.getTotal()
            );

            return payment;

        } catch (RuntimeException e) {
            LOGGER.error(
                    "Ошибка при обработке заказа №{}",
                    order.getId(),
                    e
            );

            throw e;
        }
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
