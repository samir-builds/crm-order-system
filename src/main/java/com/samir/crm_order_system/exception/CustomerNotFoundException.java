package com.samir.crm_order_system.exception;

public class CustomerNotFoundException extends RuntimeException{
    public CustomerNotFoundException(Long id){
        super("Customer with id " + id + " not found.");
    }
}
