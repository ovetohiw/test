package com.phoneshop.security;

import com.phoneshop.user.Role;

public record ShopUserPrincipal(Long id, String email, Role role) {
}
