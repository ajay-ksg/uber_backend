package com.uber.uberapi.Services;

import com.uber.uberapi.models.OTP;

public interface OTPService {
    void sendPhoneNumberVerificationOTP(OTP otp);
    void sendRideStartOTP(OTP otp);
}
