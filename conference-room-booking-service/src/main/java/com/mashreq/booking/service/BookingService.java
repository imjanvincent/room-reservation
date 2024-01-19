package com.mashreq.booking.service;

import com.mashreq.booking.model.BookingRequest;
import com.mashreq.booking.model.BookingResponse;
import com.mashreq.booking.model.ViewRoomRequest;
import com.mashreq.booking.model.ViewRoomResponse;

/**
 * The interface Booking service.
 *
 * @author janv @mashreq.com
 */
public interface BookingService {

    /**
     * Saves a booking request to the system, recording details such as booking time, duration and user name.
     *
     * @param bookingRequest the booking request
     * @return the booking response
     */
    BookingResponse bookConferenceRoom(BookingRequest bookingRequest);

    /**
     * Find available rooms view room response by the given time range
     *
     * @param viewRoomRequest the view room request
     * @return the view room response
     */
    ViewRoomResponse findAvailableRooms(ViewRoomRequest viewRoomRequest);

}
