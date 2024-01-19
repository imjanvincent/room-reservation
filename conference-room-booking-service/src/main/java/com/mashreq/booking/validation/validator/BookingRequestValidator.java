package com.mashreq.booking.validation.validator;

import com.mashreq.booking.model.BookingRequest;
import com.mashreq.booking.util.CommonUtil;
import com.mashreq.booking.validation.ValidationConstants;
import com.mashreq.booking.validation.annotation.ValidBooking;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The type Booking request validator.
 *
 * @author janv @mashreq.com
 */
@Component
public class BookingRequestValidator implements ConstraintValidator<ValidBooking, BookingRequest> {


    private final Clock clock;

    public BookingRequestValidator(Clock clock) {
        this.clock = clock;
    }

    /**
     * Initializes the validator in preparation for
     * {@link #isValid(BookingRequest, jakarta.validation.ConstraintValidatorContext)} calls.
     * The constraint annotation for a given constraint declaration
     * is passed.
     * <p>
     * This method is guaranteed to be called before any use of this instance for
     * validation.
     * <p>
     * The default implementation is a no-op.
     *
     * @param constraintAnnotation annotation instance for a given constraint declaration
     */
    @Override
    public void initialize(ValidBooking constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(BookingRequest value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        // disable existing violation message
        context.disableDefaultConstraintViolation();
        Set<String> messageSet = new HashSet<>();
        LocalTime startTime = value.getStartTime();
        LocalTime endTime = value.getEndTime();
        this.validateTimeInterval(startTime, endTime, messageSet);
        this.validateMinuteValue(startTime, endTime, messageSet);
        this.validateStartTime(startTime, endTime, messageSet);
        this.validateNumberOfPeople(value.getPersons(), messageSet);
        this.validateEndTime(endTime, startTime, messageSet);

        if (CollectionUtils.isNotEmpty(messageSet)) {
            CommonUtil.customMessageForValidation(context, StringUtils.join(messageSet, ","));
            return false;
        }
        return true;
    }

    /**
     * Validate that the time range interval is 15 minutes
     *
     * @param startTime  the start time
     * @param endTime    the end time
     * @param messageSet the message set
     */
    private void validateTimeInterval(LocalTime startTime, LocalTime endTime, Set<String> messageSet) {
        long minutesDifference = startTime.until(endTime, ChronoUnit.MINUTES);
        this.saveMessage(minutesDifference % 15 == 0, ValidationConstants.INPUT_VALID_MINUTE_INTERVAL, messageSet);
    }

    /**
     * Validate that the minute value is valid
     *
     * @param startTime  the start time
     * @param endTime    the end time
     * @param messageSet the message set
     */
    private void validateMinuteValue(LocalTime startTime, LocalTime endTime, Set<String> messageSet) {
        this.saveMessage(isValidMinute(startTime.getMinute()) && isValidMinute(endTime.getMinute()), ValidationConstants.INPUT_VALID_MINUTE_VALUE, messageSet);
    }

    /**
     * Validate that the minute value is equal to 0, 15, 30 or 45
     *
     * @param minute the minute value
     * @return true if value is valid
     */
    private boolean isValidMinute(int minute) {
        return  minute == 0 || minute == 15 || minute == 30 || minute == 45;
    }

    /**
     * Validate that the start time is valid by checking that it is equal to the current time, if it is after the current time or end time is within the range
     *
     * @param startTime  the start time
     * @param endTime  the end time
     * @param messageSet the message set
     */
    private void validateStartTime(LocalTime startTime, LocalTime endTime, Set<String> messageSet) {
        LocalTime currentTime = CommonUtil.getParsedCurrentLocalTime(LocalTime.now(clock));
        boolean endTimeBeforeCurrentTime = endTime.isAfter(currentTime) && startTime.isBefore(currentTime);
        boolean startTimeAfterCurrentTime = startTime.isAfter(currentTime);
        boolean startTimeEqualToCurrentTime = startTime.equals(currentTime);
        this.saveMessage(endTimeBeforeCurrentTime || startTimeAfterCurrentTime || startTimeEqualToCurrentTime, ValidationConstants.INVALID_START_TIME, messageSet);
    }

    /**
     * Validate that the end time is valid by checking that end time is greater than the start time
     *
     * @param endTime    the end time
     * @param startTime  the start time
     * @param messageSet the message set
     */
    private void validateEndTime(LocalTime endTime, LocalTime startTime, Set<String> messageSet) {
        this.saveMessage(endTime.isAfter(startTime), ValidationConstants.INVALID_END_TIME, messageSet);
    }

    /**
     * Validate that number of people is valid by checking if it is greater than 1
     *
     * @param numberOfPeople the number of people
     * @param messageSet     the message set
     */
    private void validateNumberOfPeople(int numberOfPeople, Set<String> messageSet) {
        this.saveMessage(numberOfPeople > 1, ValidationConstants.INVALID_NUMBER_OF_PERSON, messageSet);
    }

    /**
     * Save the message in the message set if condition is invalid
     *
     * @param isValid    the valid condition
     * @param message    the message
     * @param messageSet the message set
     */
    private void saveMessage(boolean isValid, String message, Set<String> messageSet) {
        if (!isValid) {
            messageSet.add(message);
        }
    }
}
