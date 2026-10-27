package com.phoneshop.security;

import com.phoneshop.common.ApiException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtils {

	private SecurityUtils() {
	}

	public static ShopUserPrincipal requireUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !(authentication.getPrincipal() instanceof ShopUserPrincipal principal)) {
			throw new ApiException(HttpStatus.UNAUTHORIZED, "Authentication required");
		}
		return principal;
	}
}
