package com.uber.uberapi.models;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Table;


public interface constants  {
    Long MINUTES_PER_DAY = 24L*60;
    Long RIDE_START_OTP_EXPIRY_MINUTES = MINUTES_PER_DAY*72;
}
