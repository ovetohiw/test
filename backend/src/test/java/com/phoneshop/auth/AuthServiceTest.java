package com.phoneshop.auth;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.phoneshop.auth.AuthDtos.RegisterRequest;
import com.phoneshop.common.ApiException;
import com.phoneshop.security.JwtService;
import com.phoneshop.user.Role;
import com.phoneshop.user.UserEntity;
import com.phoneshop.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

	@Mock
	UserRepository userRepository;
	@Mock
	PasswordEncoder passwordEncoder;
	@Mock
	AuthenticationManager authenticationManager;
	@Mock
	JwtService jwtService;

	AuthService authService;

	@BeforeEach
	void setUp() {
		authService = new AuthService(userRepository, passwordEncoder, authenticationManager, jwtService);
	}

	@Test
	void registerCreatesUserAndToken() {
		when(userRepository.existsByEmailIgnoreCase("a@b.com")).thenReturn(false);
		when(passwordEncoder.encode("Password123!")).thenReturn("hash");
		when(jwtService.createToken(any(), any(), any())).thenReturn("token");
		when(userRepository.save(any(UserEntity.class))).thenAnswer(invocation -> {
			UserEntity user = invocation.getArgument(0);
			user.setId(1L);
			return user;
		});

		var result = authService.register(new RegisterRequest("a@b.com", "Password123!", "Ann"));

		assertThat(result.token()).isEqualTo("token");
		assertThat(result.user().email()).isEqualTo("a@b.com");
		assertThat(result.user().role()).isEqualTo(Role.USER);

		ArgumentCaptor<UserEntity> captor = ArgumentCaptor.forClass(UserEntity.class);
		org.mockito.Mockito.verify(userRepository).save(captor.capture());
		assertThat(captor.getValue().getPasswordHash()).isEqualTo("hash");
	}

	@Test
	void registerRejectsDuplicateEmail() {
		when(userRepository.existsByEmailIgnoreCase("a@b.com")).thenReturn(true);
		assertThatThrownBy(() -> authService.register(new RegisterRequest("a@b.com", "Password123!", "Ann")))
				.isInstanceOf(ApiException.class);
	}
}
