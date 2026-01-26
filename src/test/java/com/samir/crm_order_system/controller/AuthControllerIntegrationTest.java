package com.samir.crm_order_system.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.samir.crm_order_system.dto.LoginRequest;
import com.samir.crm_order_system.dto.RegisterRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testRegisterSuccess() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("samir");
        request.setPassword("1234");
        request.setEmail("samir@gmail.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testRegister_UsernameExists() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setUsername("samir");
        request.setPassword("1234");
        request.setEmail("samir@gmail.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Username already exists"));

    }

    @Test
    void testLoginSuccess() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("samir");
        register.setPassword("1234");
        register.setEmail("samir@gmail.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setUsername("samir");
        login.setPassword("1234");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void testLoginWrongPassword() throws Exception {
        RegisterRequest register = new RegisterRequest();
        register.setUsername("samir");
        register.setPassword("1234");
        register.setEmail("samir@gmail.com");

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(register)))
                .andExpect(status().isOk());

        LoginRequest login = new LoginRequest();
        login.setUsername("samir");
        login.setPassword("wrong");

        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(login)))
                .andExpect(status().isForbidden());
    }


}
