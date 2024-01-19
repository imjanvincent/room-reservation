package com.mashreq.booking.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @author janv@mashreq.com
 */
@Data
@NoArgsConstructor
@Entity
@Table(name = "BOOKED_ROOM")
public class BookedRoomEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ROOM_NAME")
    private String roomName;

    @Column(name = "START_TIME", length = 6, nullable = false)
    private String startTime;

    @Column(name = "END_TIME", length = 6, nullable = false)
    private String endTime;

    @Column(name = "NUMBER_OF_PERSONS")
    private Integer numberOfPersons;

    @Column(name = "BOOKING_REFERENCE")
    private String bookingReference;

    @Column(name = "BOOKED_BY")
    private String bookedBy;

    @Column(name = "BOOKING_DATE_TIME")
    private LocalDateTime bookingDateTime;
}
