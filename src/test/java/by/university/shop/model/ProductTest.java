package by.university.shop.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

class ProductTest {
    @Test
    void createsValidProduct() {
        Product product = new Product(10, "Мышь", 25.0, 3);
        assertEquals("Мышь", product.getName());
        assertEquals(25.0, product.getPrice());
    }

    @Test
    void rejectsBlankProductName() {
        assertThrows(IllegalArgumentException.class, () -> new Product(10, "  ", 25.0, 3));
    }

    @Test
    void rejectsNegativePrice() {
        assertThrows(IllegalArgumentException.class, () -> new Product(10, "Мышь", -1.0, 3));
    }

    @Test
    void rejectsZeroQuantity() {
        Product product = new Product(10, "Мышь", 25.0, 3);

        assertThrows(
            IllegalStateException.class,
            () -> product.decreaseStock(0)
        );
    }

    @Test
    void rejectsNegativeQuantity() {
        Product product = new Product(10, "Мышь", 25.0, 3);

        assertThrows(
            IllegalArgumentException.class,
            () -> product.decreaseStock(-1)
        );
    }
}
