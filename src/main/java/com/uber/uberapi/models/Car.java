package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="Car", indexes = {
        @Index(columnList = "color_id"),
})

public class Car extends Auditable{
    @ManyToOne
    private Color color;

    @OneToOne
    private Driver driver;

    private String plateNumber;
    private String brandAndModel;

    @Enumerated(value = EnumType.STRING)
    private CatType carType;

}
