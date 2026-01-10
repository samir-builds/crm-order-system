package com.samir.crm_order_system.service;

import com.samir.crm_order_system.annotation.Audit;
import com.samir.crm_order_system.dto.ProductDTO;
import com.samir.crm_order_system.enums.AuditAction;
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
        return productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Audit(action = AuditAction.PRODUCT_CREATE, entity = "Product")
    public Product create(ProductDTO dto) {
        logger.info("Yeni məhsul DB‑yə yazılır: {}", dto.getName());
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        Product saved = productRepository.save(product);
        logger.info("Məhsul uğurla DB‑yə yazıldı, ID: {}", saved.getId());
        return saved;
    }

    @Audit(action = AuditAction.PRODUCT_UPDATE, entity = "Product")
    public Product update(Long id, ProductDTO dto) {
        logger.info("Məhsul DB‑də yenilənir, ID: {}", id);
        Product existing = getById(id);
        existing.setName(dto.getName());
        existing.setPrice(dto.getPrice());
        existing.setStock(dto.getStock());
        Product updated = productRepository.save(existing);
        logger.info("Məhsul uğurla yeniləndi, ID: {}", updated.getId());
        return updated;
    }

    @Audit(action = AuditAction.PRODUCT_DELETE, entity = "Product")
    public void delete(Long id) {
        logger.info("Məhsul DB‑dən silinir, ID: {}", id);
        Product product = getById(id); // yoxlama üçün
        productRepository.delete(product);
        logger.info("Məhsul uğurla silindi, ID: {}", id);
    }
}
