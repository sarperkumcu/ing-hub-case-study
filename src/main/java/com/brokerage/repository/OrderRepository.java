package com.brokerage.repository;


import com.brokerage.models.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    List<Order> findByCustomerId(UUID customerId);
    List<Order> findByCustomerIdAndCreateDateBetween(UUID customerId, LocalDateTime startDate, LocalDateTime endDate);
    List<Order> findByCustomerIdAndStatus(UUID customerId, String status);
}
