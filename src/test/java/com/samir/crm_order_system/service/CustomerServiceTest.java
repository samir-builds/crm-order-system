package com.samir.crm_order_system.service;

import com.samir.crm_order_system.dto.CustomerDTO;
import com.samir.crm_order_system.exception.CustomerNotFoundException;
import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testFindAllCustomers_Pageable() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Samir");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Customer> page = new PageImpl<>(List.of(customer), pageable, 1);

        when(customerRepository.findAll(pageable)).thenReturn(page);

        Page<Customer> result = customerService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Samir", result.getContent().get(0).getName());

        verify(customerRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetById_CustomerExists() {
        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Samir");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        Customer found = customerService.getById(1L);

        assertNotNull(found);
        assertEquals("Samir", found.getName());

        verify(customerRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_CustomerNotFound() {
        when(customerRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(CustomerNotFoundException.class, () -> customerService.getById(99L));
    }

    @Test
    void testCreateCustomer() {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("Samir");
        dto.setEmail("samir@gmail.com");
        dto.setPhone("1234567890");

        Customer saved = new Customer();
        saved.setId(1L);
        saved.setName("Samir");
        saved.setEmail("samir@gmail.com");
        saved.setPhone("1234567890");

        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        Customer result = customerService.save(dto);

        assertNotNull(result.getId());
        assertEquals("Samir", result.getName());
        assertEquals("samir@gmail.com", result.getEmail());
        assertEquals("1234567890", result.getPhone());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testUpdateCustomer() {
        Customer existing = new Customer();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@mail.com");
        existing.setPhone("0000");

        CustomerDTO dto = new CustomerDTO();
        dto.setName("New Name");
        dto.setEmail("new@mail.com");
        dto.setPhone("1111");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(existing));

        Customer updated = new Customer();
        updated.setId(1L);
        updated.setName("New Name");
        updated.setEmail("new@mail.com");
        updated.setPhone("1111");

        when(customerRepository.save(any(Customer.class))).thenReturn(updated);

        Customer result = customerService.update(1L, dto);

        assertEquals("New Name", result.getName());
        assertEquals("new@mail.com", result.getEmail());
        assertEquals("1111", result.getPhone());

        verify(customerRepository, times(1)).save(any(Customer.class));
    }

    @Test
    void testDeleteCustomer() {
        Customer customer = new Customer();
        customer.setId(1L);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        doNothing().when(customerRepository).deleteById(1L);

        customerService.deleteById(1L);

        verify(customerRepository, times(1)).deleteById(1L);
    }
}
