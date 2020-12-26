package com.uber.uberapi.models;

import com.uber.uberapi.Exceptions.InvalidOTPException;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "otp")
public class OTP extends Auditable{
   private String code;
   private String sentToNumber;

    public boolean ValidteEnteredOTP(OTP otp,Long expiryMinutes) {
        if(!code.equals(otp.getCode())){
            return false;
        }
//TODO: check for expiry time as well.
        return true;
    }
}
