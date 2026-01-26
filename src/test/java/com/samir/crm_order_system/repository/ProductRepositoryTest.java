package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.Product;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSearchByName() {
        Product p = new Product();
        p.setName("iPhone 15");
        p.setPrice(2000.0);

        entityManager.persistFlushFind(p);

        List<Product> results = productRepository.searchByName("iPhone");

        assertEquals(1, results.size());
        assertEquals("iPhone 15", results.get(0).getName());
    }
}
