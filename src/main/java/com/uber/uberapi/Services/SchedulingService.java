package com.uber.uberapi.Services;

import com.uber.uberapi.Services.Notification.NotificationService;
import com.uber.uberapi.Services.messagequeue.MQMessage;
import com.uber.uberapi.Services.messagequeue.MessageQueue;
import com.uber.uberapi.models.Booking;
import com.uber.uberapi.models.DateUtils;
import com.uber.uberapi.repositories.BookingRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Service
public class SchedulingService  {

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

    @Autowired
    BookingService bookingService;

    private Set<Booking> scheduledBookings = new HashSet<>();

    @Scheduled(fixedRate = 1000)
    public void consumer(){
        MQMessage m =  messageQueue.consumeMessage(constants.getSchedulingTopicName());
        if(m == null)
            return;
        DriverMatchingService.Message message = (DriverMatchingService.Message)m;
        this.schedule(message.getBooking());

    }

    private void schedule(Booking booking) {
        scheduledBookings.add(booking);
    }

    @Scheduled(fixedRate = 60000)
    public void process(){
        Set<Booking> newScheduledBookings = new HashSet<>();
        for(Booking booking : scheduledBookings){
            if(DateUtils.addMinutes(new Date(), constants.getBookingProcessingTimeForSchedulingInMinutes()).after(booking.getScheduledTime())){
                bookingService.acceptBooking(booking,booking.getDriver());
            }
            else{
                newScheduledBookings.add(booking);
            }
        }

        scheduledBookings = newScheduledBookings;

    }
    @Getter
    @Setter
    @AllArgsConstructor
    public  static  class Message implements MQMessage {
        private Booking booking;
    }
}
