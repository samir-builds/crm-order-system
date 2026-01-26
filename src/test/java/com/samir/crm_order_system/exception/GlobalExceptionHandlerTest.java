package com.samir.crm_order_system.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();

    @Test
    void testHandleUserNotFound() {
        UserNotFoundException ex = new UserNotFoundException(5L);
        ResponseEntity<String> response = handler.handleUserNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("5"));
    }

    @Test
    void testHandleProductNotFound() {
        ProductNotFoundException ex = new ProductNotFoundException(10L);
        ResponseEntity<String> response = handler.handleProductNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("10"));
    }

    @Test
    void testHandleCustomerNotFound() {
        CustomerNotFoundException ex = new CustomerNotFoundException(3L);
        ResponseEntity<String> response = handler.handleCustomerNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("3"));
    }

    @Test
    void testHandleOrderNotFound() {
        OrderNotFoundException ex = new OrderNotFoundException(99L);
        ResponseEntity<String> response = handler.handleOrderNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("99"));
    }

    @Test
    void testHandleRoleNotFound() {
        RoleNotFoundException ex = new RoleNotFoundException(7L);
        ResponseEntity<String> response = handler.handleRoleNotFound(ex);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertTrue(response.getBody().contains("7"));
    }
}
