package com.uber.uberapi.models;

import com.uber.uberapi.Exceptions.UnapprovedDriverException;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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


    @OneToOne(mappedBy = "driver")
    private Car car;

    private String licenseDetails;
    private Date dob;

    @Enumerated(value = EnumType.STRING)
    private DriverApprovalStatus approvalStatus;

    @OneToMany(mappedBy = "driver")
    private List<Booking> bookings = new ArrayList<>();

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
}
