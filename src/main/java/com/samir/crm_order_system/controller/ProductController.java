package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    @Autowired
    private ProductService productService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Product>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ){
        logger.info("Məhsul siyahısı çağırıldı: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending()
        );
        Page<Product> products = productService.getAll(pageable);
        logger.debug("Tapılan məhsul sayı: {}", products.getTotalElements());
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){
        logger.info("Məhsul ID ilə axtarılır: {}", id);
        Product product = productService.getById(id);
        if(product == null) {
            logger.error("Məhsul tapılmadı, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Tapılan məhsul: {}", product);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> save(@Valid @RequestBody Product product){
        logger.info("Yeni məhsul yaradılır: {}", product.getName());
        Product created = productService.create(product);
        logger.info("Məhsul uğurla yaradıldı, ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody Product product){
        logger.warn("Məhsul yenilənir, ID: {}", id);
        Product updated = productService.update(id, product);
        logger.info("Məhsul uğurla yeniləndi, ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id){
        logger.warn("Məhsul silinmə əməliyyatı başladı, ID: {}", id);
        productService.delete(id);
        logger.info("Məhsul uğurla silindi, ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
