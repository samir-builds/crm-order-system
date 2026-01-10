package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.dto.CustomerDTO;
import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.service.CustomerService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
        Pageable pageable = buildPageable(page, size, sortBy, direction);
        Page<Customer> customers = customerService.findAll(pageable);
        logger.debug("Tapılan müştəri sayı: {}", customers.getTotalElements());
        return ResponseEntity.ok(customers);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Customer> getById(@PathVariable Long id) {
        logger.info("Müştəri ID ilə axtarılır: {}", id);
        Customer customer = customerService.getById(id);
        logger.debug("Tapılan müştəri: {}", customer);
        return ResponseEntity.ok(customer);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<Customer> save(@Valid @RequestBody CustomerDTO dto) {
        logger.info("Yeni müştəri yaradılır: {}", dto.getName());
        Customer saved = customerService.save(dto);
        logger.info("Müştəri uğurla yaradıldı, ID: {}", saved.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Customer> update(@PathVariable Long id, @Valid @RequestBody CustomerDTO dto) {
        logger.warn("Müştəri yenilənir, ID: {}", id);
        Customer updated = customerService.update(id, dto);
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

    private Pageable buildPageable(int page, int size, String sortBy, String direction) {
        return PageRequest.of(page, size,
                direction.equalsIgnoreCase("asc")
                        ? Sort.by(sortBy).ascending()
                        : Sort.by(sortBy).descending()
        );
    }
}
