package com.samir.crm_order_system.service;

import com.samir.crm_order_system.dto.OrderDTO;
import com.samir.crm_order_system.exception.OrderNotFoundException;
import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.model.Order;
import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.repository.CustomerRepository;
import com.samir.crm_order_system.repository.OrderRepository;
import com.samir.crm_order_system.repository.ProductRepository;
import com.samir.crm_order_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setup() throws Exception {
        MockitoAnnotations.openMocks(this);

        var adminEmailField = OrderService.class.getDeclaredField("adminEmail");
        adminEmailField.setAccessible(true);
        adminEmailField.set(orderService, "admin@test.com");
    }

    @Test
    void testFindAllOrders_Pageable() {
        Order order = new Order();
        order.setId(1L);

        Pageable pageable = PageRequest.of(0, 10);
        Page<Order> page = new PageImpl<>(List.of(order), pageable, 1);

        when(orderRepository.findAll(pageable)).thenReturn(page);

        Page<Order> result = orderService.findAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals(1L, result.getContent().get(0).getId());

        verify(orderRepository, times(1)).findAll(pageable);
    }

    @Test
    void testFindById_OrderExists() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        Order found = orderService.findById(1L);

        assertNotNull(found);
        assertEquals(1L, found.getId());
        verify(orderRepository, times(1)).findById(1L);
    }

    @Test
    void testFindById_OrderNotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(OrderNotFoundException.class, () -> orderService.findById(99L));
    }

    @Test
    void testSaveOrder() {
        OrderDTO dto = new OrderDTO();
        dto.setCustomerId(1L);
        dto.setProductId(2L);
        dto.setQuantity(3);

        Customer customer = new Customer();
        customer.setId(1L);
        customer.setName("Samir");

        Product product = new Product();
        product.setId(2L);
        product.setPrice(100.0);

        User user = new User();
        user.setId(5L);
        user.setUsername("admin");

        Order saved = new Order();
        saved.setId(10L);
        saved.setCustomer(customer);
        saved.setProduct(product);
        saved.setCreatedBy(user);
        saved.setQuantity(3);
        saved.setTotalPrice(300.0);

        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(userRepository.findByUsername("admin")).thenReturn(Optional.of(user));
        when(orderRepository.save(any(Order.class))).thenReturn(saved);

        Order result = orderService.save(dto, "admin");

        assertNotNull(result.getId());
        assertEquals(300.0, result.getTotalPrice());
        assertEquals("Samir", result.getCustomer().getName());

        verify(orderRepository, times(1)).save(any(Order.class));
        verify(emailService, times(1)).sendSimple(anyString(), anyString(), anyString());
    }

    @Test
    void testUpdateOrder() {
        Order existing = new Order();
        existing.setId(1L);

        Customer customer = new Customer();
        customer.setId(1L);

        Product product = new Product();
        product.setId(2L);
        product.setPrice(50.0);

        OrderDTO dto = new OrderDTO();
        dto.setCustomerId(1L);
        dto.setProductId(2L);
        dto.setQuantity(4);

        Order updated = new Order();
        updated.setId(1L);
        updated.setCustomer(customer);
        updated.setProduct(product);
        updated.setQuantity(4);
        updated.setTotalPrice(200.0);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
        when(productRepository.findById(2L)).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenReturn(updated);

        Order result = orderService.update(1L, dto);

        assertEquals(4, result.getQuantity());
        assertEquals(200.0, result.getTotalPrice());

        verify(orderRepository, times(1)).save(any(Order.class));
    }

    @Test
    void testDeleteOrder() {
        Order order = new Order();
        order.setId(1L);

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));
        doNothing().when(orderRepository).deleteById(1L);

        orderService.delete(1L);

        verify(orderRepository, times(1)).deleteById(1L);
    }
}
