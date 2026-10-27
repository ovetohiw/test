package com.phoneshop.config;

import com.phoneshop.user.Role;
import com.phoneshop.user.UserEntity;
import com.phoneshop.user.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

	private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;

	public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(ApplicationArguments args) {
		createIfMissing("admin@phoneshop.local", "Admin", Role.ADMIN);
		createIfMissing("buyer@phoneshop.local", "Buyer", Role.USER);
	}

	private void createIfMissing(String email, String fullName, Role role) {
		if (userRepository.existsByEmailIgnoreCase(email)) {
			return;
		}
		UserEntity user = new UserEntity();
		user.setEmail(email);
		user.setFullName(fullName);
		user.setRole(role);
		user.setPasswordHash(passwordEncoder.encode("Password123!"));
		userRepository.save(user);
		log.info("Seeded user {}", email);
	}
}
