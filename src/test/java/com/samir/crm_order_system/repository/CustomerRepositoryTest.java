package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.Customer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CustomerRepositoryTest {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private TestEntityManager entityManager;

    @Test
    void testSaveAndFindById() {
        Customer customer = new Customer();
        customer.setName("Samir");
        customer.setEmail("samir@example.com");

        Customer saved = entityManager.persistFlushFind(customer);

        Optional<Customer> found = customerRepository.findById(saved.getId());

        assertTrue(found.isPresent());
        assertEquals("Samir", found.get().getName());
    }

    @Test
    void testFindByEmail() {
        Customer customer = new Customer();
        customer.setName("Ali");
        customer.setEmail("ali@example.com");

        entityManager.persistFlushFind(customer);

        Optional<Customer> found = customerRepository.findByEmail("ali@example.com");

        assertTrue(found.isPresent());
        assertEquals("Ali", found.get().getName());
    }

    @Test
    void testDeleteCustomer() {
        Customer customer = new Customer();
        customer.setName("Test");
        customer.setEmail("test@mail.com");

        Customer saved = entityManager.persistFlushFind(customer);

        customerRepository.deleteById(saved.getId());

        assertFalse(customerRepository.findById(saved.getId()).isPresent());
    }
}
