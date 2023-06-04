package edu.ucsb.cs156.gauchoride.Interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Optional;
import java.util.HashSet;
import java.util.Set;
import java.util.Collection;
import edu.ucsb.cs156.gauchoride.entities.User;


@Component
public class RoleUserInterceptor implements HandlerInterceptor {

   @Autowired
   UserRepository userRepository;

   @Override
   public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // Update user's security context on server each time the user makes HTTP request to the backend
        // If user has admin status in database we will keep ROLE_ADMIN in security context
        // Otherwise interceptor will remove ROLE_ADMIN before the incoming request is processed by backend API
        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof OAuth2AuthenticationToken ) {
            OAuth2User oAuthUser = ((OAuth2AuthenticationToken) authentication).getPrincipal();
            String email = oAuthUser.getAttribute("email");
            Optional<User> optionalUser = userRepository.findByEmail(email);
            if (optionalUser.isPresent()){
                User user = optionalUser.get();

                Set<GrantedAuthority> newAuthorities = new HashSet<>();
                Collection<? extends GrantedAuthority> currentAuthorities = authentication.getAuthorities();
                currentAuthorities.stream()
                .filter(authority -> !authority.getAuthority().equals("ROLE_ADMIN")
                 && !authority.getAuthority().equals("ROLE_DRIVER"))
                .forEach(authority -> {
                    newAuthorities.add(authority);
                });

                if (user.getAdmin()){
                    newAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
                }

                if (user.getDriver()){
                    newAuthorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
                }
                
                Authentication newAuth = new OAuth2AuthenticationToken(oAuthUser, newAuthorities,(((OAuth2AuthenticationToken)authentication).getAuthorizedClientRegistrationId()));
                SecurityContextHolder.getContext().setAuthentication(newAuth);
            }
        }

      return true;
   }
    
}
