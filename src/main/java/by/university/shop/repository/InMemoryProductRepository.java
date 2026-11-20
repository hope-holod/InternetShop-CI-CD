package by.university.shop.repository;

import by.university.shop.model.Product;
import java.util.*;

public class InMemoryProductRepository implements ProductRepository {
    private final Map<Integer, Product> storage = new HashMap<>();

    @Override
    public void save(Product product) {
        storage.put(product.getId(), product);
    }

    @Override
    public Product findById(int id) {
        return storage.get(id);
    }

    @Override
    public List<Product> findAll() {
        return new ArrayList<>(storage.values());
    }
}