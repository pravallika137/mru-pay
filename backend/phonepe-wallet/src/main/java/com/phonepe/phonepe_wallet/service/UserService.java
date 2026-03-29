package com.phonepe.phonepe_wallet.service;

import com.phonepe.phonepe_wallet.dto.LoginRequest;
import com.phonepe.phonepe_wallet.dto.UserRegistrationRequest;
import com.phonepe.phonepe_wallet.entity.User;
import com.phonepe.phonepe_wallet.entity.Wallet;
import com.phonepe.phonepe_wallet.repository.UserRepository;
import com.phonepe.phonepe_wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    @Transactional
    public User registerUser(UserRegistrationRequest request) {
// Check if phone number already exists
        if (userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new RuntimeException("Phone number already registered");
        }

// Check if UPI ID already exists
        if (userRepository.existsByUpiId(request.getUpiId())) {
            throw new RuntimeException("UPI ID already exists");
        }

// Create user
        User user = new User();
        user.setName(request.getName());
        user.setPhoneNumber(request.getPhoneNumber());
        user.setUpiId(request.getUpiId());
        user.setPin(request.getPin()); // In production, hash this!

        User savedUser = userRepository.save(user);

// Create wallet automatically on registration
        Wallet wallet = new Wallet();
        wallet.setUser(savedUser);
        wallet.setBalance(BigDecimal.ZERO);
        walletRepository.save(wallet);

        return savedUser;
    }

    public User login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.getPhoneNumber())
                .orElseThrow(() -> new RuntimeException("Invalid phone number or PIN"));

// Validate PIN (in production, use proper password hashing)
        if (!user.getPin().equals(request.getPin())) {
            throw new RuntimeException("Invalid phone number or PIN");
        }

        return user;
    }

    public User getUserProfile(String upiId) {
        return userRepository.findByUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("User not found with UPI ID: " + upiId));
    }
}