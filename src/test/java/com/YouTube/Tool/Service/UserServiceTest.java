package com.YouTube.Tool.Service;


import com.YouTube.Tool.Entity.User;
import com.YouTube.Tool.Repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)  // ye Junit ko batata h ki mockito use karna hai
public class UserServiceTest {
    @Mock  //iska ek nakli mock object bna dega
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks  // iska ek asli object bnao or usme nakli upr wale object daldo
    private UserService userService;

    @Test
    void testRegisterUser_WhenUsernameIsNew_ShouldSaveUser(){
        User user=new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword("password123");
        when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());

        when(passwordEncoder.encode("password123")).thenReturn("encodedPassword");

        userService.registerUser(user);

        verify(userRepository,times(1)).save(any(User.class
        ));




    }


    @Test
    void testRegisterUser_WhenUsernameExists_ShouldThrowException(){

        User existingUser =new User();
        existingUser.setUsername("existinguser");
        when(userRepository.findByUsername("existinguser")).thenReturn(Optional.of(existingUser));
        assertThrows(IllegalStateException.class,()->{
            userService.registerUser(existingUser);
        });
        verify(userRepository,never()).save(any(User.class));

    }
}
