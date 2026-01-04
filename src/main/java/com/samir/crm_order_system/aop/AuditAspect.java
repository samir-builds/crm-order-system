package com.samir.crm_order_system.aop;

import com.samir.crm_order_system.annotation.Audit;
import com.samir.crm_order_system.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class AuditAspect {
    private final AuditService auditService;
    private final ApplicationContext applicationContext;
    private final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    public AuditAspect(AuditService auditService, ApplicationContext applicationContext) {
        this.auditService = auditService;
        this.applicationContext = applicationContext;
    }

    @AfterReturning(value = "@annotation(audit)", returning = "result")
    public void logAudit(JoinPoint joinPoint, Audit audit, Object result) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        String details = "";

        if (audit.action().name().endsWith("UPDATE")) {
            Object[] args = joinPoint.getArgs();
            Long id = (Long) args[0];
            Object newData = args[1];
            String repoBeanName = audit.entity().toLowerCase() + "Repository";
            JpaRepository repository = (JpaRepository) applicationContext.getBean(repoBeanName);
            Object oldData = repository.findById(id).orElse(null);

            if (oldData != null) {
                details = buildDiffDetails(audit.entity(), id, oldData, newData);
            } else {
                details = String.format("%s yeniləndi, ID=%d", audit.entity(), id);
            }
        } else {
            details = String.format("%s əməliyyatı icra olundu, nəticə=%s", audit.entity(), result != null ? result.toString() : "null");
        }
        if (audit.action().name().endsWith("DELETE")) {
            Object[] args = joinPoint.getArgs();
            Long id = (Long) args[0];
            String repoBeanName = audit.entity().toLowerCase() + "Repository";
            JpaRepository repository = (JpaRepository) applicationContext.getBean(repoBeanName);
            Object deletedData = repository.findById(id).orElse(null);
            if (deletedData != null) {
                details = String.format("%s silindi, ID=%d, Detallar=%s", audit.entity(), id, deletedData.toString());
            } else {
                details = String.format("%s silindi, ID=%d", audit.entity(), id);
            }
        }

        auditService.log(audit.entity(), audit.action(), username, details);
    }

    private String buildDiffDetails(String entity, Long id, Object oldData, Object newData) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s yeniləndi, ID=%d", entity, id));
        for (Field field : oldData.getClass().getDeclaredFields()) {
            field.setAccessible(true);
            try {
                Object oldVal = field.get(oldData);
                Object newVal = field.get(newData);
                if (oldVal != null && !oldVal.equals(newVal)) {
                    sb.append(String.format(", %s: %s → %s", field.getName(), oldVal, newVal));
                }
            } catch (IllegalAccessException e) {
                logger.warn("Field access error in AuditAspect", e);
            }
        }
        return sb.toString();
    }
}
