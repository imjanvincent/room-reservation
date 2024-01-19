package com.mashreq.booking.controller;

import com.mashreq.booking.model.BookingRequest;
import com.mashreq.booking.model.BookingResponse;
import com.mashreq.booking.model.Response;
import com.mashreq.booking.model.ViewRoomRequest;
import com.mashreq.booking.model.ViewRoomResponse;
import com.mashreq.booking.service.BookingService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;

import java.time.LocalTime;
import java.util.ArrayList;

/**
 * The type Conference room controller test.
 *
 * @author janv @mashreq.com
 */
@ExtendWith(MockitoExtension.class)
class ConferenceRoomControllerTest {

    @InjectMocks
    private ConferenceRoomController conferenceRoomController;

    @Mock
    private BookingService bookingService;

    /**
     * Test book room.
     */
    @Test
    void testBookRoom() {
        BookingRequest bookingRequest = new BookingRequest();
        BookingResponse bookingResponse = new BookingResponse("Amaze", LocalTime.of(8, 0), LocalTime.of(8, 15));
        Mockito.when(bookingService.bookConferenceRoom(bookingRequest)).thenReturn(bookingResponse);
        ResponseEntity<Object> actual = conferenceRoomController.bookRoom(bookingRequest);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(HttpStatusCode.valueOf(200), actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Response actualResponse = (Response) actual.getBody();
        Assertions.assertNotNull(actualResponse.getData());
        BookingResponse actualBookingResponse = (BookingResponse) actualResponse.getData();
        Assertions.assertEquals(actualBookingResponse.getRoom(), bookingResponse.getRoom());
    }

    /**
     * Test view room.
     */
    @Test
    void testViewRoom() {
        ViewRoomResponse viewRoomResponse = new ViewRoomResponse();
        viewRoomResponse.setAvailableRooms(new ArrayList<>());
        viewRoomResponse.getAvailableRooms().add(new ViewRoomResponse.RoomDetails("Amaze", 3, new ArrayList<>()));
        ViewRoomRequest viewRoomRequest = new ViewRoomRequest();
        Mockito.when(bookingService.findAvailableRooms(viewRoomRequest)).thenReturn(viewRoomResponse);
        ResponseEntity<Object> actual = conferenceRoomController.viewAvailableRooms(viewRoomRequest);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals(HttpStatusCode.valueOf(200), actual.getStatusCode());
        Assertions.assertNotNull(actual.getBody());
        Response actualResponse = (Response) actual.getBody();
        Assertions.assertNotNull(actualResponse.getData());
        ViewRoomResponse actualViewRoomResponse = (ViewRoomResponse) actualResponse.getData();
        Assertions.assertFalse(actualViewRoomResponse.getAvailableRooms().isEmpty());
    }
}