package com.phoneshop.auth;

import com.phoneshop.auth.AuthDtos.LoginRequest;
import com.phoneshop.auth.AuthDtos.RegisterRequest;
import com.phoneshop.auth.AuthDtos.UserResponse;
import com.phoneshop.common.ApiException;
import com.phoneshop.security.JwtService;
import com.phoneshop.security.ShopUserDetails;
import com.phoneshop.user.Role;
import com.phoneshop.user.UserEntity;
import com.phoneshop.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtService jwtService;

	public AuthService(
			UserRepository userRepository,
			PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager,
			JwtService jwtService
	) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
	}

	@Transactional
	public AuthResult register(RegisterRequest request) {
		if (userRepository.existsByEmailIgnoreCase(request.email())) {
			throw new ApiException(HttpStatus.CONFLICT, "Email already registered");
		}
		UserEntity user = new UserEntity();
		user.setEmail(request.email().trim().toLowerCase());
		user.setFullName(request.fullName().trim());
		user.setRole(Role.USER);
		user.setPasswordHash(passwordEncoder.encode(request.password()));
		userRepository.save(user);
		String token = jwtService.createToken(user.getId(), user.getEmail(), user.getRole());
		return new AuthResult(token, toResponse(user));
	}

	public AuthResult login(LoginRequest request) {
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.email().trim().toLowerCase(), request.password())
		);
		ShopUserDetails details = (ShopUserDetails) authentication.getPrincipal();
		String token = jwtService.createToken(details.getId(), details.getUsername(), details.getRole());
		return new AuthResult(token, new UserResponse(details.getId(), details.getUsername(), details.getFullName(), details.getRole()));
	}

	@Transactional(readOnly = true)
	public UserResponse me(Long userId) {
		UserEntity user = userRepository.findById(userId)
				.orElseThrow(() -> new ApiException(HttpStatus.UNAUTHORIZED, "User not found"));
		return toResponse(user);
	}

	private UserResponse toResponse(UserEntity user) {
		return new UserResponse(user.getId(), user.getEmail(), user.getFullName(), user.getRole());
	}

	public record AuthResult(String token, UserResponse user) {
	}
}
