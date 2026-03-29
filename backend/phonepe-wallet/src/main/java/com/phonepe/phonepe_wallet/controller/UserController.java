package com.phonepe.phonepe_wallet.controller;

import com.phonepe.phonepe_wallet.dto.ApiResponse;
import com.phonepe.phonepe_wallet.dto.LoginRequest;
import com.phonepe.phonepe_wallet.dto.UserRegistrationRequest;
import com.phonepe.phonepe_wallet.entity.User;
import com.phonepe.phonepe_wallet.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "User Management", description = "APIs for user registration, login, and profile management")
public class UserController {

    private final UserService userService;

    @Operation(summary = "Register new user",
            description = "Register a new user with name, phone number, UPI ID, and PIN. Wallet is created automatically.")
    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody UserRegistrationRequest request) {
        try {
            User user = userService.registerUser(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new ApiResponse(true, "User registered successfully", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "User login",
            description = "Authenticate user with phone number and PIN")
    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest request) {
        try {
            User user = userService.login(request);
            return ResponseEntity.ok(new ApiResponse(true, "Login successful", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Get user profile",
            description = "Retrieve user profile information by UPI ID")
    @GetMapping("/profile/{upiId}")
    public ResponseEntity<ApiResponse> getUserProfile(@PathVariable String upiId) {
        try {
            User user = userService.getUserProfile(upiId);
            return ResponseEntity.ok(new ApiResponse(true, "User profile retrieved", user));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}