package com.phonepe.phonepe_wallet.dto;

import com.phonepe.phonepe_wallet.entity.Transaction.TransactionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionResponse {
    private Long transactionId;
    private String senderUpi;
    private String receiverUpi;
    private BigDecimal amount;
    private LocalDateTime date;
    private TransactionStatus status;
}