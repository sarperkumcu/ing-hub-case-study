package com.brokerage.controller;

import com.brokerage.models.dto.DepositDTO;
import com.brokerage.models.dto.WithdrawDTO;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;

import com.brokerage.models.request.admin.AdminDepositRequest;
import com.brokerage.models.request.admin.AdminWithdrawRequest;
import com.brokerage.service.interfaces.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositMoney(@Valid @RequestBody DepositRequest depositRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        DepositDTO depositDTO = new DepositDTO(user.getId(), depositRequest.getAmount());
        transactionService.publishDepositEvent(depositDTO);
        return ResponseEntity.accepted().body("Deposit request received.");
    }


    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMoney(@Valid @RequestBody WithdrawRequest withdrawRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        WithdrawDTO withdrawDTO = new WithdrawDTO(user.getId(), withdrawRequest.getAmount(), withdrawRequest.getIban());
        transactionService.publishWithdrawEvent(withdrawDTO);
        return ResponseEntity.accepted().body("Withdrawal request received.");
    }
}
