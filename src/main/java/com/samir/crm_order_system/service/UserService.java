package com.samir.crm_order_system.service;

import com.samir.crm_order_system.annotation.Audit;
import com.samir.crm_order_system.dto.UserDTO;
import com.samir.crm_order_system.enums.AuditAction;
import com.samir.crm_order_system.enums.RoleName;
import com.samir.crm_order_system.exception.RoleNotFoundException;
import com.samir.crm_order_system.exception.UserNotFoundException;
import com.samir.crm_order_system.model.Role;
import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.repository.RoleRepository;
import com.samir.crm_order_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public List<User> findAll() {
        logger.info("İstifadəçi siyahısı DB‑dən gətirilir...");
        List<User> users = userRepository.findAll();
        logger.debug("DB‑dən gətirilən istifadəçi sayı: {}", users.size());
        return users;
    }

    public User getById(Long id) {
        logger.info("İstifadəçi DB‑də ID ilə axtarılır: {}", id);
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));
    }

    @Audit(action = AuditAction.USER_CREATE, entity = "User")
    public User create(UserDTO dto) {
        logger.info("Yeni istifadəçi DB‑yə yazılır: {}", dto.getUsername());

        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        assignRoles(user, dto.getRoleIds());

        User saved = userRepository.save(user);
        logger.info("İstifadəçi uğurla DB‑yə yazıldı, ID: {}", saved.getId());
        return saved;
    }

    @Audit(action = AuditAction.USER_UPDATE, entity = "User")
    public User update(Long id, UserDTO dto) {
        logger.info("İstifadəçi DB‑də yenilənir, ID: {}", id);

        User existing = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        existing.setUsername(dto.getUsername());
        existing.setEmail(dto.getEmail());
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            existing.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        existing.getRoles().clear();
        assignRoles(existing, dto.getRoleIds());

        User updated = userRepository.save(existing);
        logger.info("İstifadəçi uğurla yeniləndi, ID: {}", updated.getId());
        return updated;
    }

    @Audit(action = AuditAction.USER_DELETE, entity = "User")
    public void deleteById(Long id) {
        logger.info("İstifadəçi DB‑dən silinir, ID: {}", id);
        getById(id); // yoxlama üçün
        userRepository.deleteById(id);
        logger.info("İstifadəçi uğurla silindi, ID: {}", id);
    }

    private void assignRoles(User user, Set<Long> roleIds) {
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                Role role = roleRepository.findById(roleId)
                        .orElseThrow(() -> new RoleNotFoundException(roleId));
                user.getRoles().add(role);
            }
        } else {
            Role defaultRole = roleRepository.findByName(RoleName.ROLE_USER)
                    .orElseThrow(() -> new RoleNotFoundException(-1L));
            user.getRoles().add(defaultRole);
        }
    }
}
