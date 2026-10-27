package com.phoneshop.security;

import com.phoneshop.user.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class ShopUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;

	public ShopUserDetailsService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	@Override
	public UserDetails loadUserByUsername(String username) {
		return userRepository.findByEmailIgnoreCase(username)
				.map(ShopUserDetails::new)
				.orElseThrow(() -> new UsernameNotFoundException("User not found"));
	}
}
