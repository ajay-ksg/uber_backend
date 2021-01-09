package com.uber.uberapi.Services;

import com.uber.uberapi.repositories.DBConstantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class Constants {
    @Autowired
    DBConstantRepository dbConstantRepository;

    Map<String,String> constants = new HashMap<>();
    private static final Integer TEN_MINUTES = 60*10*1000;
    public Constants(DBConstantRepository dbConstantRepository){
        this.dbConstantRepository = dbConstantRepository;
        loadConstantsFromDB();

    }

    @Scheduled(fixedRate = TEN_MINUTES)
    private void loadConstantsFromDB() {
        dbConstantRepository.findAll().forEach(dbConstant -> {
            constants.put(dbConstant.getName(),dbConstant.getValue());
        });
    }

    public Integer getRideStartOYPExpiryMinutes(){
        return Integer.parseInt(constants.getOrDefault("rideStartOTPExpiryMinutes","36000"));
    }


    public String getSchedulingTopicName() {
        return constants.getOrDefault("schedulingTopicName", "schedulingServiceTopic");
    }

    public String getDriverMatchingTopicName() {
        return constants.getOrDefault("driverMatchingTopicName","driverMatchingServiceTopic");
    }

    public Integer getMaxWaitTimeForPreviousRide() {
        return Integer.parseInt(constants.getOrDefault("maxWaitTimeForPreviousRide","10"));
    }

    public Integer getBookingProcessingTimeForSchedulingInMinutes() {
        return Integer.parseInt(constants.getOrDefault("bookingProcessingTimeForScheduling","10"));
    }
}
