package com.samir.crm_order_system.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CustomerDTO {

    @NotNull(message = "Name can not be null")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Email can not be null")
    @Email(message = "Email must be valid")
    private String email;

    @Size(max = 15, message = "Phone number must be max 15 characters")
    private String phone;
}
