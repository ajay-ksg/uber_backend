package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "NamedLocation")
public class NamedLocation extends Auditable {

    private String name;
    @OneToOne
   private ExactLocation location;

    private String zipCode;
    private String city;
    private String country;
    private String state;

}


//