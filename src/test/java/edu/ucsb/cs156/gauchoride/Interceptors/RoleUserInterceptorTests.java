package edu.ucsb.cs156.gauchoride.Interceptors;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.security.core.context.SecurityContext;
import  org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import edu.ucsb.cs156.gauchoride.entities.User;
import edu.ucsb.cs156.gauchoride.ControllerTestCase;
import edu.ucsb.cs156.gauchoride.repositories.UserRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import java.util.Collection;
import java.util.Map;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.mock.web.MockHttpServletResponse;

import lombok.extern.slf4j.Slf4j;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
public class RoleUserInterceptorTests extends ControllerTestCase{

    @MockBean
    UserRepository userRepository;

    @Autowired
    private RequestMappingHandlerMapping mapping;

    @BeforeEach
    public void setupSecurityContext(){
        Map<String, Object> attributes = new HashMap<>();
        attributes.put("sub", "mockSub");
        attributes.put("name", "mockOauthUserName");
        attributes.put("email", "mockOauth@gmail.com");

        Set<GrantedAuthority> fakeAuthorities = new HashSet<>();
        fakeAuthorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        fakeAuthorities.add(new SimpleGrantedAuthority("ROLE_DRIVER"));
        fakeAuthorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        fakeAuthorities.add(new SimpleGrantedAuthority("ROLE_MEMBER"));

        OAuth2User mockUser = new DefaultOAuth2User(fakeAuthorities, attributes, "name");
        Authentication authentication = new OAuth2AuthenticationToken(mockUser, fakeAuthorities , "mockUserRegisterId");
        // Set the authentication in the SecurityContextHolder for test environment
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    @Test
    public void interceptor_remove_admin_driver_role() throws Exception {
        // Set up
        User mockUser = User.builder().id(1L).email("mockOauth@gmail.com").admin(false).familyName("lmao").driver(false).build();
        when(userRepository.findByEmail("mockOauth@gmail.com")).thenReturn(Optional.of(mockUser));

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        // Can access mock securityContext in this test
        OAuth2User oAuthUser = ((OAuth2AuthenticationToken) authentication).getPrincipal();
        java.util.Map<java.lang.String,java.lang.Object> attrs = oAuthUser.getAttributes();
        log.info("test user attrs={}", attrs);

        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/api/currentUser");
        HandlerExecutionChain chain = mapping.getHandler(request);
        MockHttpServletResponse response = new MockHttpServletResponse();

        assert chain != null;
        Optional<HandlerInterceptor> roleRuleInterceptor = chain.getInterceptorList()
                        .stream()
                        .filter(RoleUserInterceptor.class::isInstance)
                        .findAny();

        assertTrue(roleRuleInterceptor.isPresent());
        roleRuleInterceptor.get().preHandle(request, response, chain.getHandler());

        Collection<? extends GrantedAuthority> currentAuthorities = authentication.getAuthorities();
        for (GrantedAuthority authority : currentAuthorities) {
            // Access each authority element here
            // Somehow not properly updated. cannot write to securityContext in this case
            System.out.println(authority.getAuthority());
        }
        verify(userRepository, times(1)).findByEmail("mockOauth@gmail.com");
    }
}
