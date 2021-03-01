package com.common.security;

import com.common.domain.User;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Collections;

public class MyUserDetails extends org.springframework.security.core.userdetails.User {

    private User user;

    public MyUserDetails(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public MyUserDetails(User user) {
        this(user.getUsername(), user.getPassword(), Collections.emptyList());
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}
