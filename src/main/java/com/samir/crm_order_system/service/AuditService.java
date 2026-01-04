package com.samir.crm_order_system.service;

import com.samir.crm_order_system.enums.AuditAction;
import com.samir.crm_order_system.model.AuditLog;
import com.samir.crm_order_system.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditService {

    private AuditLogRepository auditLogRepository;

    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public void log(String entityName, AuditAction action, String performedBy,
                    String details
                    ){
        AuditLog log = new AuditLog();
        log.setEntityName(entityName);
        log.setAction(action.name());
        log.setPerformedBy(performedBy);
        log.setDetails(details);
        log.setTimestamp(LocalDateTime.now());
        auditLogRepository.save(log);


    }
}
