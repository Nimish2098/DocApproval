
package com.Project.DocApproval.auth.security;

import com.Project.DocApproval.auth.entity.AppUser;
import com.Project.DocApproval.auth.repository.AppUserRepository;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

  private final AppUserRepository userRepository;

  public CustomUserDetailsService(AppUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  public UserDetails loadUserByUsername(String email)
      throws UsernameNotFoundException {

    AppUser appUser = userRepository
        .findByEmailIgnoreCase(email.trim())
        .orElseThrow(() ->
                         new UsernameNotFoundException("User not found")
        );

    return User.withUsername(appUser.getEmail())
               .password(appUser.getPasswordHash())
               .authorities("ROLE_" + appUser.getRole())
               .build();
  }
}
