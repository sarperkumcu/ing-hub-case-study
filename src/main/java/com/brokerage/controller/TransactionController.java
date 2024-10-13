package com.brokerage.controller;

import com.brokerage.models.dto.DepositDTO;
import com.brokerage.models.dto.WithdrawDTO;
import com.brokerage.models.entity.User;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;

import com.brokerage.models.request.admin.AdminDepositRequest;
import com.brokerage.models.request.admin.AdminWithdrawRequest;
import com.brokerage.service.TransactionService;
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
    @PostMapping("/deposit/admin")
    public ResponseEntity<String> depositMoneyAdmin(@RequestBody AdminDepositRequest depositRequest) {
        DepositDTO depositDTO = new DepositDTO(depositRequest.getUserId(), depositRequest.getAmount());
        transactionService.publishDepositEvent(depositDTO);
        return ResponseEntity.accepted().body("Deposit request received.");
    }

    @PostMapping("/deposit")
    public ResponseEntity<String> depositMoney(@RequestBody DepositRequest depositRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        DepositDTO depositDTO = new DepositDTO(user.getId(), depositRequest.getAmount());
        transactionService.publishDepositEvent(depositDTO);
        return ResponseEntity.accepted().body("Deposit request received.");
    }

    @PostMapping("/withdraw/admin")
    public ResponseEntity<String> withdrawMoneyAdmin(@RequestBody AdminWithdrawRequest withdrawRequest) {
        WithdrawDTO withdrawDTO = new WithdrawDTO(withdrawRequest.getUserId(), withdrawRequest.getAmount(), withdrawRequest.getIban());
        transactionService.publishWithdrawEvent(withdrawDTO);
        return ResponseEntity.accepted().body("Withdrawal request received.");
    }
    @PostMapping("/withdraw")
    public ResponseEntity<String> withdrawMoney(@RequestBody WithdrawRequest withdrawRequest) {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getDetails();
        WithdrawDTO withdrawDTO = new WithdrawDTO(user.getId(), withdrawRequest.getAmount(), withdrawRequest.getIban());
        transactionService.publishWithdrawEvent(withdrawDTO);
        return ResponseEntity.accepted().body("Withdrawal request received.");
    }
}
