package com.uber.uberapi.Services;

import com.uber.uberapi.Services.Notification.NotificationService;
import com.uber.uberapi.Services.messagequeue.MQMessage;
import com.uber.uberapi.Services.messagequeue.MessageQueue;
import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.Driver;
import com.uber.uberapi.models.ExactLocation;
import com.uber.uberapi.repositories.BookingRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverMatchingService {

    @Autowired
    MessageQueue messageQueue;
    @Autowired
    Constants constants;

    @Autowired
    LocationTrackingService locationTrackingService;

    @Autowired
    NotificationService notificationService;

    @Autowired
    BookingRepository bookingRepository;

    @Scheduled(fixedRate = 1000)
    public void consumer(){
       MQMessage m =  messageQueue.consumeMessage(constants.getDriverMatchingTopicName());
       if(m == null)
           return;
        Message message = (Message)m;
        this.findNearByDrivers(message.getBooking());

    }

   private void findNearByDrivers(Booking booking){
       ExactLocation pickup = booking.getPickUpLocation();
       List<Driver> drivers = locationTrackingService.getDriversNearLocation(pickup);

       if(drivers.size() == 0)
       {
           //todo: add surge to this area So tht drivers can come here.
           notificationService.notify(booking.getPassenger().getPhoneNumber(),"no cabs near you");
           return ;
       }

       notificationService.notify(booking.getPassenger().getPhoneNumber(),String.format("connecting %s near by drivers",drivers.size()));
       //filter drivers
       //todo: chain of responsibility comes under consideration.


       //after filtering
       if(drivers.size() == 0)
       {
           //todo: add surge to this area So tht drivers can come here.
           notificationService.notify(booking.getPassenger().getPhoneNumber(),"no cabs near you");
           return ;
       }

        drivers.forEach(driver -> {
            notificationService.notify(driver.getPhoneNumber(),"Booking near you"+ booking.toString());
            driver.getAcceptableBookings().add(booking);
        });
        bookingRepository.save(booking);
   }

    @Getter
    @Setter
    @AllArgsConstructor
    public static class Message implements MQMessage {
        Booking booking;
    }


}
