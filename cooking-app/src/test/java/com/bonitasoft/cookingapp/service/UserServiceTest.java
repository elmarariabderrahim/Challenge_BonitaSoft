package com.bonitasoft.cookingapp.service;

import com.bonitasoft.cookingapp.entity.User;
import com.bonitasoft.cookingapp.respository.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSaveUser() {
        User mockUser = new User();


        when(userRepository.save(mockUser)).thenReturn(mockUser);

        User savedUser = userService.save(mockUser);

        verify(userRepository, times(1)).save(mockUser);
        assertEquals(mockUser, savedUser);
    }

    @Test
    public void testFindUser() {
        String username = "testUser";
        User mockUser = new User();

        when(userRepository.getByUsername(username)).thenReturn(mockUser);

        User foundUser = userService.findUser(username);

        verify(userRepository, times(1)).getByUsername(username);
        assertEquals(mockUser, foundUser);
    }
}
