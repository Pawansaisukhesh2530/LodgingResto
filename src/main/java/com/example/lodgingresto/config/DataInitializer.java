package com.example.lodgingresto.config;

import com.example.lodgingresto.model.Role;
import com.example.lodgingresto.model.User;
import com.example.lodgingresto.repository.RoleRepository;
import com.example.lodgingresto.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Set;

@Configuration
public class DataInitializer {

    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);

    @Bean
    CommandLineRunner init(RoleRepository roleRepository, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            // Create roles
            Role adminRole = roleRepository.findByName("ADMIN").orElseGet(() -> roleRepository.save(new Role("ADMIN")));
            Role receptionistRole = roleRepository.findByName("RECEPTIONIST").orElseGet(() -> roleRepository.save(new Role("RECEPTIONIST")));
            Role restaurantRole = roleRepository.findByName("RESTAURANT_STAFF").orElseGet(() -> roleRepository.save(new Role("RESTAURANT_STAFF")));
            Role managerRole = roleRepository.findByName("MANAGER").orElseGet(() -> roleRepository.save(new Role("MANAGER")));

            // Create admin user if not present
            if (!userRepository.existsByUsername("admin")) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("123456"));
                admin.setEnabled(true);
                Set<Role> roles = new HashSet<>();
                roles.add(adminRole);
                roles.add(managerRole);
                admin.setRoles(roles);
                userRepository.save(admin);
                logger.info("Seeded default admin user (username=admin)");
            }
        };
    }
}

