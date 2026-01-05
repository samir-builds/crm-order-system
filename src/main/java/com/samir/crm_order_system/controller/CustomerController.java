package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/customers")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    private final CustomerService customerService;

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<Page<Customer>> findAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String direction
    ) {
        logger.info("Müştəri siyahısı çağırıldı: page={}, size={}, sortBy={}, direction={}", page, size, sortBy, direction);
        Pageable pageable = PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending()
        );
        Page<Customer> customers = customerService.findAll(pageable);
        logger.debug("Tapılan müştəri sayı: {}", customers.getTotalElements());
        return ResponseEntity.ok(customers);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        logger.info("Müştəri ID ilə axtarılır: {}", id);
        Customer customer = customerService.getById(id);
        if(customer == null) {
            logger.error("Müştəri tapılmadı, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Tapılan müştəri: {}", customer);
        return ResponseEntity.ok(customer);
    }

    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<Customer> save(@Valid @RequestBody Customer customer) {
        logger.info("Yeni müştəri yaradılır: {}", customer.getName());
        Customer saved = customerService.save(customer);
        logger.info("Müştəri uğurla yaradıldı, ID: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody Customer customer) {
        logger.warn("Müştəri yenilənir, ID: {}", id);
        Customer updated = customerService.update(id, customer);
        logger.info("Müştəri uğurla yeniləndi, ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.warn("Müştəri silinmə əməliyyatı başladı, ID: {}", id);
        customerService.deleteById(id);
        logger.info("Müştəri uğurla silindi, ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
