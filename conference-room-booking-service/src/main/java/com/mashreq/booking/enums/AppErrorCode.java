package com.mashreq.booking.enums;

/**
 * @author janv@mashreq.com
 */
public enum AppErrorCode {

    SYSTEM_ERROR("SERVICE-ERROR-0000", "Something went wrong with the service"),
    NO_ROOMS_FOUND("ERROR-0001", "No available rooms found for the given time range"),
    MAX_CAPACITY("ERROR-0002", "The requested number of attendees is greater than the available room capacity"),
    ROOM_MAINTENANCE_TIME("ERROR-0003", "Conference room is temporarily unavailable during this time. Please book room after maintenance timings."),
    INVALID_REQUEST("INVALID-REQ-0001", "Invalid value found in the request"),
    INVALID_REQUEST_PARAMETER("INVALID-REQ-0001", "Invalid request parameter"),
    INVALID_REQUEST_HEADER_PARAMETER("INVALID-REQ-0002", "Invalid request header parameter"),
    INTERFACE_ERROR_INVALID_PARAMETER_VALUES("INVALID-REQ-0003", "Invalid parameter values"),
    INTERFACE_ERROR_UNKNOWN_PARAMETERS("INVALID-REQ-0004", "Unknown parameters");
    private final String errorCode;
    private final String errorMessage;

    AppErrorCode(String errorCode, String errorMessage) {
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    public String getErrorCode() {
        return this.errorCode;
    }

    public String getErrorMessage() {
        return this.errorMessage;
    }
}
