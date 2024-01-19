package com.mashreq.booking.repo;

import com.mashreq.booking.entity.BookedRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * @author janv@mashreq.com
 */
public interface BookedRoomRepository extends JpaRepository<BookedRoomEntity, Long> {

    @Query(value = "select c from BookedRoomEntity c where c.startTime BETWEEN :startTime and :endTime")
    List<BookedRoomEntity> getBookedRoomByTime(String startTime, String endTime);

    List<BookedRoomEntity> findByRoomName(String roomName);
}
