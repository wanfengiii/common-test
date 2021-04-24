package com.common.security;

import com.common.domain.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;

public final class Auth {

    /**
     * return the username of the current user, null if no authentication existed
     * 
     * the other way to get Authentication object is: declare parameter type of Authentication in Controller method
     * eg:
     * 
     * @GetMapping("/name")
     * public String getUsername(Authentication auth) {
     *      return auth.getName();
     * }
     * 
     */
    private final static String ADMIN = "admin";

    private final static String ENT = "ent";

    private final static String CUS = "cus";

    public static String getUsername() {
      Authentication auth = SecurityContextHolder.getContext().getAuthentication(); // for reactive part, ReactiveSecurityContextHolder
      return getUsername(auth);
    }

    public static UserDetails getUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            Object principal = auth.getPrincipal();
            if (principal instanceof UserDetails) {
              return ((UserDetails) principal);
            }
        }
        return null;
    }

    public static User getDomainUser() {
        UserDetails ud = getUser();
        if (ud instanceof MyUserDetails) {
            return ((MyUserDetails) ud).getUser();
        }
        return null;
    }

    public static String getUsername(Authentication auth) {
      String username = null;
      if (auth != null) {
          Object principal = auth.getPrincipal();
          if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
          } else {
            username = principal.toString();
          }
      }
      return username;
     }

    public static boolean isEnterpriseUser() {
        return getUserType().equals(ENT);
    }

    public static boolean isAdmin() {
        return getUserType().equals(ADMIN);
    }

    public static boolean isCus() {
        return getUserType().equals(CUS);
    }

    public static String getUserType() {
        User domainUser = getDomainUser();
        return Objects.isNull(domainUser) ? "cus" : domainUser.getType();
    }

    public static Long getEntId() {
        User domainUser = getDomainUser();
        return Objects.isNull(domainUser) ? null : domainUser.getEntId();
    }
}