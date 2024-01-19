package com.mashreq.booking.validation.validator;

import com.mashreq.booking.model.ViewRoomRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Clock;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;


/**
 * The type View room request validator test.
 *
 * @author janv @mashreq.com
 */
@ExtendWith(MockitoExtension.class)
class ViewRoomRequestValidatorTest {

    @InjectMocks
    private ViewRoomRequestValidator viewRoomRequestValidator;

    @Mock
    private Clock clock;

    @Mock
    private ConstraintValidatorContext constraintValidatorContext;

    @Mock
    private ConstraintValidatorContext.ConstraintViolationBuilder constraintViolationBuilder;

    /**
     * Sets up.
     */
    @BeforeEach
    void setUp() {
        viewRoomRequestValidator = new ViewRoomRequestValidator(clock);
        viewRoomRequestValidator.initialize(null);

        Clock fixedClock = Clock.fixed(LocalDateTime.of(2024, 3, 3, 8, 0, 0).atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());

        Mockito.lenient().doReturn(fixedClock.instant()).when(clock).instant();
        Mockito.lenient().doReturn(fixedClock.getZone()).when(clock).getZone();

        Mockito.lenient().when(constraintValidatorContext.buildConstraintViolationWithTemplate(Mockito.isA(String.class))).thenReturn(constraintViolationBuilder);
    }

    /**
     * Test is valid.
     */
    @Test
    void testIsValid() {
        Assertions.assertTrue(viewRoomRequestValidator.isValid(null, constraintValidatorContext));
        ViewRoomRequest viewRoomRequest = new ViewRoomRequest();
        // Test invalid end time
        viewRoomRequest.setStartTime(LocalTime.of(8,0));
        viewRoomRequest.setEndTime(LocalTime.of(7,30));
        boolean actual = viewRoomRequestValidator.isValid(viewRoomRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test invalid end time
        viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(6,45));
        viewRoomRequest.setEndTime(LocalTime.of(7,15));
        actual = viewRoomRequestValidator.isValid(viewRoomRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test invalid minute value
        viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(9,3));
        viewRoomRequest.setEndTime(LocalTime.of(10,15));
        actual = viewRoomRequestValidator.isValid(viewRoomRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(9,45));
        viewRoomRequest.setEndTime(LocalTime.of(10,1));
        actual = viewRoomRequestValidator.isValid(viewRoomRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test valid request
        viewRoomRequest = new ViewRoomRequest();
        viewRoomRequest.setStartTime(LocalTime.of(9,15));
        viewRoomRequest.setEndTime(LocalTime.of(10,15));
        actual = viewRoomRequestValidator.isValid(viewRoomRequest, constraintValidatorContext);
        Assertions.assertTrue(actual);
    }
}