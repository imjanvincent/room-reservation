package com.mashreq.booking.service.impl;

import com.mashreq.booking.constants.AppConstants;
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
import com.mashreq.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * @author janv@mashreq.com
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class BookingServiceImpl implements BookingService {

    private final ConferenceRoomRepository conferenceRoomRepository;
    private final BookedRoomRepository bookedRoomRepository;
    private final MaintenanceTimeRepository maintenanceTimeRepository;

    /**
     * Saves a booking request to the system, recording details such as booking time, duration and user name.
     *
     * @param bookingRequest the booking request
     * @return the booking response
     */
    @Override
    public BookingResponse bookConferenceRoom(BookingRequest bookingRequest) {

        this.validateRequestTimeForMaintenance(bookingRequest);

        List<ConferenceRoomsEntity> conferenceRooms = conferenceRoomRepository.findAll();
        String idealConferenceRoom = getIdealConferenceRoom(conferenceRooms, bookingRequest);
        if (StringUtils.isBlank(idealConferenceRoom)) {
            log.error("No ideal conference room found for the given request");
            throw new AppException(AppErrorCode.NO_ROOMS_FOUND);
        }

        LocalTime requestStartTime = bookingRequest.getStartTime();
        LocalTime requestEndTime = bookingRequest.getEndTime();
        List<String> availableRoomTimes = generateBookingTimes(requestStartTime, requestEndTime);

        this.saveBookingDetails(bookingRequest, idealConferenceRoom, availableRoomTimes);
        return new BookingResponse(idealConferenceRoom, requestStartTime, requestEndTime);
    }

    /**
     * Validate the booking time range do not overlap with the maintenance timings
     *
     * @param bookingRequest the booking request
     */
    private void validateRequestTimeForMaintenance(BookingRequest bookingRequest) {
        List<MaintenanceTimeEntity> maintenanceTimeEntities = maintenanceTimeRepository.findAll();
        for (MaintenanceTimeEntity maintenanceTime : maintenanceTimeEntities) {
            LocalTime requestStartTime = bookingRequest.getStartTime();
            LocalTime requestEndTime = bookingRequest.getEndTime();
            LocalTime maintenanceStartTime = LocalTime.parse(maintenanceTime.getStartTime());
            LocalTime maintenanceEndTime = LocalTime.parse(maintenanceTime.getEndTime());
            if (isTimeCrossing(requestStartTime, requestEndTime, maintenanceStartTime, maintenanceEndTime)) {
                log.error("Requested time range overlaps with the maintenance time");
                throw new AppException(AppErrorCode.ROOM_MAINTENANCE_TIME);
            }
        }
    }

    /**
     * Validate the given time if it crosses the time range
     *
     * @param start1 the request start time
     * @param end1   the request end time
     * @param start2 the start time to check
     * @param end2   the end time to check
     * @return true if time crosses the time range
     */
    private boolean isTimeCrossing(LocalTime start1, LocalTime end1, LocalTime start2, LocalTime end2) {
        return start1.isBefore(end2) && end1.isAfter(start2);
    }

    /**
     * Get the ideal conference room based on the given number of people, time range and availability of the room
     *
     * @param conferenceRooms the list of conference rooms
     * @param bookingRequest  the booking request
     * @return the ideal conference room
     */
    private String getIdealConferenceRoom(List<ConferenceRoomsEntity> conferenceRooms, BookingRequest bookingRequest) {
        String idealRoom = null;
        int minCapacityDifference = getMaxRoomCapacity(conferenceRooms);
        int requestedPersonCount = bookingRequest.getPersons();
        // Validate that requested person count can fit the largest room
        if (requestedPersonCount > minCapacityDifference) {
            log.error("Requested number of persons is greater than the largest room capacity");
            throw new AppException(AppErrorCode.MAX_CAPACITY);
        }
        for (ConferenceRoomsEntity rooms : conferenceRooms) {
            int roomCapacity = rooms.getCapacity();
            if (requestedPersonCount > roomCapacity) {
                // Skip this room if requested person count cannot fit the room
                continue;
            }
            int capacityDifference = Math.abs(requestedPersonCount - roomCapacity);
            if (capacityDifference < minCapacityDifference) {
                // Check if the room name has an existing booking
                List<BookedRoomEntity> bookedRoomList = bookedRoomRepository.findByRoomName(rooms.getName());
                if (CollectionUtils.isEmpty(bookedRoomList) || !isAlreadyBooked(bookedRoomList, bookingRequest)) {
                    // Assign room name if no booking reservation found
                    minCapacityDifference = capacityDifference;
                    idealRoom = rooms.getName();
                }
            }
        }
        return idealRoom;
    }

    /**
     * Check if the room name has an existing booking
     *
     * @param bookedRoomList the list of booked rooms
     * @param bookingRequest the booking request
     * @return true if room already booked
     */
    private boolean isAlreadyBooked(List<BookedRoomEntity> bookedRoomList, BookingRequest bookingRequest) {
        return bookedRoomList.stream().anyMatch(entity -> isTimeCrossing(LocalTime.parse(entity.getStartTime()), LocalTime.parse(entity.getEndTime()), bookingRequest.getStartTime(), bookingRequest.getEndTime()));
    }

    /**
     * Get the largest room capacity from the list of conference rooms
     *
     * @param conferenceRooms the list of conference rooms
     * @return the max room capacity
     */
    private int getMaxRoomCapacity(List<ConferenceRoomsEntity> conferenceRooms) {
        return conferenceRooms.stream()
                .mapToInt(ConferenceRoomsEntity::getCapacity)
                .max()
                .orElse(1);
    }

    /**
     * Save the booking details to the system
     *
     * @param bookingRequest      the booking request
     * @param idealConferenceRoom the ideal conference room
     * @param availableRoomTimes  the list of available room times
     */
    private void saveBookingDetails(BookingRequest bookingRequest, String idealConferenceRoom, List<String> availableRoomTimes) {
        String bookingReference = UUID.randomUUID().toString();
        LocalDateTime bookingDateTime = LocalDateTime.now();
        log.info("Saving booking details under reference id {}", bookingReference);
        for (String time : availableRoomTimes) {
            String startTime = StringUtils.normalizeSpace(StringUtils.substringBefore(time, AppConstants.DASH_SPLITTER));
            String endTime = StringUtils.normalizeSpace(StringUtils.substringAfter(time, AppConstants.DASH_SPLITTER));
            BookedRoomEntity bookedRoomEntity = new BookedRoomEntity();
            bookedRoomEntity.setRoomName(idealConferenceRoom);
            bookedRoomEntity.setStartTime(startTime);
            bookedRoomEntity.setEndTime(endTime);
            bookedRoomEntity.setNumberOfPersons(bookingRequest.getPersons());
            bookedRoomEntity.setBookedBy(StringUtils.defaultIfBlank(bookingRequest.getUserName(), AppConstants.DEFAULT_USER_NAME));
            bookedRoomEntity.setBookingReference(bookingReference);
            bookedRoomEntity.setBookingDateTime(bookingDateTime);
            bookedRoomRepository.save(bookedRoomEntity);
        }
        log.info("Conference room {} successfully booked under reference id {}", idealConferenceRoom, bookingReference);
    }

    /**
     * Find available rooms view room response by the given time range
     *
     * @param viewRoomRequest the view room request
     * @return the view room response
     */
    @Override
    public ViewRoomResponse findAvailableRooms(ViewRoomRequest viewRoomRequest) {
        LocalTime requestStartTime = viewRoomRequest.getStartTime();
        LocalTime requestEndTime = viewRoomRequest.getEndTime();
        if (requestStartTime.equals(LocalTime.of(23, 45)) || requestStartTime.isAfter(LocalTime.of(23, 45))) {
            log.error("No available rooms found for the end of the day time range");
            throw new AppException(AppErrorCode.NO_ROOMS_FOUND);
        }
        // Generate the booking timings with 15 minutes interval based on the given time range
        List<String> availableTimes = generateBookingTimes(requestStartTime, requestEndTime);

        // Get the maintenance timings and remove the time from the list of booking times
        List<MaintenanceTimeEntity> maintenanceTimeEntities = maintenanceTimeRepository.findAll();
        List<String> maintenanceList = maintenanceTimeEntities.stream().map(x -> String.format(AppConstants.TIME_RANGE_FORMATTER, x.getStartTime(), x.getEndTime())).toList();
        // Remove maintenance times from the available room times
        availableTimes.removeAll(maintenanceList);

        if (CollectionUtils.isEmpty(availableTimes)) {
            log.error("No available rooms found for the given time range");
            throw new AppException(AppErrorCode.NO_ROOMS_FOUND);
        }

        ViewRoomResponse viewRoomResponse = new ViewRoomResponse();
        List<ViewRoomResponse.RoomDetails> availableRooms = new ArrayList<>();
        // Collect all conference rooms
        List<ConferenceRoomsEntity> conferenceRooms = conferenceRoomRepository.findAll();
        for (ConferenceRoomsEntity room : conferenceRooms) {
            availableRooms.add(new ViewRoomResponse.RoomDetails(room.getName(), room.getCapacity(), availableTimes));
        }

        // Map the booked timings for each room
        Map<String, List<String>> bookedRoomMap = new HashMap<>();
        List<BookedRoomEntity> bookedRoomList = bookedRoomRepository.getBookedRoomByTime(requestStartTime.toString(), requestEndTime.toString());
        if (CollectionUtils.isNotEmpty(bookedRoomList)) {
            List<String> bookedRoomTiming = new ArrayList<>();
            for (BookedRoomEntity bookedRoom : bookedRoomList) {
                bookedRoomTiming.add(String.format(AppConstants.TIME_RANGE_FORMATTER, bookedRoom.getStartTime(), bookedRoom.getEndTime()));
                bookedRoomMap.put(bookedRoom.getRoomName(), bookedRoomTiming);
            }
        }

        for (ViewRoomResponse.RoomDetails roomDetails : availableRooms) {
            String room = roomDetails.getRoom();
            List<String> bookedRoomTimings = bookedRoomMap.get(room);
            if (CollectionUtils.isNotEmpty(bookedRoomTimings)) {
                // Remove the booked timings from the generated list of available time
                List<String> filteredTiming = new ArrayList<>(roomDetails.getTime());
                filteredTiming.removeAll(bookedRoomTimings);
                roomDetails.setTime(filteredTiming);
            }
        }

        viewRoomResponse.setAvailableRooms(availableRooms);
        return viewRoomResponse;
    }


    /**
     * Create the list of time range from the given start and end time
     *
     * @param startTime the requested start time
     * @param endTime   the requested end time
     * @return the list of booking times
     */
    private List<String> generateBookingTimes(LocalTime startTime, LocalTime endTime) {
        List<String> bookingTimes = new ArrayList<>();
        LocalTime currentTime = startTime;
        int intervalMinutes = 15;
        while (currentTime.isBefore(endTime)) {
            bookingTimes.add(String.format(AppConstants.TIME_RANGE_FORMATTER, currentTime, currentTime.plusMinutes(intervalMinutes)));
            currentTime = currentTime.plusMinutes(intervalMinutes);
        }
        return bookingTimes;
    }

}
