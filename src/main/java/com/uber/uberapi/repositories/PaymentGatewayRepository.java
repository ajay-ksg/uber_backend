package com.uber.uberapi.repositories;

import com.uber.uberapi.models.PaymentGatway;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentGatewayRepository extends JpaRepository<PaymentGatway,Long> {
}
