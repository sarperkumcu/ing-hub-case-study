package com.brokerage.service;

import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.dto.DepositDTO;
import com.brokerage.models.dto.WithdrawDTO;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.User;
import com.brokerage.publisher.TransactionEventPublisher;
import com.brokerage.repository.AssetRepository;
import com.brokerage.service.interfaces.TransactionService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


@Service
public class TransactionServiceImpl implements TransactionService {
    private final TransactionEventPublisher eventPublisher;
    private final UserDetailsServiceImpl userDetailsService;
    private final AssetRepository assetRepository;


    public TransactionServiceImpl(TransactionEventPublisher eventPublisher, UserDetailsServiceImpl userDetailsService, AssetRepository assetRepository) {
        this.eventPublisher = eventPublisher;
        this.userDetailsService = userDetailsService;
        this.assetRepository = assetRepository;
    }
    public UUID publishDepositEvent(DepositDTO depositDTO) {
        return eventPublisher.publishDepositEvent(depositDTO);
    }

    public UUID publishWithdrawEvent(WithdrawDTO withdrawDTO) {
        return eventPublisher.publishWithdrawEvent(withdrawDTO);
    }
    @Override
    @Transactional
    public void deposit(UUID userId, BigDecimal amount) {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        Optional<Asset> tryAssetOptional = assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY");
        Asset tryAsset;
        User user = userDetailsService.getUserById(userId);
        if (tryAssetOptional.isPresent()) {
            tryAsset = tryAssetOptional.get();
            tryAsset.setSize(tryAsset.getSize().add(amount));
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(amount));
        } else {
            tryAsset = new Asset();
            tryAsset.setUser(user);
            tryAsset.setAssetName("TRY");
            tryAsset.setSize(amount);
            tryAsset.setUsableSize(amount);
        }
        assetRepository.save(tryAsset);
    }


    @Override
    @Transactional
    public void withdraw(UUID userId, BigDecimal amount, String iban) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero.");
        }

        Asset tryAsset = assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY")
                .orElseThrow(() -> new ResourceNotFoundException("TRY asset not found for customer with ID: " + userId));

        if (tryAsset.getUsableSize().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal. Requested: " + amount + ", Available: " + tryAsset.getUsableSize());
        }

        tryAsset.setSize(tryAsset.getSize().subtract(amount));
        tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(amount));

        assetRepository.save(tryAsset);

        System.out.println("Withdrew " + amount + " TRY from customer " + userId + " to IBAN " + iban);
    }

}
