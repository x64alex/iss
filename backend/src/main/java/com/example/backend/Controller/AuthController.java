package com.example.backend.Controller;
import java.util.*;
import java.util.stream.Collectors;


import javax.transaction.Transactional;
import javax.validation.Valid;

import com.example.backend.Model.BucketList;
import com.example.backend.Model.PrivateBucketList;
import com.example.backend.Model.User;
import com.example.backend.Repository.BucketListRepository;
import com.example.backend.Repository.UserRepository;
import com.example.backend.Security.JWT.JwtUtils;
import com.example.backend.Security.Payload.request.LoginRequest;
import com.example.backend.Security.Payload.request.SignupRequest;
import com.example.backend.Security.Payload.response.MessageResponse;
import com.example.backend.Security.Payload.response.UserInfoResponse;
import com.example.backend.Security.Services.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/api")
public class AuthController {
    @Autowired
    AuthenticationManager authenticationManager;

    @Autowired
    private BucketListRepository bucketListRepository;

    @Autowired
    UserRepository userRepository;


    @Autowired
    PasswordEncoder encoder;

    @Autowired
    JwtUtils jwtUtils;


    @PostMapping("/register")
    public Boolean register(@Valid @RequestBody SignupRequest signUpRequest) {
        try {
            User user = new User();
            user.setName(signUpRequest.getName());
            user.setSurname(signUpRequest.getSurname());
            user.setEmail(signUpRequest.getEmail());
            user.setPassword(encoder.encode(signUpRequest.getPassword()));


            if(signUpRequest.getEmail().equals("admin"))
                user.setUserRole("ROLE_ADMIN");
            else{
                user.setUserRole("ROLE_REGULAR");
            }

            userRepository.save(user);
            BucketList privateBucketList = new PrivateBucketList(user.getId());
            user.setPrivateBucketListId(privateBucketList.getId());
            userRepository.save(user);
            bucketListRepository.save(privateBucketList);


            return true;
        }catch(Exception e){
            System.out.println(e);
            return false;
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager
                .authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwtCookie = jwtUtils.generateTokenFromUsernameSignIn(userDetails.getUsername()).toString();

        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, jwtCookie)
                .body(new UserInfoResponse(userDetails.getId(),
                        userDetails.getUsername(),
                        roles,
                        jwtCookie
                ));
    }

    @PostMapping("/logout")
    @PreAuthorize("hasRole('ROLE_REGULAR') or hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> logout() {
        String cookie = jwtUtils.getCleanJwtCookie().toString();
        return ResponseEntity
                .status(HttpStatus.OK)
                .header(HttpHeaders.SET_COOKIE, cookie)
                .body(new MessageResponse("You've been signed out!"));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Map<String, String> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return errors;
    }
}