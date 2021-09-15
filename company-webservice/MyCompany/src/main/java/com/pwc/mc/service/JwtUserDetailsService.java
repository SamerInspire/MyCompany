package com.pwc.mc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.User;
import com.pwc.mc.model.UserVO;

@Service
public class JwtUserDetailsService implements UserDetailsService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private PasswordEncoder bcryptEncoder;

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

		UserVO user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("User not found with email: " + email);
		}
		return new User(user.getEmail(), user.getPassword(), user.getAuthorities());
	}

	public UserVO save(UserVO user) {
		UserVO newUser = new UserVO();
		newUser.setEmail(user.getEmail());
		newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
		return userRepository.save(newUser);
	}
}