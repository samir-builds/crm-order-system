package com.samir.crm_order_system.exception;

public class RoleNotFoundException extends RuntimeException{
    public RoleNotFoundException(Long id){
        super("Role with id " + id + " not found.");
    }
}
