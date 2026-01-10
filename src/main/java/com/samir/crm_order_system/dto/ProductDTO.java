package com.samir.crm_order_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProductDTO {

    @NotNull(message = "Product name can not be null")
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String name;

    @NotNull(message = "Price can not be null")
    @Min(value = 0, message = "Price must be positive")
    private Double price;

    private Integer stock;
}
