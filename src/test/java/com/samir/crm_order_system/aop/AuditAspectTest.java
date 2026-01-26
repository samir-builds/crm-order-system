package com.samir.crm_order_system.aop;

import com.samir.crm_order_system.annotation.Audit;
import com.samir.crm_order_system.enums.AuditAction;
import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.repository.CustomerRepository;
import com.samir.crm_order_system.service.AuditService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.Method;
import java.util.Optional;

import static org.mockito.Mockito.*;

public class AuditAspectTest {

    private AuditAspect aspect;
    private AuditService auditService;
    private CustomerRepository customerRepository;

    @BeforeEach
    void setup() {
        auditService = mock(AuditService.class);
        customerRepository = mock(CustomerRepository.class);

        aspect = new AuditAspect(
                auditService,
                customerRepository,
                null,
                null
        );

        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("samir", null)
        );
    }

    @Audit(entity = "Customer", action = AuditAction.ORDER_UPDATE)
    public Customer dummyUpdate(Long id) {
        Customer c = new Customer();
        c.setId(id);
        c.setName("NewName");
        return c;
    }

    @Test
    void testUpdate_WithOldData() throws Exception {
        Customer oldC = new Customer();
        oldC.setId(1L);
        oldC.setName("OldName");

        when(customerRepository.findById(1L)).thenReturn(Optional.of(oldC));

        Method method = this.getClass().getMethod("dummyUpdate", Long.class);
        Audit audit = method.getAnnotation(Audit.class);

        JoinPoint jp = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        when(jp.getArgs()).thenReturn(new Object[]{1L});
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        Customer newC = dummyUpdate(1L);

        aspect.logAudit(jp, audit, newC);

        verify(auditService, times(1))
                .log(eq("Customer"), eq(AuditAction.ORDER_UPDATE), eq("samir"), contains("OldName → NewName"));
    }

    @Test
    void testUpdate_NoOldData() throws Exception {
        when(customerRepository.findById(1L)).thenReturn(Optional.empty());

        Method method = this.getClass().getMethod("dummyUpdate", Long.class);
        Audit audit = method.getAnnotation(Audit.class);

        JoinPoint jp = mock(JoinPoint.class);
        MethodSignature signature = mock(MethodSignature.class);

        when(jp.getArgs()).thenReturn(new Object[]{1L});
        when(jp.getSignature()).thenReturn(signature);
        when(signature.getMethod()).thenReturn(method);

        Customer newC = dummyUpdate(1L);

        aspect.logAudit(jp, audit, newC);

        verify(auditService, times(1))
                .log(eq("Customer"), eq(AuditAction.ORDER_UPDATE), eq("samir"), contains("yeniləndi, ID=1"));
    }
}
