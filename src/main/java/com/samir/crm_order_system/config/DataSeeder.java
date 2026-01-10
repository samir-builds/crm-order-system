package com.samir.crm_order_system.config;

import com.samir.crm_order_system.exception.RoleNotFoundException;
import com.samir.crm_order_system.model.Role;
import com.samir.crm_order_system.model.User;
import com.samir.crm_order_system.repository.RoleRepository;
import com.samir.crm_order_system.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DataSeeder.class);

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
        seedAdminUser();
    }

    private void seedAdminUser() {
        Role adminRole = roleRepository.findByName("ROLE_ADMIN")
                .orElseThrow(() -> new RoleNotFoundException(-1L));

        userRepository.findByUsername("admin").ifPresentOrElse(
                user -> logger.info("Admin user already exists: {}", user.getUsername()),
                () -> {
                    User admin = new User();
                    admin.setUsername("admin");
                    admin.setEmail("admin@mail.com");
                    admin.setPassword(passwordEncoder.encode("12345"));
                    admin.getRoles().add(adminRole);

                    userRepository.save(admin);
                    logger.info("✅ Default admin user yaradıldı: username=admin, password=12345");
                }
        );
    }
}
