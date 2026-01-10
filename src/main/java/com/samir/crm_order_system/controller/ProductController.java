package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.dto.ProductDTO;
import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.service.ProductService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/products")
public class ProductController {

    private static final Logger logger = LoggerFactory.getLogger(ProductController.class);

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Product>> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        logger.info("Məhsul siyahısı çağırıldı: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<Product> products = productService.getAll(pageable);
        logger.debug("Tapılan məhsul sayı: {}", products.getTotalElements());
        return ResponseEntity.ok(products);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        logger.info("Məhsul ID ilə axtarılır: {}", id);
        Product product = productService.getById(id);
        logger.debug("Tapılan məhsul: {}", product);
        return ResponseEntity.ok(product);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Product> save(@Valid @RequestBody ProductDTO dto) {
        logger.info("Yeni məhsul yaradılır: {}", dto.getName());
        Product created = productService.create(dto);
        logger.info("Məhsul uğurla yaradıldı, ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(@PathVariable Long id, @Valid @RequestBody ProductDTO dto) {
        logger.info("Məhsul yenilənir, ID: {}", id);
        Product updated = productService.update(id, dto);
        logger.info("Məhsul uğurla yeniləndi, ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteById(@PathVariable Long id) {
        logger.info("Məhsul silinmə əməliyyatı başladı, ID: {}", id);
        productService.delete(id);
        logger.info("Məhsul uğurla silindi, ID: {}", id);
        return ResponseEntity.noContent().build();
    }

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        return PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending()
        );
    }
}
