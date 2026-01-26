package com.samir.crm_order_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samir.crm_order_system.dto.LoginRequest;
import com.samir.crm_order_system.dto.ProductDTO;
import com.samir.crm_order_system.dto.RegisterRequest;
import com.samir.crm_order_system.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

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
                .andDo(result -> {
                    System.out.println("REGISTER STATUS = " + result.getResponse().getStatus());
                    System.out.println("REGISTER BODY = " + result.getResponse().getContentAsString());
                });

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

    @Test
    void testGetAllProducts_AsUser() throws Exception {
        mockMvc.perform(get("/products")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetProductById_AsUser() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("iPhone");
        dto.setPrice(1500.0);
        dto.setStock(65);

        String createResponse = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/products/" + productId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.price").value(1500.0))
                .andExpect(jsonPath("$.stock").value(65));
    }

    @Test
    void testCreateProduct_AsAdmin() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("iPhone");
        dto.setPrice(1500.0);
        dto.setStock(65);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("iPhone"))
                .andExpect(jsonPath("$.price").value(1500.0))
                .andExpect(jsonPath("$.stock").value(65));
    }

    @Test
    void testCreateProduct_AsUser_Forbidden() throws Exception {
        ProductDTO dto = new ProductDTO();
        dto.setName("Samsung TV");
        dto.setPrice(999.0);
        dto.setStock(3);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateProduct_AsAdmin() throws Exception {
        ProductDTO createDto = new ProductDTO();
        createDto.setName("Old Laptop");
        createDto.setPrice(1000.0);
        createDto.setStock(5);

        String createResponse = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(createResponse).get("id").asLong();

        ProductDTO updateDto = new ProductDTO();
        updateDto.setName("Updated Laptop");
        updateDto.setPrice(1200.0);
        updateDto.setStock(8);

        mockMvc.perform(put("/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Laptop"))
                .andExpect(jsonPath("$.price").value(1200.0))
                .andExpect(jsonPath("$.stock").value(8));
    }

    @Test
    void testUpdateProduct_AsUser_Forbidden() throws Exception {
        ProductDTO createDto = new ProductDTO();
        createDto.setName("Old Laptop");
        createDto.setPrice(1000.0);
        createDto.setStock(5);

        String createResponse = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(createResponse).get("id").asLong();


        ProductDTO updateDto = new ProductDTO();
        updateDto.setName("Updated Monitor");
        updateDto.setPrice(350.0);
        updateDto.setStock(5);

        mockMvc.perform(put("/products/" + productId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteProduct_AsAdmin() throws Exception {
        ProductDTO createDto = new ProductDTO();
        createDto.setName("Old Laptop");
        createDto.setPrice(1000.0);
        createDto.setStock(5);

        String createResponse = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/products/" + productId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteProduct_AsUser_Forbidden() throws Exception {
        ProductDTO createDto = new ProductDTO();
        createDto.setName("Keyboard");
        createDto.setPrice(80.0);
        createDto.setStock(20);

        String createResponse = mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long productId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/products/" + productId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllProducts_PaginationAndSorting() throws Exception {
        ProductDTO dto1 = new ProductDTO();
        dto1.setName("AirPods");
        dto1.setPrice(200.0);
        dto1.setStock(50);

        ProductDTO dto2 = new ProductDTO();
        dto2.setName("MacBook");
        dto2.setPrice(2500.0);
        dto2.setStock(10);

        ProductDTO dto3 = new ProductDTO();
        dto3.setName("iPad");
        dto3.setPrice(1200.0);
        dto3.setStock(20);

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/products?page=0&size=10&sortBy=name&direction=asc")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].name").value("AirPods"))
                .andExpect(jsonPath("$.content[1].name").value("MacBook"))
                .andExpect(jsonPath("$.content[2].name").value("iPad"));
    }


}
