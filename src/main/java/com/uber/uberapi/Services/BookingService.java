package com.uber.uberapi.Services;

import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Driver;

public interface BookingService {

    void acceptBooking(Booking booking, Driver driver);

    void cancelBooking(Booking booking, Driver driver);

    void cancelByDriver(Booking booking, Driver driver);
}
