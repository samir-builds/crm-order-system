package com.samir.crm_order_system.dto;

import com.samir.crm_order_system.enums.OrderStatus;
import lombok.Data;

@Data
public class StatusUpdateRequest {
    private OrderStatus status;
}
