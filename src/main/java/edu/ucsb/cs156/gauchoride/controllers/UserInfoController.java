package edu.ucsb.cs156.gauchoride.controllers;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.models.CurrentUser;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import edu.ucsb.cs156.gauchoride.services.CurrentUserService;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


@Api(description="Current User Information")
@RequestMapping("/api/currentUser")
@RestController
public class UserInfoController extends ApiController {

  @Autowired
  UserRepository userRepository;
 
  @ApiOperation(value = "Get information about current user")
  @PreAuthorize("hasRole('ROLE_USER')")
  @GetMapping("")
  public CurrentUser getCurrentUser() {
    SecurityContext securityContext = SecurityContextHolder.getContext();
    Authentication authentication = securityContext.getAuthentication();
    if (authentication instanceof OAuth2AuthenticationToken) {
      OAuth2User oAuthUser = ((OAuth2AuthenticationToken) authentication).getPrincipal();
      String email =  oAuthUser.getAttribute("email");
      Optional<User> optionalUser = userRepository.findByEmail(email);

      boolean isAdmin = true;

      if (optionalUser.isPresent()){
        User user = optionalUser.get();
        isAdmin = user.getAdmin();
      }

      Set<GrantedAuthority> newAuthorities = new HashSet<>();
      Collection<? extends GrantedAuthority> currentAuthorities = authentication.getAuthorities();
      
      currentAuthorities.stream().filter(authority -> !authority.getAuthority().equals("ROLE_ADMIN")).forEach(authority -> {
        newAuthorities.add(authority);
      });

      if (isAdmin){
        newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
      }
      Authentication newAuth = new OAuth2AuthenticationToken(oAuthUser, newAuthorities,(((OAuth2AuthenticationToken)authentication).getAuthorizedClientRegistrationId()));
      SecurityContextHolder.getContext().setAuthentication(newAuth);
    }

    return super.getCurrentUser();
  }
}
