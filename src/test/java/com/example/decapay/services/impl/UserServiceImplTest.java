package com.example.decapay.services.impl;

import com.example.decapay.configurations.security.CustomUserDetailService;
import com.example.decapay.configurations.security.JwtUtils;
import com.example.decapay.exceptions.AuthenticationException;
import com.example.decapay.pojos.requestDtos.LoginRequestDto;
import com.example.decapay.pojos.requestDtos.UserUpdateRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
@SpringBootTest
public class UserServiceImplTest {

    @MockBean
    private AuthenticationManager authenticationManager;

    @Mock
    private CustomUserDetailService customUserDetailService;

    @Mock
    private UserDetails userDetails;

    @Mock
    private JwtUtils jwtUtils;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    LoginRequestDto loginRequestDto;


    @BeforeEach
    public void setUp(){
        MockitoAnnotations.openMocks(this);
        loginRequestDto = new LoginRequestDto();
        loginRequestDto.setEmail("oluseun@gmail.com");
        loginRequestDto.setPassword("oluseun1");

    }
    @Test
    public void userLogin(){
    when(authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())))
            .thenReturn(authentication);
    when(customUserDetailService.loadUserByUsername(anyString()))
            .thenReturn(userDetails);
    when(jwtUtils.generateToken(any(UserDetails.class))).thenReturn("934859hfdjghdhfk");

        ResponseEntity<String> response = userService.userLogin(loginRequestDto);
        assertNotNull(response);
        verify(customUserDetailService, times(1))
                .loadUserByUsername(anyString());
    }

}
