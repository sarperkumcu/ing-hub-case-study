package com.brokerage.controller;

import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;

import com.brokerage.service.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }
    @PostMapping("/deposit")
    public ResponseEntity<String> depositMoney(@RequestBody DepositRequest depositRequest) {
        transactionService.publishDepositEvent(depositRequest);
        return ResponseEntity.accepted().body("Deposit request received.");
    }

    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMoney(@RequestBody WithdrawRequest withdrawRequest) {
        transactionService.publishWithdrawEvent(withdrawRequest);
        return ResponseEntity.accepted().body("Withdrawal request received.");
    }
}
