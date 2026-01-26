package com.samir.crm_order_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samir.crm_order_system.dto.LoginRequest;
import com.samir.crm_order_system.dto.RegisterRequest;
import com.samir.crm_order_system.dto.UserDTO;
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
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setup() throws Exception {
        adminToken = login("admin", "1234");
        userToken = registerAndLogin("samir_user", "1234");
    }

    private String login(String username, String password) throws Exception {
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

    private String registerAndLogin(String username, String password) throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername(username);
        request.setPassword(password);
        request.setEmail(username + "@gmail.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());


        return login(username, password);
    }

    @Test
    void testFindAll_AsUser() throws Exception {
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void testFindAll_Unauthorized() throws Exception {
        mockMvc.perform(get("/users"))
                .andExpect(status().isForbidden());
    }

    @Test
    void testGetById_AsUser() throws Exception {
        mockMvc.perform(get("/users/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk());
    }

    @Test
    void testGetById_NotFound() throws Exception {
        mockMvc.perform(get("/users/999999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void testCreateUser_AsAdmin() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("new_user");
        dto.setPassword("123456");
        dto.setEmail("new_user@gmail.com");

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists());
    }

    @Test
    void testCreateUser_AsUser_Forbidden() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("blocked_user");
        dto.setPassword("123456");
        dto.setEmail("blocked@gmail.com");

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testCreateUser_ValidationError() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("");
        dto.setPassword("1234");
        dto.setEmail("invalid@gmail.com");

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testUpdateUser_AsAdmin() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("updated_user");
        dto.setPassword("123456");
        dto.setEmail("updated@gmail.com");

        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("updated_user"));
    }

    @Test
    void testUpdateUser_AsUser_Forbidden() throws Exception {
        UserDTO dto = new UserDTO();
        dto.setUsername("blocked_update");
        dto.setPassword("123456");
        dto.setEmail("blocked@gmail.com");

        mockMvc.perform(put("/users/1")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isForbidden());
    }

    @Test
    void testDeleteUser_AsAdmin() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteUser_AsUser_Forbidden() throws Exception {
        mockMvc.perform(delete("/users/1")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }
}
