package za.pulsewatch.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import za.pulsewatch.dto.ApiResponse;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/test")
public class TestController {
    
    @GetMapping("/public")
    public ApiResponse<Map<String, String>> publicEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a public endpoint");
        response.put("status", "accessible");
        return ApiResponse.success("Public endpoint accessed successfully", response);
    }
    
    @GetMapping("/protected")
    @PreAuthorize("hasRole('USER')")
    public ApiResponse<Map<String, String>> protectedEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is a protected endpoint");
        response.put("status", "authorized");
        return ApiResponse.success("Protected endpoint accessed successfully", response);
    }
    
    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResponse<Map<String, String>> adminEndpoint() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "This is an admin endpoint");
        response.put("status", "admin authorized");
        return ApiResponse.success("Admin endpoint accessed successfully", response);
    }
}
