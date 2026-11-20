
package by.university.shop.model;

public class Product {
    private final int id;
    private final String name;
    private final double price;
    private int stock;

    public Product(int id, String name, double price, int stock) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "Название товара не может быть пустым"
            );
        }
        if (price < 0) {
            throw new IllegalArgumentException(
                    "Цена товара не может быть отрицательной"
            );
        }
        if (stock < 0) {
            throw new IllegalArgumentException(
                    "Остаток не может быть отрицательным"
            );
        }

        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public void decreaseStock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Количество товара должно быть положительным"
            );
        }

        if (quantity > stock) {
            throw new IllegalStateException(
                    "Недостаточно товара на складе"
            );
        }

        stock -= quantity;
    }
}
