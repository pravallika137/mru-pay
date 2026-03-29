package com.phonepe.phonepe_wallet.controller;

import com.phonepe.phonepe_wallet.dto.ApiResponse;
import com.phonepe.phonepe_wallet.dto.SendMoneyRequest;
import com.phonepe.phonepe_wallet.dto.TransactionResponse;
import com.phonepe.phonepe_wallet.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/transaction")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
@Tag(name = "Transaction Operations", description = "Core APIs for sending money and viewing transaction history")
public class TransactionController {

    private final TransactionService transactionService;

    @Operation(summary = "Send money",
            description = "Send money from sender to receiver. Validates UPI IDs, checks balance, deducts from sender, adds to receiver, and saves transaction record.")
    @PostMapping("/send")
    public ResponseEntity<ApiResponse> sendMoney(@Valid @RequestBody SendMoneyRequest request) {
        try {
            TransactionResponse transaction = transactionService.sendMoney(request);
            return ResponseEntity.ok(new ApiResponse(true, "Money sent successfully", transaction));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }

    @Operation(summary = "Get transaction history",
            description = "Retrieve all sent and received transactions for a given UPI ID")
    @GetMapping("/history/{upiId}")
    public ResponseEntity<ApiResponse> getTransactionHistory(@PathVariable String upiId) {
        try {
            List<TransactionResponse> transactions = transactionService.getTransactionHistory(upiId);
            return ResponseEntity.ok(new ApiResponse(true, "Transaction history retrieved", transactions));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(new ApiResponse(false, e.getMessage()));
        }
    }
}