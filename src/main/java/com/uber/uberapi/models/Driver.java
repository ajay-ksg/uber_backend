package com.uber.uberapi.models;

import com.uber.uberapi.Exceptions.UnapprovedDriverException;
import lombok.*;

import javax.persistence.*;
import java.util.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Driver extends Auditable {
    @OneToOne
    private Account account;

    private Gender gender;
    private String name;

    private String phoneNumber;

    @OneToOne(mappedBy = "driver")
    private Car car;

    private String licenseDetails;
    private Date dob;

    @Enumerated(value = EnumType.STRING)
    private DriverApprovalStatus approvalStatus;

    @OneToMany(mappedBy = "driver") //bookings that driver actually drove
    private List<Booking> bookings = new ArrayList<>();

    @ManyToMany(mappedBy = "notifiedDrivers",cascade = CascadeType.PERSIST) //bookings that driver actually drove
    private Set<Booking> acceptableBookings = new HashSet<>();

    @OneToOne
    private Booking activeBooking = null;



    private boolean isAvailable;

    private String activeCity;

    @OneToOne
    private NamedLocation lastKnowLocation;

    @OneToOne
    private NamedLocation home;

    @OneToOne
    private NamedLocation work;

    public void setAvailablity(Boolean available) {
        if(!approvalStatus.equals(DriverApprovalStatus.APPROVED) ){
            throw new UnapprovedDriverException("Driver approval is denied ID" + getId());
        }
        isAvailable = available;
    }

    public boolean canAcceptBooking(Integer maxWaitTimeForPreviousRide) {
        if(isAvailable && activeBooking == null){
            return true;
        }
        return activeBooking.getExpectedCompletionTime().before(DateUtils.addMinutes(new Date(), maxWaitTimeForPreviousRide));
    }
}
