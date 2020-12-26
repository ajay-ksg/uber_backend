package com.uber.uberapi.models;

import lombok.*;
import net.bytebuddy.implementation.bind.MethodDelegationBinder;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name="account")
public class Account extends Auditable {
    @Column(unique = true,nullable = false)
    private String username;
    private String password;

    @ManyToMany(fetch =  FetchType.EAGER)
    private List<Role> roles = new ArrayList<>();
}
