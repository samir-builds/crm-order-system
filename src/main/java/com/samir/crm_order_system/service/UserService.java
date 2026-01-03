package com.samir.crm_order_system.service;

import com.samir.crm_order_system.exception.UserNotFoundException;
import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll() {
        logger.info("İstifadəçi siyahısı DB‑dən gətirilir...");
        List<User> users = userRepository.findAll();
        logger.debug("DB‑dən gətirilən istifadəçi sayı: {}", users.size());
        return users;
    }

    public User getById(Long id) {
        logger.info("İstifadəçi DB‑də ID ilə axtarılır: {}", id);
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    public User create(User user) {
        logger.info("Yeni istifadəçi DB‑yə yazılır: {}", user.getUsername());
        User saved = userRepository.save(user);
        logger.info("İstifadəçi uğurla DB‑yə yazıldı, ID: {}", saved.getId());
        return saved;
    }

    public User update(Long id, User user) {
        logger.warn("İstifadəçi DB‑də yenilənir, ID: {}", id);
        userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        user.setId(id);
        User updated = userRepository.save(user);
        logger.info("İstifadəçi uğurla yeniləndi, ID: {}", updated.getId());
        return updated;
    }

    public void deleteById(Long id) {
        logger.warn("İstifadəçi DB‑dən silinir, ID: {}", id);
        getById(id);
        userRepository.deleteById(id);
        logger.info("İstifadəçi uğurla silindi, ID: {}", id);
    }

}
