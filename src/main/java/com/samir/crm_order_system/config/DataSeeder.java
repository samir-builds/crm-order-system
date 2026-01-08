package com.samir.crm_order_system.config;

import com.samir.crm_order_system.exception.RoleNotFoundException;
import com.samir.crm_order_system.model.Role;
import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.repository.RoleRepository;
import com.samir.crm_order_system.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository,
                      RoleRepository roleRepository,
                      PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Əvvəlcə ROLE_ADMIN DB-də varmı yoxla
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RoleNotFoundException(-1L));

        // Əgər admin user yoxdursa, yarat
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setEmail("admin@mail.com");
            admin.setPassword(passwordEncoder.encode("12345")); // test üçün sadə parol
            admin.getRoles().add(adminRole);

            userRepository.save(admin);
            System.out.println("✅ Default admin user yaradıldı: username=admin, password=12345");
        }
    }
}
