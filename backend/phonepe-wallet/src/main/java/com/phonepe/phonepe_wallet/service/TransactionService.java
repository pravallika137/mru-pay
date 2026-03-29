package com.phonepe.phonepe_wallet.service;

import com.phonepe.phonepe_wallet.dto.SendMoneyRequest;
import com.phonepe.phonepe_wallet.dto.TransactionResponse;
import com.phonepe.phonepe_wallet.entity.Transaction;
import com.phonepe.phonepe_wallet.entity.Transaction.TransactionStatus;
import com.phonepe.phonepe_wallet.entity.User;
import com.phonepe.phonepe_wallet.entity.Wallet;
import com.phonepe.phonepe_wallet.repository.TransactionRepository;
import com.phonepe.phonepe_wallet.repository.UserRepository;
import com.phonepe.phonepe_wallet.repository.WalletRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserRepository userRepository;
    private final WalletRepository walletRepository;

    /**
     * Core Transaction Logic:
     * 1. Validate sender & receiver UPI
     * 2. Check sender wallet balance
     * 3. Deduct amount from sender
     * 4. Add amount to receiver
     * 5. Save transaction record
     */
    @Transactional
    public TransactionResponse sendMoney(SendMoneyRequest request) {
// 1. Validate sender UPI ID
        User sender = userRepository.findByUpiId(request.getSenderUpi())
                .orElseThrow(() -> new RuntimeException("Sender UPI ID not found: " + request.getSenderUpi()));

// 2. Validate receiver UPI ID
        User receiver = userRepository.findByUpiId(request.getReceiverUpi())
                .orElseThrow(() -> new RuntimeException("Receiver UPI ID not found: " + request.getReceiverUpi()));

// Prevent self-transfer
        if (sender.getUpiId().equals(receiver.getUpiId())) {
            throw new RuntimeException("Cannot send money to yourself");
        }

// Get wallets
        Wallet senderWallet = walletRepository.findByUserUpiId(request.getSenderUpi())
                .orElseThrow(() -> new RuntimeException("Sender wallet not found"));

        Wallet receiverWallet = walletRepository.findByUserUpiId(request.getReceiverUpi())
                .orElseThrow(() -> new RuntimeException("Receiver wallet not found"));

// 3. Check sender wallet balance
        if (senderWallet.getBalance().compareTo(request.getAmount()) < 0) {
// Save failed transaction
            Transaction failedTransaction = createTransaction(
                    request.getSenderUpi(),
                    request.getReceiverUpi(),
                    request.getAmount(),
                    TransactionStatus.FAILED
            );
            transactionRepository.save(failedTransaction);
            throw new RuntimeException("Insufficient balance. Available: " + senderWallet.getBalance());
        }

// Validate minimum amount
        if (request.getAmount().compareTo(BigDecimal.ONE) < 0) {
            throw new RuntimeException("Amount must be at least 1");
        }

        try {
// 4. Deduct amount from sender
            senderWallet.setBalance(senderWallet.getBalance().subtract(request.getAmount()));
            walletRepository.save(senderWallet);

// 5. Add amount to receiver
            receiverWallet.setBalance(receiverWallet.getBalance().add(request.getAmount()));
            walletRepository.save(receiverWallet);

// 6. Save transaction record as SUCCESS
            Transaction transaction = createTransaction(
                    request.getSenderUpi(),
                    request.getReceiverUpi(),
                    request.getAmount(),
                    TransactionStatus.SUCCESS
            );
            Transaction savedTransaction = transactionRepository.save(transaction);

            return mapToTransactionResponse(savedTransaction);

        } catch (Exception e) {
// If anything fails, save as failed transaction
            Transaction failedTransaction = createTransaction(
                    request.getSenderUpi(),
                    request.getReceiverUpi(),
                    request.getAmount(),
                    TransactionStatus.FAILED
            );
            transactionRepository.save(failedTransaction);
            throw new RuntimeException("Transaction failed: " + e.getMessage());
        }
    }

    public List<TransactionResponse> getTransactionHistory(String upiId) {
// Validate UPI ID exists
        userRepository.findByUpiId(upiId)
                .orElseThrow(() -> new RuntimeException("UPI ID not found: " + upiId));

        List<Transaction> transactions = transactionRepository
                .findBySenderUpiOrReceiverUpiOrderByDateDesc(upiId, upiId);

        return transactions.stream()
                .map(this::mapToTransactionResponse)
                .collect(Collectors.toList());
    }

    private Transaction createTransaction(String senderUpi, String receiverUpi,
                                          BigDecimal amount, TransactionStatus status) {
        Transaction transaction = new Transaction();
        transaction.setSenderUpi(senderUpi);
        transaction.setReceiverUpi(receiverUpi);
        transaction.setAmount(amount);
        transaction.setDate(LocalDateTime.now());
        transaction.setStatus(status);
        return transaction;
    }

    private TransactionResponse mapToTransactionResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getSenderUpi(),
                transaction.getReceiverUpi(),
                transaction.getAmount(),
                transaction.getDate(),
                transaction.getStatus()
        );
    }
}