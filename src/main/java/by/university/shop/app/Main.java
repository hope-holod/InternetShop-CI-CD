
package by.university.shop.app;

import java.util.Properties;

import by.university.shop.model.Customer;
import by.university.shop.model.Order;
import by.university.shop.model.Product;
import by.university.shop.repository.InMemoryOrderRepository;
import by.university.shop.repository.InMemoryProductRepository;
import by.university.shop.repository.OrderRepository;
import by.university.shop.repository.ProductRepository;
import by.university.shop.service.NotificationService;
import by.university.shop.service.OrderService;
import by.university.shop.service.PaymentService;
import by.university.shop.service.SimplePaymentService;

public class Main {
    public static void main(String[] args) throws Exception {
        Properties config = new Properties();
        config.load(Main.class.getResourceAsStream("/application.properties"));
        System.out.println("Профиль: " + config.getProperty("app.env"));

        // DI — собираем зависимости вручную
        ProductRepository productRepo = new InMemoryProductRepository();
        OrderRepository orderRepo = new InMemoryOrderRepository();
        PaymentService paymentService = new SimplePaymentService();
        NotificationService notifier =
                msg -> System.out.println("[NOTIFY] " + msg);

        OrderService orderService =
                new OrderService(productRepo, orderRepo, paymentService, notifier);

        // Товары
        productRepo.save(new Product(1, "Ноутбук", 1200.0, 5));
        productRepo.save(new Product(2, "Мышь", 25.0, 10));

        Customer customer = new Customer(1, "Иван");
        Order order = new Order(1, customer);

        // Успешное добавление
        orderService.addItemToOrder(order, 1, 2);
        orderService.addItemToOrder(order, 2, 3);
        System.out.println("Сумма заказа: " + order.getTotal());

        // Индивидуальное задание — проверка остатков
        try {
            orderService.addItemToOrder(order, 1, 100);
        } catch (IllegalStateException e) {
            System.out.println("[ERROR] " + e.getMessage());
        }

        // Оформление
        orderService.checkout(order);
        System.out.println(
                "Остаток ноутбуков: " + productRepo.findById(1).getStock()
        );
    }
    
}
