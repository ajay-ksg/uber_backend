package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "paymentgateway")
public class PaymentGateway extends Auditable{
    private String name;

    @OneToMany(mappedBy =  "paymentGateway")
    private Set<PaymentReceipt> receiptSet = new HashSet<>();
}
