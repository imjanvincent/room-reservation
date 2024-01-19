package com.mashreq.booking.model;

import com.mashreq.booking.validation.annotation.ValidViewRoom;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalTime;

/**
 * @author janv@mashreq.com
 */
@Data
@ValidViewRoom
public class ViewRoomRequest implements Serializable {

    @Serial
    private static final long serialVersionUID = -5155381267741980831L;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

}
