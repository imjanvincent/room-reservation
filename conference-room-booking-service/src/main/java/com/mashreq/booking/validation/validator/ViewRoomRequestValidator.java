package com.mashreq.booking.validation.validator;

import com.mashreq.booking.model.ViewRoomRequest;
import com.mashreq.booking.util.CommonUtil;
import com.mashreq.booking.validation.ValidationConstants;
import com.mashreq.booking.validation.annotation.ValidViewRoom;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.Clock;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * The type View room request validator.
 *
 * @author janv @mashreq.com
 */
@Component
public class ViewRoomRequestValidator implements ConstraintValidator<ValidViewRoom, ViewRoomRequest> {

    private final Clock clock;

    public ViewRoomRequestValidator(Clock clock) {
        this.clock = clock;
    }

    /**
     * Initializes the validator in preparation for
     * {@link #isValid(com.mashreq.booking.model.ViewRoomRequest, jakarta.validation.ConstraintValidatorContext)} calls.
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
    public void initialize(ValidViewRoom constraintAnnotation) {
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
    public boolean isValid(ViewRoomRequest value, ConstraintValidatorContext context) {
        if (Objects.isNull(value)) {
            return true;
        }
        // disable existing violation message
        context.disableDefaultConstraintViolation();
        Set<String> messageSet = new HashSet<>();
        LocalTime startTime = value.getStartTime();
        LocalTime endTime = value.getEndTime();
        this.validateEndTime(endTime, startTime, messageSet);
        this.validateMinuteValue(startTime, endTime, messageSet);
        if (CollectionUtils.isNotEmpty(messageSet)) {
            CommonUtil.customMessageForValidation(context, StringUtils.join(messageSet, ","));
            return false;
        }
        return true;
    }

    /**
     * Validate that end time is valid by checking if it is greater than the start time or current time
     *
     * @param endTime    the end time
     * @param startTime  the start time
     * @param messageSet the message set
     */
    private void validateEndTime(LocalTime endTime, LocalTime startTime, Set<String> messageSet) {
        if (endTime.isBefore(startTime)) {
            messageSet.add(ValidationConstants.END_TIME_LESS_THAN_START_TIME);
        }

        if (endTime.isBefore(LocalTime.now(clock))) {
            messageSet.add(ValidationConstants.END_TIME_LESS_THAN_CURRENT_TIME);
        }
    }

    /**
     * Validate that the minute value is valid
     *
     * @param startTime  the start time
     * @param endTime    the end time
     * @param messageSet the message set
     */
    private void validateMinuteValue(LocalTime startTime, LocalTime endTime, Set<String> messageSet) {
        if (isValidMinute(startTime.getMinute()) && isValidMinute(endTime.getMinute())) {
           return;
        }
        messageSet.add(ValidationConstants.INPUT_VALID_MINUTE_VALUE);
    }

    /**
     * Validate that the minute value is equal to 0, 15, 30 or 45
     *
     * @param minute the minute value
     * @return true if value is valid
     */
    private boolean isValidMinute(int minute) {
        return minute == 0 || minute == 15 || minute == 30 || minute == 45;
    }

}
