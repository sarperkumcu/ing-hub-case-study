package com.brokerage.service;

import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.request.DepositRequest;
import com.brokerage.models.request.WithdrawRequest;
import com.brokerage.publisher.TransactionEventPublisher;
import com.brokerage.repository.AssetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionEventPublisher eventPublisher;
    @Mock
    private AssetRepository assetRepository;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UUID customerId;

    @BeforeEach
    void setUp() {
        customerId = UUID.randomUUID();
    }

    @Test
    void deposit_WithExistingTRYAsset_ShouldUpdateBalance() {
        // Mock the existing TRY asset for the customer
        Asset tryAsset = new Asset();
        tryAsset.setCustomerId(customerId);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(BigDecimal.valueOf(1000));
        tryAsset.setUsableSize(BigDecimal.valueOf(800));

        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        transactionService.deposit(customerId, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(1200), tryAsset.getSize());
        assertEquals(BigDecimal.valueOf(1000), tryAsset.getUsableSize());

        verify(assetRepository).save(tryAsset);
    }

    @Test
    void deposit_WithNoExistingTRYAsset_ShouldCreateNewAsset() {
        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "TRY"))
                .thenReturn(Optional.empty());

        transactionService.deposit(customerId, BigDecimal.valueOf(500));

        verify(assetRepository).save(any(Asset.class));
        Asset savedAsset = new Asset();
        savedAsset.setCustomerId(customerId);
        savedAsset.setAssetName("TRY");
        savedAsset.setSize(BigDecimal.valueOf(500));
        savedAsset.setUsableSize(BigDecimal.valueOf(500));

        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void deposit_WithNegativeAmount_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.deposit(customerId, BigDecimal.valueOf(-100));
        });

        assertEquals("Deposit amount must be greater than zero.", exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void withdraw_WithSufficientBalance_ShouldUpdateBalance() {
        Asset tryAsset = new Asset();
        tryAsset.setCustomerId(customerId);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(BigDecimal.valueOf(1000));
        tryAsset.setUsableSize(BigDecimal.valueOf(800));

        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        transactionService.withdraw(customerId, BigDecimal.valueOf(500), "TR1234567890");

        assertEquals(BigDecimal.valueOf(500), tryAsset.getSize());
        assertEquals(BigDecimal.valueOf(300), tryAsset.getUsableSize());

        verify(assetRepository).save(tryAsset);
    }

    @Test
    void withdraw_WithInsufficientBalance_ShouldThrowException() {
        Asset tryAsset = new Asset();
        tryAsset.setCustomerId(customerId);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(BigDecimal.valueOf(1000));
        tryAsset.setUsableSize(BigDecimal.valueOf(300));

        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.withdraw(customerId, BigDecimal.valueOf(500), "TR1234567890");
        });

        assertEquals("Insufficient balance for withdrawal. Requested: 500, Available: 300", exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void withdraw_WithNoTRYAsset_ShouldThrowResourceNotFoundException() {
        when(assetRepository.findByCustomerIdAndAssetNameForUpdate(customerId, "TRY"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.withdraw(customerId, BigDecimal.valueOf(500), "TR1234567890");
        });

        assertEquals("TRY asset not found for customer with ID: " + customerId, exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void withdraw_WithNegativeAmount_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.withdraw(customerId, BigDecimal.valueOf(-100), "TR1234567890");
        });

        assertEquals("Withdraw amount must be greater than zero.", exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }
    @Test
    void publishDepositEvent_ShouldCallEventPublisher() {
        DepositRequest depositRequest = new DepositRequest();
        UUID expectedUuid = UUID.randomUUID();

        when(eventPublisher.publishDepositEvent(depositRequest)).thenReturn(expectedUuid);

        UUID result = transactionService.publishDepositEvent(depositRequest);

        verify(eventPublisher).publishDepositEvent(depositRequest);

        assertEquals(expectedUuid, result);
    }

    @Test
    void publishWithdrawEvent_ShouldCallEventPublisher() {
        WithdrawRequest withdrawRequest = new WithdrawRequest();
        UUID expectedUuid = UUID.randomUUID();

        when(eventPublisher.publishWithdrawEvent(withdrawRequest)).thenReturn(expectedUuid);

        UUID result = transactionService.publishWithdrawEvent(withdrawRequest);

        verify(eventPublisher).publishWithdrawEvent(withdrawRequest);

        assertEquals(expectedUuid, result);
    }
}

