package com.common.security;

import com.common.domain.User;
import com.common.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOpt = userService.findUser(username);

        if (!userOpt.isPresent()) {
          throw new UsernameNotFoundException("User '" + username + "' not found");
        }

        return new MyUserDetails(userOpt.get());
    }

}