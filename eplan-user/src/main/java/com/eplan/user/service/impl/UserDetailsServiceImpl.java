package com.eplan.user.service.impl;

import com.eplan.user.entity.User;
import com.eplan.user.repository.UserRepository;
import com.eplan.user.service.UserDetailService;
import com.eplan.user.util.UserPrinciple;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
* @author  Adinandra Dharmasurya
* @version 1.0
* @since   2020-09-19
*/
@Service
public class UserDetailsServiceImpl implements UserDetailService {

    @Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.getUserByAuthentication(username);
		return UserPrinciple.build(user);
	}
    
}
