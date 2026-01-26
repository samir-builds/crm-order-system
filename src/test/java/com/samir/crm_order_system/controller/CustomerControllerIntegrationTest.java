package com.samir.crm_order_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samir.crm_order_system.dto.CustomerDTO;
import com.samir.crm_order_system.dto.LoginRequest;
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
public class CustomerControllerIntegrationTest {
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

    @Test
    void testGetAllCustomers_AsUser() throws Exception {
        mockMvc.perform(get("/customers")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetCustomerById_AsUser() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("123456789");

        String createResponse = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(get("/customers/" + customerId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phone").value("123456789"));
    }

    @Test
    void testCreateCustomer_AsAdmin() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("John Doe");
        dto.setEmail("john@example.com");
        dto.setPhone("123456789");

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.phone").value("123456789"));
    }

    @Test
    void testCreateCustomer_AsUser_Forbidden() throws Exception {
        CustomerDTO dto = new CustomerDTO();
        dto.setName("Blocked User");
        dto.setEmail("blocked@example.com");
        dto.setPhone("999999");

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testUpdateCustomer_AsAdmin() throws Exception {
        CustomerDTO createDto = new CustomerDTO();
        createDto.setName("Old Name");
        createDto.setEmail("old@example.com");
        createDto.setPhone("111111");

        String createResponse = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        CustomerDTO updateDto = new CustomerDTO();
        updateDto.setName("Updated Name");
        updateDto.setEmail("updated@example.com");
        updateDto.setPhone("222222");

        mockMvc.perform(put("/customers/" + customerId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"))
                .andExpect(jsonPath("$.email").value("updated@example.com"))
                .andExpect(jsonPath("$.phone").value("222222"));
    }

    @Test
    void testUpdateCustomer_AsUser_Forbidden() throws Exception {
        CustomerDTO createDto = new CustomerDTO();
        createDto.setName("Original Name");
        createDto.setEmail("original@example.com");
        createDto.setPhone("111111");

        String createResponse = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        CustomerDTO updateDto = new CustomerDTO();
        updateDto.setName("Hacked Name");
        updateDto.setEmail("hacked@example.com");
        updateDto.setPhone("222222");

        mockMvc.perform(put("/customers/" + customerId)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteCustomer_AsAdmin() throws Exception {
        CustomerDTO createDto = new CustomerDTO();
        createDto.setName("Delete Me");
        createDto.setEmail("delete@example.com");
        createDto.setPhone("777777");

        String createResponse = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/customers/" + customerId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteCustomer_AsUser_Forbidden() throws Exception {
        CustomerDTO createDto = new CustomerDTO();
        createDto.setName("Protected Customer");
        createDto.setEmail("protected@example.com");
        createDto.setPhone("123123");

        String createResponse = mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        Long customerId = objectMapper.readTree(createResponse).get("id").asLong();

        mockMvc.perform(delete("/customers/" + customerId)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetAllCustomers_PaginationAndSorting() throws Exception {
        CustomerDTO dto1 = new CustomerDTO();
        dto1.setName("Alice");
        dto1.setEmail("alice@example.com");
        dto1.setPhone("111111");

        CustomerDTO dto2 = new CustomerDTO();
        dto2.setName("Bob");
        dto2.setEmail("bob@example.com");
        dto2.setPhone("222222");

        CustomerDTO dto3 = new CustomerDTO();
        dto3.setName("Charlie");
        dto3.setEmail("charlie@example.com");
        dto3.setPhone("333333");

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto2)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/customers")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto3)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/customers?page=0&size=10&sortBy=name&direction=asc")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(3))
                .andExpect(jsonPath("$.content[0].name").value("Alice"))
                .andExpect(jsonPath("$.content[1].name").value("Bob"))
                .andExpect(jsonPath("$.content[2].name").value("Charlie"));
    }


}
