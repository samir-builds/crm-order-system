package com.samir.crm_order_system.service;

import com.samir.crm_order_system.model.Customer;
import com.samir.crm_order_system.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    @Autowired
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public List<Customer> findAll() {
        return customerRepository.findAll();
    }

    public Customer getById(Long id) {
        return customerRepository.findById(id).orElseThrow(() -> new RuntimeException("Customer with id " + id + " not found"));
    }

    public Customer save(Customer customer) {
        return customerRepository.save(customer);
    }
    public Customer update(Long id, Customer customerDetails) {
        Customer customer = getById(id);
        customer.setName(customerDetails.getName());
        customer.setEmail(customerDetails.getEmail());
        customer.setPhone(customerDetails.getPhone());
        return customerRepository.save(customer);
    }
    public void deleteById(Long id) {
        customerRepository.deleteById(id);
    }
}
