package com.phoneshop.auth;

import com.phoneshop.auth.AuthDtos.LoginRequest;
import com.phoneshop.auth.AuthDtos.RegisterRequest;
import com.phoneshop.auth.AuthDtos.UserResponse;
import com.phoneshop.auth.AuthService.AuthResult;
import com.phoneshop.security.JwtService;
import com.phoneshop.security.SecurityUtils;
import com.phoneshop.security.ShopUserPrincipal;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final AuthService authService;
	private final JwtService jwtService;
	private final String cookieName;

	public AuthController(
			AuthService authService,
			JwtService jwtService,
			@Value("${app.jwt.cookie-name}") String cookieName
	) {
		this.authService = authService;
		this.jwtService = jwtService;
		this.cookieName = cookieName;
	}

	@PostMapping("/register")
	public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request, HttpServletResponse response) {
		AuthResult result = authService.register(request);
		writeAuthCookie(response, result.token());
		return ResponseEntity.ok(result.user());
	}

	@PostMapping("/login")
	public ResponseEntity<UserResponse> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
		AuthResult result = authService.login(request);
		writeAuthCookie(response, result.token());
		return ResponseEntity.ok(result.user());
	}

	@PostMapping("/logout")
	public ResponseEntity<Void> logout(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from(cookieName, "")
				.httpOnly(true)
				.secure(false)
				.path("/")
				.sameSite("Lax")
				.maxAge(0)
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/me")
	public UserResponse me() {
		ShopUserPrincipal principal = SecurityUtils.requireUser();
		return authService.me(principal.id());
	}

	private void writeAuthCookie(HttpServletResponse response, String token) {
		ResponseCookie cookie = ResponseCookie.from(cookieName, token)
				.httpOnly(true)
				.secure(false)
				.path("/")
				.sameSite("Lax")
				.maxAge(jwtService.getTtlSeconds())
				.build();
		response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
	}
}
