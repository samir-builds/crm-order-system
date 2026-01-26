package com.samir.crm_order_system.aop;

import com.samir.crm_order_system.annotation.Audit;
import com.samir.crm_order_system.service.AuditService;
import com.samir.crm_order_system.repository.CustomerRepository;
import com.samir.crm_order_system.repository.OrderRepository;
import com.samir.crm_order_system.repository.ProductRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

@Aspect
@Component
public class AuditAspect {

    private final AuditService auditService;
    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    private static final Logger logger = LoggerFactory.getLogger(AuditAspect.class);

    public AuditAspect(AuditService auditService,
                       CustomerRepository customerRepository,
                       OrderRepository orderRepository,
                       ProductRepository productRepository) {
        this.auditService = auditService;
        this.customerRepository = customerRepository;
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    @AfterReturning(value = "@annotation(audit)", returning = "result")
    public void logAudit(JoinPoint joinPoint, Audit audit, Object result) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        String details;

        switch (audit.action()) {
            case ORDER_UPDATE -> details = handleUpdate(joinPoint, audit, result);
            case ORDER_DELETE -> details = handleDelete(joinPoint, audit);
            default -> details = String.format("%s əməliyyatı icra olundu, nəticə=%s",
                    audit.entity(), result != null ? result.toString() : "null");
        }

        auditService.log(audit.entity(), audit.action(), username, details);
    }

    private String handleUpdate(JoinPoint joinPoint, Audit audit, Object result) {
        Object[] args = joinPoint.getArgs();
        Long id = (Long) args[0];

        // old entity
        Object oldData = switch (audit.entity()) {
            case "Customer" -> customerRepository.findById(id).orElse(null);
            case "Order" -> orderRepository.findById(id).orElse(null);
            case "Product" -> productRepository.findById(id).orElse(null);
            default -> null;
        };

        // new entity (method result)
        Object newData = result;

        return (oldData != null)
                ? buildDiffDetails(audit.entity(), id, oldData, newData)
                : String.format("%s yeniləndi, ID=%d", audit.entity(), id);
    }

    private String handleDelete(JoinPoint joinPoint, Audit audit) {
        Object[] args = joinPoint.getArgs();
        Long id = (Long) args[0];

        Object deletedData = switch (audit.entity()) {
            case "Customer" -> customerRepository.findById(id).orElse(null);
            case "Order" -> orderRepository.findById(id).orElse(null);
            case "Product" -> productRepository.findById(id).orElse(null);
            default -> null;
        };

        return (deletedData != null)
                ? String.format("%s silindi, ID=%d, Detallar=%s", audit.entity(), id, deletedData)
                : String.format("%s silindi, ID=%d", audit.entity(), id);
    }

    private String buildDiffDetails(String entity, Long id, Object oldData, Object newData) {
        StringBuilder sb = new StringBuilder(String.format("%s yeniləndi, ID=%d", entity, id));

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
