package com.mashreq.booking.repo;

import com.mashreq.booking.entity.ConferenceRoomsEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author janv@mashreq.com
 */
public interface ConferenceRoomRepository extends JpaRepository<ConferenceRoomsEntity, Long> {
}
