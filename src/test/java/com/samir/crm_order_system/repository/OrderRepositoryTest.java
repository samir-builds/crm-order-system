package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.model.Order;
import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class OrderRepositoryTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testFindByCustomerId() {
        Customer customer = new Customer();
        customer.setName("Samir");
        customer.setEmail("samir@mail.com");
        entityManager.persist(customer);

        Product product = new Product();
        product.setName("iPhone 15");
        product.setPrice(2000.0);
        product.setStock(10);
        entityManager.persist(product);

        User user = new User();
        user.setUsername("order_test_user");
        user.setPassword("123456");
        user.setEmail("admin@mail.com");
        entityManager.persist(user);

        Order order = new Order();
        order.setCustomer(customer);
        order.setProduct(product);
        order.setCreatedBy(user);
        order.setQuantity(2);
        order.setTotalPrice(4000.0);

        entityManager.persist(order);

        List<Order> orders = orderRepository.findByCustomerId(customer.getId());

        assertEquals(1, orders.size());
        assertEquals(4000.0, orders.get(0).getTotalPrice());
        assertEquals("order_test_user", orders.get(0).getCreatedBy().getUsername());
    }
}
