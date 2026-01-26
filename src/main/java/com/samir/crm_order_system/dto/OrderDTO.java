package com.samir.crm_order_system.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderDTO {

    @NotNull(message = "Customer ID can not be null")
    private Long customerId;

    @NotNull(message = "Product ID can not be null")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}
