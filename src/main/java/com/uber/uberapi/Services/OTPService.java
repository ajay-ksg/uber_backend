package com.uber.uberapi.Services;

import com.uber.uberapi.models.OTP;
import org.springframework.stereotype.Service;

@Service
public interface OTPService {
    void sendPhoneNumberVerificationOTP(OTP otp);
    void sendRideStartOTP(OTP otp);
}
