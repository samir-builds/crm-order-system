package com.samir.crm_order_system.service;

import com.samir.crm_order_system.exception.ProductNotFoundException;
import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public Page<Product> getAll(Pageable pageable){
        return productRepository.findAll(pageable);
    }

    public Product getById(Long id){
       return productRepository.findById(id).orElse(null);
    }

    public Product create(Product product){
        return  productRepository.save(product);
    }

    public Product update(Long id, Product product){
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setId(id);
        return productRepository.save(product);
    }

    public void delete(Long id){
        productRepository.deleteById(id);
    }
}
