package com.mashreq.booking.repo;

import com.mashreq.booking.entity.MaintenanceTimeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author janv@mashreq.com
 */
public interface MaintenanceTimeRepository extends JpaRepository<MaintenanceTimeEntity, Long> {
}
