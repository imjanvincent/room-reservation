package com.mashreq.booking.model;

import com.mashreq.booking.validation.annotation.ValidBooking;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * @author janv@mashreq.com
 */
@Data
@ValidBooking
public class BookingRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = 7339415592961864466L;

    private int persons;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private String userName;
}
