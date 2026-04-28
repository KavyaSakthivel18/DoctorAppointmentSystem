package com.hospitalmanagement.app.config;

import com.hospitalmanagement.app.entity.Role;
import com.hospitalmanagement.app.entity.User;
import com.hospitalmanagement.app.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByEmail("admin@hospital.com").isEmpty()) {
                User admin = new User();
                admin.setName("Super Admin");
                admin.setEmail("admin@hospital.com");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole(Role.ADMIN);
                userRepository.save(admin);
            }
        };
    }
}
