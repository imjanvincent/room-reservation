package com.mashreq.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * @author janv@mashreq.com
 */
@Data
public class ViewRoomResponse implements Serializable {


    @Serial
    private static final long serialVersionUID = -7611348432115128911L;

    private List<RoomDetails> availableRooms;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomDetails implements Serializable {

        @Serial
        private static final long serialVersionUID = -8663132254581082545L;

        private String room;
        private int capacity;
        private List<String> time;
    }
}
