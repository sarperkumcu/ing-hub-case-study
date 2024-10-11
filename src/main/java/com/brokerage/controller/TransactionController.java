package com.brokerage.controller;

import com.brokerage.event.DepositEvent;
import com.brokerage.event.WithdrawEvent;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import com.brokerage.publisher.OrderEventPublisher;
import com.brokerage.service.OrderServiceImpl;
import com.brokerage.service.TransactionService;
import com.brokerage.service.TransactionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

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
