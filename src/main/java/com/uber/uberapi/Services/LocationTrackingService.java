package com.uber.uberapi.Services;

import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.ExactLocation;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LocationTrackingService {
    List<Driver> getDriversNearLocation(ExactLocation pickup);
}
