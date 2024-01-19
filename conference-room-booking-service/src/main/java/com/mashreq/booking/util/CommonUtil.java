package com.mashreq.booking.util;

import com.mashreq.booking.enums.ResponseStatus;
import com.mashreq.booking.model.Response;
import jakarta.validation.ConstraintValidatorContext;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * The type Common util.
 *
 * @author janv @mashreq.com
 */
public class CommonUtil {

    /**
     * Instantiates a new Common util.
     */
    private CommonUtil() {
        // DEFAULT CONSTRUCTOR
    }

    /**
     * Create success Response object
     *
     * @param data the data in the response
     * @return the Response body with success status
     */
    public static Response buildSuccessResponse(Object data) {
        return Response.builder().status(ResponseStatus.SUCCESS).data(data).build();
    }


    /**
     * Custom message for validation.
     *
     * @param constraintContext the constraint context
     * @param message           the message
     */
    public static void customMessageForValidation(ConstraintValidatorContext constraintContext, String message) {
        // build new violation message and add it
        constraintContext.buildConstraintViolationWithTemplate(message).addConstraintViolation();
    }

    /**
     * Gets parsed current local time.
     * @param today the current date
     * @return the parsed current local time
     */
    public static LocalTime getParsedCurrentLocalTime(LocalTime today) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
        String timeString = today.format(formatter);
        return LocalTime.parse(timeString);
    }
}
