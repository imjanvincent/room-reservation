package com.mashreq.booking.service.impl;

import com.mashreq.booking.entity.BookedRoomEntity;
import com.mashreq.booking.entity.ConferenceRoomsEntity;
import com.mashreq.booking.entity.MaintenanceTimeEntity;
import com.mashreq.booking.enums.AppErrorCode;
import com.mashreq.booking.exception.AppException;
import com.mashreq.booking.model.BookingRequest;
import com.mashreq.booking.model.BookingResponse;
import com.mashreq.booking.model.ViewRoomRequest;
import com.mashreq.booking.model.ViewRoomResponse;
import com.mashreq.booking.repo.BookedRoomRepository;
import com.mashreq.booking.repo.ConferenceRoomRepository;
import com.mashreq.booking.repo.MaintenanceTimeRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;


/**
 * The type Booking service impl test.
 *
 * @author janv @mashreq.com
 */
@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {

    @InjectMocks
    private BookingServiceImpl bookingService;

    @Mock
    private ConferenceRoomRepository conferenceRoomRepository;

    @Mock
    private BookedRoomRepository bookedRoomRepository;

    @Mock
    private MaintenanceTimeRepository maintenanceTimeRepository;

    /**
     * Test book conference room request within maintenance time.
     */
    @Test
    void testBookConferenceRoom_requestWithinMaintenanceTime() {
        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(8,0));
        bookingRequest.setEndTime(LocalTime.of(8,15));
        AppException exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.ROOM_MAINTENANCE_TIME.getErrorCode(), exception.getErrorCode());

        BookingRequest  bookingRequest2 = new BookingRequest();
        bookingRequest2.setStartTime(LocalTime.of(7,0));
        bookingRequest2.setEndTime(LocalTime.of(8,30));
        exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest2));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.ROOM_MAINTENANCE_TIME.getErrorCode(), exception.getErrorCode());

        BookingRequest  bookingRequest3 = new BookingRequest();
        bookingRequest3.setStartTime(LocalTime.of(7,0));
        bookingRequest3.setEndTime(LocalTime.of(8,10));
        exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest3));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.ROOM_MAINTENANCE_TIME.getErrorCode(), exception.getErrorCode());

        BookingRequest  bookingRequest4 = new BookingRequest();
        bookingRequest4.setStartTime(LocalTime.of(8,5));
        bookingRequest4.setEndTime(LocalTime.of(8,10));
        exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest4));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.ROOM_MAINTENANCE_TIME.getErrorCode(), exception.getErrorCode());

        BookingRequest  bookingRequest5 = new BookingRequest();
        bookingRequest5.setStartTime(LocalTime.of(8,5));
        bookingRequest5.setEndTime(LocalTime.of(8,20));
        exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest5));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.ROOM_MAINTENANCE_TIME.getErrorCode(), exception.getErrorCode());

        BookingRequest  bookingRequest6 = new BookingRequest();
        bookingRequest6.setStartTime(LocalTime.of(6,5));
        bookingRequest6.setEndTime(LocalTime.of(10,20));
        exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest6));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.ROOM_MAINTENANCE_TIME.getErrorCode(), exception.getErrorCode());
    }


    /**
     * Test book conference room max room capacity exception.
     */
    @Test
    void testBookConferenceRoom_maxRoomCapacityException() {
        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);

        List<ConferenceRoomsEntity> conferenceRooms = new ArrayList<>();
        conferenceRooms.add(new ConferenceRoomsEntity(1L, "Strive", 20));
        Mockito.when(conferenceRoomRepository.findAll()).thenReturn(conferenceRooms);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(8,15));
        bookingRequest.setEndTime(LocalTime.of(8,30));
        bookingRequest.setPersons(22);
        AppException exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.MAX_CAPACITY.getErrorCode(), exception.getErrorCode());
    }

    /**
     * Test book conference room no room found exception.
     */
    @Test
    void testBookConferenceRoom_noRoomFoundException() {
        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);

        List<ConferenceRoomsEntity> conferenceRooms = new ArrayList<>();
        conferenceRooms.add(new ConferenceRoomsEntity(1L, "Amaze", 3));
        conferenceRooms.add(new ConferenceRoomsEntity(2L, "Beauty", 7));
        Mockito.when(conferenceRoomRepository.findAll()).thenReturn(conferenceRooms);

        List<BookedRoomEntity> beautyRoomList = new ArrayList<>();
        BookedRoomEntity beautyRoom = new BookedRoomEntity();
        beautyRoom.setStartTime("08:15");
        beautyRoom.setEndTime("08:30");
        beautyRoomList.add(beautyRoom);
        Mockito.when(bookedRoomRepository.findByRoomName("Beauty")).thenReturn(beautyRoomList);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(8,15));
        bookingRequest.setEndTime(LocalTime.of(8,30));
        bookingRequest.setPersons(7);
        AppException exception = Assertions.assertThrows(AppException.class, () -> bookingService.bookConferenceRoom(bookingRequest));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.NO_ROOMS_FOUND.getErrorCode(), exception.getErrorCode());
    }

    /**
     * Test book conference room save booking details.
     */
    @Test
    void testBookConferenceRoom_saveBookingDetails() {
        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);

        List<ConferenceRoomsEntity> conferenceRooms = new ArrayList<>();
        conferenceRooms.add(new ConferenceRoomsEntity(1L, "Amaze", 3));
        conferenceRooms.add(new ConferenceRoomsEntity(2L, "Beauty", 7));
        conferenceRooms.add(new ConferenceRoomsEntity(3L, "Inspire", 12));
        conferenceRooms.add(new ConferenceRoomsEntity(4L, "Strive", 20));
        Mockito.when(conferenceRoomRepository.findAll()).thenReturn(conferenceRooms);

        List<BookedRoomEntity> beautyRoomList = new ArrayList<>();
        BookedRoomEntity beautyRoom = new BookedRoomEntity();
        beautyRoom.setStartTime("08:15");
        beautyRoom.setEndTime("08:30");
        beautyRoomList.add(beautyRoom);
        Mockito.when(bookedRoomRepository.findByRoomName("Beauty")).thenReturn(beautyRoomList);
        Mockito.when(bookedRoomRepository.findByRoomName("Inspire")).thenReturn(new ArrayList<>());

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(8,15));
        bookingRequest.setEndTime(LocalTime.of(8,30));
        bookingRequest.setPersons(7);
        BookingResponse actual = bookingService.bookConferenceRoom(bookingRequest);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Inspire", actual.getRoom());
    }

    /**
     * Test book conference room save booking details for available time.
     */
    @Test
    void testBookConferenceRoom_saveBookingDetailsForAvailableTime() {
        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);

        List<ConferenceRoomsEntity> conferenceRooms = new ArrayList<>();
        conferenceRooms.add(new ConferenceRoomsEntity(1L, "Amaze", 3));
        conferenceRooms.add(new ConferenceRoomsEntity(2L, "Beauty", 7));
        conferenceRooms.add(new ConferenceRoomsEntity(3L, "Inspire", 12));
        conferenceRooms.add(new ConferenceRoomsEntity(4L, "Strive", 20));
        Mockito.when(conferenceRoomRepository.findAll()).thenReturn(conferenceRooms);

        List<BookedRoomEntity> beautyRoomList = new ArrayList<>();
        BookedRoomEntity beautyRoom = new BookedRoomEntity();
        beautyRoom.setStartTime("08:15");
        beautyRoom.setEndTime("08:30");
        beautyRoomList.add(beautyRoom);
        Mockito.when(bookedRoomRepository.findByRoomName("Beauty")).thenReturn(beautyRoomList);
        List<BookedRoomEntity> inspireRoomList = new ArrayList<>();
        BookedRoomEntity inspireRoom = new BookedRoomEntity();
        inspireRoom.setStartTime("09:15");
        inspireRoom.setEndTime("09:30");
        inspireRoomList.add(inspireRoom);
        Mockito.when(bookedRoomRepository.findByRoomName("Inspire")).thenReturn(inspireRoomList);

        BookingRequest bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(8,15));
        bookingRequest.setEndTime(LocalTime.of(8,30));
        bookingRequest.setPersons(7);
        BookingResponse actual = bookingService.bookConferenceRoom(bookingRequest);
        Assertions.assertNotNull(actual);
        Assertions.assertEquals("Inspire", actual.getRoom());
    }

    /**
     * Test find available rooms more than allowed booking time.
     */
    @Test
    void testFindAvailableRooms_moreThanAllowedBookingTime() {
        ViewRoomRequest viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(23,45));
        AppException exception = Assertions.assertThrows(AppException.class, () -> bookingService.findAvailableRooms(viewRoomRequest));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.NO_ROOMS_FOUND.getErrorCode(), exception.getErrorCode());

        ViewRoomRequest viewRoomRequest2 = new ViewRoomRequest();
        viewRoomRequest2.setStartTime(LocalTime.of(23,50));
        exception = Assertions.assertThrows(AppException.class, () -> bookingService.findAvailableRooms(viewRoomRequest2));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.NO_ROOMS_FOUND.getErrorCode(), exception.getErrorCode());
    }

    /**
     * Test find available rooms empty available rooms.
     */
    @Test
    void testFindAvailableRooms_emptyAvailableRooms() {
        ViewRoomRequest viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(8,0));
        viewRoomRequest.setEndTime(LocalTime.of(8,15));

        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);

        AppException exception = Assertions.assertThrows(AppException.class, () -> bookingService.findAvailableRooms(viewRoomRequest));
        Assertions.assertNotNull(exception);
        Assertions.assertEquals(AppErrorCode.NO_ROOMS_FOUND.getErrorCode(), exception.getErrorCode());
    }

    /**
     * Test find available rooms no booked rooms.
     */
    @Test
    void testFindAvailableRooms_noBookedRooms() {
        ViewRoomRequest viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(8,0));
        viewRoomRequest.setEndTime(LocalTime.of(10,15));

        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);


        List<ConferenceRoomsEntity> conferenceRooms = new ArrayList<>();
        conferenceRooms.add(new ConferenceRoomsEntity(1L, "Amaze", 3));
        conferenceRooms.add(new ConferenceRoomsEntity(2L, "Beauty", 7));
        conferenceRooms.add(new ConferenceRoomsEntity(3L, "Inspire", 12));
        conferenceRooms.add(new ConferenceRoomsEntity(4L, "Strive", 20));
        Mockito.when(conferenceRoomRepository.findAll()).thenReturn(conferenceRooms);

        ViewRoomResponse availableRooms = bookingService.findAvailableRooms(viewRoomRequest);
        Assertions.assertNotNull(availableRooms);
        Assertions.assertFalse(availableRooms.getAvailableRooms().isEmpty());
        Assertions.assertEquals(4, availableRooms.getAvailableRooms().size());
    }


    /**
     * Test find available rooms with booked rooms.
     */
    @Test
    void testFindAvailableRooms_withBookedRooms() {
        ViewRoomRequest viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(8,0));
        viewRoomRequest.setEndTime(LocalTime.of(10,15));

        List<MaintenanceTimeEntity> maintenanceTimeEntities = new ArrayList<>();
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(1L, "08:00", "08:15"));
        maintenanceTimeEntities.add(new MaintenanceTimeEntity(2L, "09:00", "09:15"));
        Mockito.when(maintenanceTimeRepository.findAll()).thenReturn(maintenanceTimeEntities);


        List<ConferenceRoomsEntity> conferenceRooms = new ArrayList<>();
        conferenceRooms.add(new ConferenceRoomsEntity(1L, "Amaze", 3));
        conferenceRooms.add(new ConferenceRoomsEntity(2L, "Beauty", 7));
        conferenceRooms.add(new ConferenceRoomsEntity(3L, "Inspire", 12));
        conferenceRooms.add(new ConferenceRoomsEntity(4L, "Strive", 20));
        Mockito.when(conferenceRoomRepository.findAll()).thenReturn(conferenceRooms);

        List<BookedRoomEntity> bookedRoomList = new ArrayList<>();
        BookedRoomEntity amaze = new BookedRoomEntity();
        amaze.setRoomName("Amaze");
        amaze.setStartTime("08:15");
        amaze.setEndTime("08:30");
        bookedRoomList.add(amaze);
        amaze = new BookedRoomEntity();
        amaze.setRoomName("Amaze");
        amaze.setStartTime("08:30");
        amaze.setEndTime("08:45");
        bookedRoomList.add(amaze);
        BookedRoomEntity beauty = new BookedRoomEntity();
        beauty.setRoomName("Beauty");
        beauty.setStartTime("09:15");
        beauty.setEndTime("10:15");
        bookedRoomList.add(amaze);
        Mockito.when(bookedRoomRepository.getBookedRoomByTime(viewRoomRequest.getStartTime().toString(), viewRoomRequest.getEndTime().toString())).thenReturn(bookedRoomList);

        ViewRoomResponse availableRooms = bookingService.findAvailableRooms(viewRoomRequest);
        Assertions.assertNotNull(availableRooms);
        Assertions.assertFalse(availableRooms.getAvailableRooms().isEmpty());
        Assertions.assertEquals(4, availableRooms.getAvailableRooms().size());
    }
}