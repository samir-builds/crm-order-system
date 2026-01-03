package com.samir.crm_order_system.controller;

import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.service.UserService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);


    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<User>> findAll() {
        logger.info("İstifadəçi siyahısı çağırıldı");
        List<User> users = userService.findAll();
        logger.debug("Tapılan istifadəçi sayı: {}", users.size());
        return ResponseEntity.ok(users);
    }

    @PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<User> getById(@PathVariable Long id) {
        logger.info("İstifadəçi ID ilə axtarılır: {}", id);
        User user = userService.getById(id);
        if (user == null) {
            logger.error("İstifadəçi tapılmadı, ID: {}", id);
            return ResponseEntity.notFound().build();
        }
        logger.debug("Tapılan istifadəçi: {}", user);
        return ResponseEntity.ok(user);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<User> create(@Valid @RequestBody User user) {
        logger.info("Yeni istifadəçi yaradılır: {}", user.getUsername());
        User created = userService.create(user);
        logger.info("İstifadəçi uğurla yaradıldı, ID: {}", created.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<User> update(@PathVariable Long id, @Valid @RequestBody User user) {
        logger.warn("İstifadəçi yenilənir, ID: {}", id);
        User updated = userService.update(id, user);
        logger.info("İstifadəçi uğurla yeniləndi, ID: {}", updated.getId());
        return ResponseEntity.ok(updated);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        logger.warn("İstifadəçi silinmə əməliyyatı başladı, ID: {}", id);
        userService.deleteById(id);
        logger.info("İstifadəçi uğurla silindi, ID: {}", id);
        return ResponseEntity.noContent().build();
    }
}
