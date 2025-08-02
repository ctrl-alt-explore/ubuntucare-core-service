package za.pulsewatch.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import za.pulsewatch.dto.LoginRequest;
import za.pulsewatch.dto.SignupRequest;
import za.pulsewatch.model.User;
import za.pulsewatch.repository.UserRepository;
import za.pulsewatch.security.JwtUtil;
import za.pulsewatch.dto.ApiResponse;
import org.springframework.security.authentication.BadCredentialsException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8080"}, maxAge = 3600)
public class AuthController {
    
    @Autowired
    AuthenticationManager authenticationManager;
    
    @Autowired
    UserRepository userRepository;
    
    @Autowired
    PasswordEncoder encoder;
    
    @Autowired
    JwtUtil jwtUtil;
    
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<Map<String, Object>>> registerUser(@Valid @RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest()
                .body(ApiResponse.error("Email is already in use", "DUPLICATE_EMAIL", 400));
        }
        
        User user = new User();
        user.setFirstName(signUpRequest.getFirstName());
        user.setLastName(signUpRequest.getLastName());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        
        userRepository.save(user);
        
        Map<String, Object> userData = new HashMap<>();
        userData.put("userId", user.getId());
        userData.put("email", user.getEmail());
        userData.put("firstName", user.getFirstName());
        userData.put("lastName", user.getLastName());
        
        return ResponseEntity.ok(ApiResponse.success("User registered successfully", userData));
    }
    
    @PostMapping("/signin")
    public ResponseEntity<ApiResponse<Map<String, Object>>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String jwt = jwtUtil.generateToken((User) authentication.getPrincipal());
            
            User userDetails = (User) authentication.getPrincipal();
            
            Map<String, Object> authData = new HashMap<>();
            authData.put("token", jwt);
            authData.put("email", userDetails.getEmail());
            authData.put("firstName", userDetails.getFirstName());
            authData.put("lastName", userDetails.getLastName());
            
            return ResponseEntity.ok(ApiResponse.success("Authentication successful", authData));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                .body(ApiResponse.error("Invalid email or password", "INVALID_CREDENTIALS", 401));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                .body(ApiResponse.error("Authentication failed", "AUTH_ERROR", 500));
        }
    }
}
