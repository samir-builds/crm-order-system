package com.samir.crm_order_system.service;

import com.samir.crm_order_system.dto.ProductDTO;
import com.samir.crm_order_system.exception.ProductNotFoundException;
import com.samir.crm_order_system.model.Product;
import com.samir.crm_order_system.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllProducts_Pageable() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        Pageable pageable = PageRequest.of(0, 10);
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findAll(pageable)).thenReturn(page);

        Page<Product> result = productService.getAll(pageable);

        assertEquals(1, result.getTotalElements());
        assertEquals("Test Product", result.getContent().get(0).getName());
        verify(productRepository, times(1)).findAll(pageable);
    }

    @Test
    void testGetById_ProductExists() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Test Product");

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        Product found = productService.getById(1L);

        assertNotNull(found);
        assertEquals("Test Product", found.getName());
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void testGetById_ProductNotFound() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(ProductNotFoundException.class, () -> productService.getById(1L));
    }

    @Test
    void testCreateProduct() {
        ProductDTO dto = new ProductDTO();
        dto.setName("Test Product");
        dto.setPrice(800.0);
        dto.setStock(50);

        Product saved = new Product();
        saved.setId(1L);
        saved.setName("Test Product");
        saved.setPrice(800.0);
        saved.setStock(50);

        when(productRepository.save(any(Product.class))).thenReturn(saved);

        Product result = productService.create(dto);

        assertNotNull(result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals(800.0, result.getPrice());
        assertEquals(50, result.getStock());

        verify(productRepository, times(1)).save(any(Product.class));

    }

    @Test
    void testUpdateProduct() {
        Product existing = new Product();
        existing.setId(1L);
        existing.setName("Old Phone");
        existing.setPrice(500.0);
        existing.setStock(10);

        ProductDTO dto = new ProductDTO();
        dto.setName("New Phone");
        dto.setPrice(900.0);
        dto.setStock(20);

        when(productRepository.findById(1L)).thenReturn(Optional.of(existing));

        Product updated = new Product();
        updated.setId(1L);
        updated.setName("New Phone");
        updated.setPrice(900.0);
        updated.setStock(20);

        when(productRepository.save(any(Product.class))).thenReturn(updated);

        Product result = productService.update(1L, dto);

        assertEquals("New Phone", result.getName());
        assertEquals(900.0, result.getPrice());
        assertEquals(20, result.getStock());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void testDeleteProduct() {
        Product product = new Product();
        product.setId(1L);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        doNothing().when(productRepository).delete(product);

        productService.delete(1L);

        verify(productRepository, times(1)).delete(product);
    }

}
