package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public List<Product> getAll(){
        return productService.getAll();
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id){
        return productService.getById(id);
    }

    @PostMapping
    public Product save(@RequestBody Product product){
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Product update(@PathVariable Long id, @RequestBody Product product){
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Long id){
        productService.delete(id);
    }
}
