package com.brokerage.service;

import com.brokerage.exception.InsufficientBalanceException;
import com.brokerage.exception.ResourceNotFoundException;
import com.brokerage.models.dto.DepositDTO;
import com.brokerage.models.dto.WithdrawDTO;
import com.brokerage.models.entity.Asset;
import com.brokerage.models.entity.User;
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

    @Mock
    private UserDetailsServiceImpl userDetailsService;
    @InjectMocks
    private TransactionServiceImpl transactionService;

    private UUID userId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();

        user = new User();
        user.setId(userId);
    }

    @Test
    void deposit_WithExistingTRYAsset_ShouldUpdateBalance() {
        Asset tryAsset = new Asset();
        tryAsset.setUser(user);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(BigDecimal.valueOf(1000));
        tryAsset.setUsableSize(BigDecimal.valueOf(800));

        when(assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        transactionService.deposit(userId, BigDecimal.valueOf(200));

        assertEquals(BigDecimal.valueOf(1200), tryAsset.getSize());
        assertEquals(BigDecimal.valueOf(1000), tryAsset.getUsableSize());

        verify(assetRepository).save(tryAsset);
    }

    @Test
    void deposit_WithNoExistingTRYAsset_ShouldCreateNewAsset() {
        when(assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY"))
                .thenReturn(Optional.empty());

        transactionService.deposit(userId, BigDecimal.valueOf(500));

        verify(assetRepository).save(any(Asset.class));
        Asset savedAsset = new Asset();
        savedAsset.setUser(user);
        savedAsset.setAssetName("TRY");
        savedAsset.setSize(BigDecimal.valueOf(500));
        savedAsset.setUsableSize(BigDecimal.valueOf(500));

        verify(assetRepository, times(1)).save(any(Asset.class));
    }

    @Test
    void deposit_WithNegativeAmount_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.deposit(userId, BigDecimal.valueOf(-100));
        });

        assertEquals("Deposit amount must be greater than zero.", exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void withdraw_WithSufficientBalance_ShouldUpdateBalance() {
        Asset tryAsset = new Asset();
        tryAsset.setUser(user);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(BigDecimal.valueOf(1000));
        tryAsset.setUsableSize(BigDecimal.valueOf(800));

        when(assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        transactionService.withdraw(userId, BigDecimal.valueOf(500), "TR1234567890");

        assertEquals(BigDecimal.valueOf(500), tryAsset.getSize());
        assertEquals(BigDecimal.valueOf(300), tryAsset.getUsableSize());

        verify(assetRepository).save(tryAsset);
    }

    @Test
    void withdraw_WithInsufficientBalance_ShouldThrowException() {
        Asset tryAsset = new Asset();
        tryAsset.setUser(user);
        tryAsset.setAssetName("TRY");
        tryAsset.setSize(BigDecimal.valueOf(1000));
        tryAsset.setUsableSize(BigDecimal.valueOf(300));

        when(assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY"))
                .thenReturn(Optional.of(tryAsset));

        InsufficientBalanceException exception = assertThrows(InsufficientBalanceException.class, () -> {
            transactionService.withdraw(userId, BigDecimal.valueOf(500), "TR1234567890");
        });

        assertEquals("Insufficient balance for withdrawal. Requested: 500, Available: 300", exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void withdraw_WithNoTRYAsset_ShouldThrowResourceNotFoundException() {
        when(assetRepository.findByUserIdAndAssetNameForUpdate(userId, "TRY"))
                .thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(ResourceNotFoundException.class, () -> {
            transactionService.withdraw(userId, BigDecimal.valueOf(500), "TR1234567890");
        });

        assertEquals("TRY asset not found for customer with ID: " + userId, exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void withdraw_WithNegativeAmount_ShouldThrowIllegalArgumentException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transactionService.withdraw(userId, BigDecimal.valueOf(-100), "TR1234567890");
        });

        assertEquals("Withdraw amount must be greater than zero.", exception.getMessage());

        verify(assetRepository, never()).save(any(Asset.class));
    }

    @Test
    void publishDepositEvent_ShouldCallEventPublisher() {
        DepositDTO depositDTO = new DepositDTO(userId, BigDecimal.valueOf(500));
        UUID expectedUuid = UUID.randomUUID();

        when(eventPublisher.publishDepositEvent(depositDTO)).thenReturn(expectedUuid);

        UUID result = transactionService.publishDepositEvent(depositDTO);

        verify(eventPublisher).publishDepositEvent(depositDTO);

        assertEquals(expectedUuid, result);
    }

    @Test
    void publishWithdrawEvent_ShouldCallEventPublisher() {
        WithdrawDTO withdrawDTO = new WithdrawDTO(userId, BigDecimal.valueOf(500), "TR1234567890");
        UUID expectedUuid = UUID.randomUUID();

        when(eventPublisher.publishWithdrawEvent(withdrawDTO)).thenReturn(expectedUuid);

        UUID result = transactionService.publishWithdrawEvent(withdrawDTO);

        verify(eventPublisher).publishWithdrawEvent(withdrawDTO);

        assertEquals(expectedUuid, result);
    }
}
