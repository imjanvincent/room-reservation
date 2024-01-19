package com.mashreq.booking.validation;

/**
 * @author janv@mashreq.com
 */
public class ValidationConstants {

    public ValidationConstants() {
        // DEFAULT CONSTRUCTOR
    }

    public static final String INPUT_VALID_MINUTE_INTERVAL = "Please input time range with 15 minutes interval";
    public static final String INPUT_VALID_MINUTE_VALUE = "Please input 0, 15, 30 or 45 minute value";
    public static final String INVALID_END_TIME = "Room reservation is allowed only for the current date";
    public static final String INVALID_NUMBER_OF_PERSON = "Conference room is not allowed to book for one person";
    public static final String INVALID_START_TIME = "Start time cannot be less than the current time";
    public static final String END_TIME_LESS_THAN_START_TIME = "End time cannot be less than the start time";
    public static final String END_TIME_LESS_THAN_CURRENT_TIME = "End time cannot be less than the current time";
}
