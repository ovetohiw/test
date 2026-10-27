package com.phoneshop.auth;

import com.phoneshop.user.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public final class AuthDtos {

	private AuthDtos() {
	}

	public record RegisterRequest(
			@Email @NotBlank String email,
			@NotBlank @Size(min = 8, max = 72) String password,
			@NotBlank @Size(max = 255) String fullName
	) {
	}

	public record LoginRequest(
			@Email @NotBlank String email,
			@NotBlank String password
	) {
	}

	public record UserResponse(Long id, String email, String fullName, Role role) {
	}
}
