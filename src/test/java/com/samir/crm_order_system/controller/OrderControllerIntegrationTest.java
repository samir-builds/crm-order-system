package com.samir.crm_order_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samir.crm_order_system.dto.CustomerDTO;
import com.samir.crm_order_system.dto.LoginRequest;
import com.samir.crm_order_system.dto.OrderDTO;
import com.samir.crm_order_system.dto.ProductDTO;
import com.samir.crm_order_system.dto.RegisterRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class OrderControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setup() throws Exception {
        adminToken = login("admin", "1234");
        userToken = registerAndLogin("samir", "1234");
    }


    private String login(String username, String password) throws Exception {
        LoginRequest login = new LoginRequest();
        login.setUsername(username);
        login.setPassword(password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andDo(result -> {
                    System.out.println("LOGIN STATUS = " + result.getResponse().getStatus());
                    System.out.println("LOGIN BODY = " + result.getResponse().getContentAsString());
                })

                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    private String registerAndLogin(String username, String password) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(username + "@gmail.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setUsername(username);
        login.setPassword(password);

        String response = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("token").asText();
    }

    private Long createCustomer(String name, String email, String phone) throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setName(name);
        dto.setEmail(email);
        dto.setPhone(phone);

        String response = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createProduct(String name, double price, int stock) throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName(name);
        dto.setPrice(price);
        dto.setStock(stock);

        String response = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    private Long createOrder(Long customerId, Long productId, int quantity) throws Exception {
        OrderDTO dto = new OrderDTO();
        dto.setCustomerId(customerId);
        dto.setProductId(productId);
        dto.setQuantity(quantity);

        String response = mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        return objectMapper.readTree(response).get("id").asLong();
    }

    @Test
    void testGetAllOrders() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);

        createOrder(customerId, productId, 1);
        createOrder(customerId, productId, 2);
        createOrder(customerId, productId, 3);

        mockMvc.perform(get("/orders")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }


    @Test
    void testGetOrderById() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);
        Long orderId = createOrder(customerId, productId, 2);

        mockMvc.perform(get("/orders/" + orderId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId));
    }

    @Test
    void testCreateOrder_AsAdmin() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customerId);
        order.setProductId(productId);
        order.setQuantity(2);

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.totalPrice").value(3000.0));
    }

    @Test
    void testCreateOrder_AsUser_Forbidden() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);

        OrderDTO order = new OrderDTO();
        order.setCustomerId(customerId);
        order.setProductId(productId);
        order.setQuantity(2);

        mockMvc.perform(post("/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isForbidden());
    }


    @Test
    void testUpdateOrder_AsAdmin() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);
        Long orderId = createOrder(customerId, productId, 2);

        OrderDTO update = new OrderDTO();
        update.setCustomerId(customerId);
        update.setProductId(productId);
        update.setQuantity(3);

        mockMvc.perform(put("/orders/" + orderId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderId))
                .andExpect(jsonPath("$.quantity").value(3))
                .andExpect(jsonPath("$.totalPrice").value(4500.0));
    }

    @Test
    void testUpdateOrder_AsUser_Forbidden() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);
        Long orderId = createOrder(customerId, productId, 2);

        OrderDTO update = new OrderDTO();
        update.setCustomerId(customerId);
        update.setProductId(productId);
        update.setQuantity(5);

        mockMvc.perform(put("/orders/" + orderId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());
    }


    @Test
    void testDeleteOrder_AsAdmin() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);
        Long orderId = createOrder(customerId, productId, 2);

        mockMvc.perform(delete("/orders/" + orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteOrder_AsUser_Forbidden() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);
        Long orderId = createOrder(customerId, productId, 2);

        mockMvc.perform(delete("/orders/" + orderId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllOrders_Pagination() throws Exception {
        Long customerId = createCustomer("John", "john@example.com", "12345");
        Long productId = createProduct("iPhone", 1500.0, 10);

        createOrder(customerId, productId, 1);
        createOrder(customerId, productId, 2);
        createOrder(customerId, productId, 3);

        mockMvc.perform(get("/orders?page=0&size=10")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3));
    }
}
