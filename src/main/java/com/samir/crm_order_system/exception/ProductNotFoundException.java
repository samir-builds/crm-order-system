package com.samir.crm_order_system.exception;

public class ProductNotFoundException extends RuntimeException{
    public ProductNotFoundException(Long id){
        super("Product not found with id " + id);
    }
}
