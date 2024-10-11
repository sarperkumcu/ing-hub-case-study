package com.brokerage.event;

import java.math.BigDecimal;
import java.util.UUID;

public record WithdrawEvent(UUID eventId, UUID customerId, BigDecimal amount, String iban) {}

