package com.phonepe.phonepe_wallet.service;

import com.phonepe.phonepe_wallet.dto.AddMoneyRequest;
import com.phonepe.phonepe_wallet.dto.WalletResponse;
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
public class WalletService {

    private final WalletRepository walletRepository;
    private final UserRepository userRepository;

    public WalletResponse getWalletBalance(String upiId) {
        User user = userRepository.findByUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("User not found with UPI ID: " + upiId));

        Wallet wallet = walletRepository.findByUserUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("Wallet not found for UPI ID: " + upiId));

        return new WalletResponse(wallet.getId(), user.getUpiId(), wallet.getBalance());
    }

    @Transactional
    public WalletResponse addMoney(AddMoneyRequest request) {
        User user = userRepository.findByUpiId(request.getUpiId())
                .orElseThrow(() -> new RuntimeException("User not found with UPI ID: " + request.getUpiId()));

        Wallet wallet = walletRepository.findByUserUpiId(request.getUpiId())
                .orElseThrow(() -> new RuntimeException("Wallet not found for UPI ID: " + request.getUpiId()));

// Validate amount
        if (request.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new RuntimeException("Amount must be at least 1");
        }

// Add money (dummy operation - in real app, integrate with payment gateway)
        wallet.setBalance(wallet.getBalance().add(request.getAmount()));
        Wallet updatedWallet = walletRepository.save(wallet);

        return new WalletResponse(updatedWallet.getId(), user.getUpiId(), updatedWallet.getBalance());
    }
}