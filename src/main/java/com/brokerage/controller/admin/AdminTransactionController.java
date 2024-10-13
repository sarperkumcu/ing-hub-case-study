package com.brokerage.controller.admin;

import com.brokerage.models.dto.DepositDTO;
import com.brokerage.models.dto.WithdrawDTO;
import com.brokerage.models.request.admin.AdminDepositRequest;
import com.brokerage.models.request.admin.AdminWithdrawRequest;
import com.brokerage.service.interfaces.TransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/transactions")
public class AdminTransactionController {
    private final TransactionService transactionService;

    public AdminTransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/deposit")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> depositMoneyAdmin(@RequestBody AdminDepositRequest depositRequest) {
        DepositDTO depositDTO = new DepositDTO(depositRequest.getUserId(), depositRequest.getAmount());
        transactionService.publishDepositEvent(depositDTO);
        return ResponseEntity.accepted().body("Deposit request received.");
    }
    @PostMapping("/withdraw")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> withdrawMoneyAdmin(@RequestBody AdminWithdrawRequest withdrawRequest) {
        WithdrawDTO withdrawDTO = new WithdrawDTO(withdrawRequest.getUserId(), withdrawRequest.getAmount(), withdrawRequest.getIban());
        transactionService.publishWithdrawEvent(withdrawDTO);
        return ResponseEntity.accepted().body("Withdrawal request received.");
    }
}
