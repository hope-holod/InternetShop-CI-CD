package by.university.shop.service;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import by.university.shop.model.Customer;
import by.university.shop.model.Order;
import by.university.shop.model.Payment;
import by.university.shop.model.Product;
import by.university.shop.repository.InMemoryOrderRepository;
import by.university.shop.repository.InMemoryProductRepository;
import by.university.shop.repository.ProductRepository;

class OrderServiceTest {

    private ProductRepository productRepository;
    private List<String> sentMessages;   // сюда тестовое "уведомление" складывает сообщения
    private OrderService orderService;
    private Order order;

    @BeforeEach
    void setUp() {
        productRepository = new InMemoryProductRepository();
        sentMessages = new ArrayList<>();
        // Тестовая реализация зависимости (заглушка): ничего не отправляет, просто запоминает
        NotificationService fakeNotifier = message -> sentMessages.add(message);

        orderService = new OrderService(productRepository, new InMemoryOrderRepository(),
                new SimplePaymentService(), fakeNotifier);

        productRepository.save(new Product(1, "Ноутбук", 1200.0, 5));
        order = new Order(1, new Customer(1, "Иван"));
    }

    // Тест 1 — успешный сценарий: заказ оформлен, остаток уменьшился, уведомление отправлено
    @Test
    void checkoutSuccess() {
        orderService.addItemToOrder(order, 1, 2);
        Payment payment = orderService.checkout(order);

        assertEquals(Payment.Status.COMPLETED, payment.getStatus());
        assertEquals(Order.Status.PAID, order.getStatus());
        assertEquals(3, productRepository.findById(1).getStock());
        assertEquals(1, sentMessages.size());
    }

    // Тест 2 — ошибочный сценарий: один и тот же заказ нельзя оформить повторно
    @Test
    void cannotCheckoutSameOrderTwice() {
        orderService.addItemToOrder(order, 1, 2);
        orderService.checkout(order);

        assertThrows(IllegalStateException.class, () -> orderService.checkout(order));
        assertEquals(3, productRepository.findById(1).getStock()); // второй раз не списали
    }

    // Тест 3 — индивидуальное задание: нельзя заказать больше, чем есть на складе
    @Test
    void cannotOrderMoreThanStock() {
        assertThrows(IllegalStateException.class, () -> orderService.addItemToOrder(order, 1, 6));

        orderService.addItemToOrder(order, 1, 4);
        // 4 уже в заказе + ещё 4 = 8 > 5 — тоже нельзя
        assertThrows(IllegalStateException.class, () -> orderService.addItemToOrder(order, 1, 4));
        assertEquals(5, productRepository.findById(1).getStock()); // склад не тронут
    }

    // Тест 4 — ошибка оплаты и журналирование ERROR
    @Test
    void checkoutPaymentFailure() {
        OrderService failingOrderService = new OrderService(
                productRepository,
                new InMemoryOrderRepository(),
                new FailingPaymentService(),
                message -> sentMessages.add(message)
        );

        failingOrderService.addItemToOrder(order, 1, 1);

        assertThrows(
                IllegalStateException.class,
                () -> failingOrderService.checkout(order)
        );
    }

}
