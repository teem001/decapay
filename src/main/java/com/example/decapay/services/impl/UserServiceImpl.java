package com.example.decapay.services.impl;

import com.example.decapay.configurations.mails.EmailSenderService;
import com.example.decapay.configurations.security.CustomUserDetailService;
import com.example.decapay.configurations.security.JwtUtils;
import com.example.decapay.enums.Status;
import com.example.decapay.enums.VerificationType;
import com.example.decapay.exceptions.ResourceNotFoundException;
import com.example.decapay.exceptions.UserAlreadyExistException;
import com.example.decapay.exceptions.UserNotFoundException;
import com.example.decapay.exceptions.ValidationException;
import com.example.decapay.models.Token;

import com.example.decapay.pojos.requestDtos.*;

import com.example.decapay.pojos.responseDtos.UserResponseDto;
import com.example.decapay.repositories.TokenRepository;
import com.example.decapay.services.UserService;
import com.example.decapay.utils.MailSenderUtil;
import com.example.decapay.utils.UserIdUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


import com.example.decapay.models.User;
import com.example.decapay.repositories.UserRepository;

import com.example.decapay.utils.UserUtil;


import javax.mail.MessagingException;
import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.InputMismatchException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    @Value("${forgot.password.url:http://localhost:5432/Api/v1/auth/verify-token/}")
    private String forgotPasswordUrl;

    private final UserRepository userRepository;
    private final UserUtil userUtil;
    private final PasswordEncoder passwordEncoder;
    private final EmailSenderService emailSenderService;
    private final TokenRepository tokenRepository;

    private final MailSenderUtil mailSenderUtil;

    private final UserIdUtil idUtil;


    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private CustomUserDetailService customUserDetailService;
    @Autowired
    private JwtUtils jwtUtils;


    @Override
    public UserResponseDto createUser(UserRequestDto request) throws  MessagingException {

        if(userRepository.findByEmail(request.getEmail()).isPresent())
            throw new UserAlreadyExistException("User already exist");

        boolean matches = request.getPassword().equals(request.getConfirmPassword());

        if(!matches) throw new ValidationException("password and confirm password do not match.");

        User newUser = new User();

        BeanUtils.copyProperties(request,newUser);

        String publicUserId = idUtil.generatedUserId(20);
        newUser.setUserId(publicUserId);

        newUser.setPassword(passwordEncoder.encode(request.getPassword()));





        User savedUser = userRepository.save(newUser);

        mailSenderUtil.verifyMail(savedUser);

        UserResponseDto responseDto = new UserResponseDto();

        BeanUtils.copyProperties(savedUser,responseDto);



        return responseDto;

    }

    @Override
    public ResponseEntity<String> userLogin(LoginRequestDto loginRequestDto) {
         authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword()));
        final UserDetails user = customUserDetailService.loadUserByUsername(loginRequestDto.getEmail());
        if (user != null)
            return ResponseEntity.ok(jwtUtils.generateToken(user));

        return ResponseEntity.status(400).body("Some Error Occurred");
    }




    @Override
    @Transactional
    public ResponseEntity<String> editUser(UserUpdateRequest userUpdateRequest) {

        String email = userUtil.getAuthenticatedUserEmail();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        user.setFirstName(userUpdateRequest.getFirstName());
        user.setLastName(user.getLastName());
        user.setEmail(userUpdateRequest.getEmail());
        user.setPhoneNumber(userUpdateRequest.getPhoneNumber());

        userRepository.save(user);

        return ResponseEntity.ok("User details updated");
    }


    @Override
    public ResponseEntity<String> forgotPasswordRequest(ForgetPasswordRequest forgotPasswordRequest) {
        String email = forgotPasswordRequest.getEmail();
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        String generatedToken = jwtUtils.generatePasswordResetToken(email);

        Token token = new Token();
        token.setToken(generatedToken);
        token.setStatus(Status.ACTIVE);
        token.setVerificationType(VerificationType.RESET_PASSWORD);
        token.setUser(user);

        tokenRepository.save(token);

        String link = String.format("%s%s", forgotPasswordUrl, generatedToken + " expires in 15 minutes.");
        emailSenderService.sendPasswordResetEmail( forgotPasswordRequest.getEmail(), "forgot Password token", link);
        return ResponseEntity.ok("Check your email for password reset instructions");
    }

    @Override
    public ResponseEntity<String> resetPassword(ResetPasswordRequest request) {
        if (!request.getNewPassword().equals(request.getConfirmPassword()))
            throw new InputMismatchException("Passwords do not match");
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok("Password reset successful");
    }

    @Override
    public String verifyToken(String token) {

          tokenRepository.findByToken(token)
                .orElseThrow(() -> new RuntimeException("token does not exist"));
          //todo: update user verification status


        return "token exist";
    }

    @Override
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException(
                        HttpStatus.BAD_REQUEST, "User with email: " + email + " Not Found"));
    }

    @Override
    public void verifyUserExists(String userEmail) {
        userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new UserNotFoundException(
                        HttpStatus.BAD_REQUEST, "User with email: " + userEmail + " Not Found"));

    }
}
