package com.samir.crm_order_system.model;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name="audit_logs")
@Data
public class AuditLog {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private Long id;

    private String entityName;
    private String action;
    private String performedBy;
    private LocalDateTime timestamp;

    private String details;
}
