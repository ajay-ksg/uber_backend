package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="constant")
public class Constant extends Auditable {
    public String name;
    public String value;

    public Long getAsLong(){
        return Long.parseLong(value);
    }
}
