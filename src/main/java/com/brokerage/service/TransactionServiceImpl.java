package com.brokerage.service;

import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.request.CancelOrderRequest;
import com.brokerage.models.request.CreateOrderRequest;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import com.brokerage.publisher.OrderEventPublisher;
import com.brokerage.publisher.TransactionEventPublisher;
import com.brokerage.repository.AssetRepository;
import com.brokerage.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.With;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;


@Service
public class TransactionServiceImpl implements TransactionService{
    private final TransactionEventPublisher eventPublisher;
    private final AssetRepository assetRepository;


    public TransactionServiceImpl(TransactionEventPublisher eventPublisher, AssetRepository assetRepository) {
        this.eventPublisher = eventPublisher;
        this.assetRepository = assetRepository;
    }
    public UUID publishDepositEvent(DepositRequest depositRequest) {
        return eventPublisher.publishDepositEvent(depositRequest);
    }

    public UUID publishWithdrawEvent(WithdrawRequest withdrawRequest) {
        return eventPublisher.publishWithdrawEvent(withdrawRequest);
    }
    @Override
    @Transactional
    public void deposit(UUID customerId, BigDecimal amount) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be greater than zero.");
        }

        Optional<Asset> tryAssetOptional = assetRepository.findByUserIdAndAssetNameForUpdate(customerId, "TRY");

        Asset tryAsset;

        if (tryAssetOptional.isPresent()) {
            tryAsset = tryAssetOptional.get();
            tryAsset.setSize(tryAsset.getSize().add(amount));
            tryAsset.setUsableSize(tryAsset.getUsableSize().add(amount));
        } else {
            tryAsset = new Asset();
           // tryAsset.getId(customerId);
            tryAsset.setAssetName("TRY");
            tryAsset.setSize(amount);
            tryAsset.setUsableSize(amount);
        }
        assetRepository.save(tryAsset);
    }


    @Override
    @Transactional
    public void withdraw(UUID customerId, BigDecimal amount, String iban) {
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdraw amount must be greater than zero.");
        }

        Asset tryAsset = assetRepository.findByUserIdAndAssetNameForUpdate(customerId, "TRY")
                .orElseThrow(() -> new ResourceNotFoundException("TRY asset not found for customer with ID: " + customerId));

        if (tryAsset.getUsableSize().compareTo(amount) < 0) {
            throw new InsufficientBalanceException("Insufficient balance for withdrawal. Requested: " + amount + ", Available: " + tryAsset.getUsableSize());
        }

        tryAsset.setSize(tryAsset.getSize().subtract(amount));
        tryAsset.setUsableSize(tryAsset.getUsableSize().subtract(amount));

        assetRepository.save(tryAsset);

        System.out.println("Withdrew " + amount + " TRY from customer " + customerId + " to IBAN " + iban);
    }

}
