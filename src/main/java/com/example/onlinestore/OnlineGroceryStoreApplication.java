package com.example.onlinestore;

import com.example.onlinestore.entity.Role;
import com.example.onlinestore.entity.User;
import com.example.onlinestore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.*;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class OnlineGroceryStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(OnlineGroceryStoreApplication.class, args);
	}

	@Bean
	public CommandLineRunner initAdmin(
			@Autowired UserRepository userRepo,
			@Autowired PasswordEncoder encoder
	) {
		return args -> {
			String adminUsername = "admin";
			if (userRepo.findByUsername(adminUsername).isEmpty()) {
				User admin = User.builder()
						.username(adminUsername)
						.email("admin@foodmarket.local")
						.password(encoder.encode("adminpass"))
						.role(Role.ROLE_ADMIN)
						.build();
				userRepo.save(admin);
			}
		};
	}
}
