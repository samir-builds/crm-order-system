package com.samir.crm_order_system.repository;

import com.samir.crm_order_system.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

}
