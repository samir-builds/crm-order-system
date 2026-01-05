package com.samir.crm_order_system.service;

import com.samir.crm_order_system.annotation.Audit;
import com.samir.crm_order_system.enums.AuditAction;
import com.samir.crm_order_system.exception.CustomerNotFoundException;
import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

    private final CustomerRepository customerRepository;

    private final AuditService auditService;

    public CustomerService(CustomerRepository customerRepository, AuditService auditService) {
        this.customerRepository = customerRepository;
        this.auditService = auditService;
    }

    public Page<Customer> findAll(Pageable pageable) {
        logger.info("Müştəri siyahısı DB‑dən gətirilir...");
        Page<Customer> customers = customerRepository.findAll(pageable);
        logger.debug("DB‑dən gətirilən müştəri sayı: {}", customers.getTotalElements());
        return customers;
    }

    public Customer getById(Long id) {
        logger.info("Müştəri DB‑də ID ilə axtarılır: {}", id);
        return customerRepository.findById(id).orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @Audit(action = AuditAction.CUSTOMER_CREATE, entity = "Customer")
    public Customer save(Customer customer) {
        logger.info("Yeni müştəri DB‑yə yazılır: {}", customer.getName());
        Customer saved = customerRepository.save(customer);
        logger.info("Müştəri uğurla DB‑yə yazıldı, ID: {}", saved.getId());
        return saved;
    }

    @Audit(action = AuditAction.CUSTOMER_UPDATE, entity = "Customer")
    public Customer update(Long id, Customer customerDetails) {
        logger.warn("Müştəri DB‑də yenilənir, ID: {}", id);
        Customer customer = getById(id);
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        Customer updated = customerRepository.save(customer);
        logger.info("Müştəri uğurla yeniləndi, ID: {}", updated.getId());
        return updated;
    }

    @Audit(action = AuditAction.CUSTOMER_DELETE, entity = "Customer")
    public void deleteById(Long id) {
        logger.warn("Müştəri DB‑dən silinir, ID: {}", id);
        getById(id);
        customerRepository.deleteById(id);
        logger.info("Müştəri uğurla silindi, ID: {}", id);
    }
}
