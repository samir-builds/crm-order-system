package com.samir.crm_order_system.annotation;

import com.samir.crm_order_system.enums.AuditAction;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Audit {
    AuditAction action();
    String entity();
}


