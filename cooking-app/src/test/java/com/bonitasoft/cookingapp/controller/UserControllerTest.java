package com.bonitasoft.cookingapp.controller;


import com.bonitasoft.cookingapp.entity.AuthRequest;
import com.bonitasoft.cookingapp.entity.User;
import com.bonitasoft.cookingapp.service.UserService;
import com.bonitasoft.cookingapp.util.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.bonitasoft.cookingapp.controller.UserController;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserController userController;

    @Test
    public void testAddUser_Success() {
        User newUser = new User();
        newUser.setUsername("newUser");
        newUser.setPassword("password123");

        when(userService.findUser("newUser")).thenReturn(null);

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        when(userService.save(any(User.class))).thenReturn(newUser);

        ResponseEntity<?> responseEntity = userController.welcome(newUser);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals(newUser, responseEntity.getBody());

        verify(userService, times(1)).save(newUser);
    }

    @Test
    public void testAddUser_UserAlreadyExists() {
        User existingUser = new User();
        existingUser.setUsername("existingUser");

        when(userService.findUser("existingUser")).thenReturn(existingUser);

        ResponseEntity<?> responseEntity = userController.welcome(existingUser);

        assertEquals(HttpStatus.CONFLICT, responseEntity.getStatusCode());
        assertEquals("Unable to create User, already exist !", responseEntity.getBody());

        verify(userService, times(1)).findUser("existingUser");
    }


    @Test
    public void testGenerateToken_ValidCredentials() throws Exception {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("password123");

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        )).thenReturn(null);

        when(jwtUtil.generateToken("testUser")).thenReturn("generatedToken");

        String generatedToken = userController.generateToken(authRequest);

        assertEquals("generatedToken", generatedToken);

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        verify(jwtUtil, times(1)).generateToken("testUser");
    }

    @Test
    public void testGenerateToken_InvalidCredentials() {
        AuthRequest authRequest = new AuthRequest();
        authRequest.setUsername("testUser");
        authRequest.setPassword("invalidPassword");

        when(authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        )).thenThrow(new IllegalArgumentException("invalid username or password"));

        Exception exception = org.junit.jupiter.api.Assertions.assertThrows(Exception.class,
                () -> userController.generateToken(authRequest));

        assertEquals("invalid username or password", exception.getMessage());

        verify(authenticationManager, times(1)).authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );
    }
}
