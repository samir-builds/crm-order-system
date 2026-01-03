package com.samir.crm_order_system.service;

import com.samir.crm_order_system.exception.ProductNotFoundException;
import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private static final Logger logger = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Page<Product> getAll(Pageable pageable) {
        logger.info("Məhsul siyahısı DB‑dən gətirilir...");
        Page<Product> products = productRepository.findAll(pageable);
        logger.debug("DB‑dən gətirilən məhsul sayı: {}", products.getTotalElements());
        return products;
    }

    public Product getById(Long id) {
        logger.info("Məhsul DB‑də ID ilə axtarılır: {}", id);
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    public Product create(Product product) {
        logger.info("Yeni məhsul DB‑yə yazılır: {}", product.getName());
        Product saved = productRepository.save(product);
        logger.info("Məhsul uğurla DB‑yə yazıldı, ID: {}", saved.getId());
        return saved;
    }

    public Product update(Long id, Product product) {
        logger.warn("Məhsul DB‑də yenilənir, ID: {}", id);
        productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
        product.setId(id);
        Product updated = productRepository.save(product);
        logger.info("Məhsul uğurla yeniləndi, ID: {}", updated.getId());
        return  updated;
    }

    public void delete(Long id) {
        logger.warn("Məhsul DB‑dən silinir, ID: {}", id);
        getById(id);
        productRepository.deleteById(id);
        logger.info("Məhsul uğurla silindi, ID: {}", id);
    }
}
