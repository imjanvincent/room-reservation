package com.mashreq.booking.validation.validator;

import com.mashreq.booking.model.BookingRequest;
import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.AfterEach;
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
 * The type Booking request validator test.
 *
 * @author janv @mashreq.com
 */
@ExtendWith(MockitoExtension.class)
class BookingRequestValidatorTest {

    @InjectMocks
    private BookingRequestValidator bookingRequestValidator;

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
        bookingRequestValidator = new BookingRequestValidator(clock);
        bookingRequestValidator.initialize(null);

        Clock fixedClock = Clock.fixed(LocalDateTime.of(2024, 3, 3, 6, 30, 0).atZone(ZoneId.systemDefault()).toInstant(),
                ZoneId.systemDefault());

        Mockito.lenient().doReturn(fixedClock.instant()).when(clock).instant();
        Mockito.lenient().doReturn(fixedClock.getZone()).when(clock).getZone();

        Mockito.lenient().when(constraintValidatorContext.buildConstraintViolationWithTemplate(Mockito.isA(String.class))).thenReturn(constraintViolationBuilder);
    }

    /**
     * Tear down.
     */
    @AfterEach
    void tearDown() {
        bookingRequestValidator = null;
        clock = null;
        constraintValidatorContext = null;
        constraintViolationBuilder = null;
    }

    /**
     * Test is valid.
     */
    @Test
    void testIsValid() {
        BookingRequest bookingRequest = new BookingRequest();
        // Test invalid time interval
        bookingRequest.setPersons(3);
        bookingRequest.setStartTime(LocalTime.of(8, 0));
        bookingRequest.setEndTime(LocalTime.of(8,20));
        boolean actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test invalid minute value
        bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(8, 5));
        bookingRequest.setEndTime(LocalTime.of(8,20));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test invalid start time with end time
        bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(5, 0));
        bookingRequest.setEndTime(LocalTime.of(6,30));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);

        bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(5, 0));
        bookingRequest.setEndTime(LocalTime.of(7,30));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test invalid start time
        bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(6, 15));
        bookingRequest.setEndTime(LocalTime.of(6,30));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);

        bookingRequest = new BookingRequest();
        bookingRequest.setStartTime(LocalTime.of(5, 15));
        bookingRequest.setEndTime(LocalTime.of(5,30));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);

        // Test invalid people count
        bookingRequest = new BookingRequest();
        bookingRequest.setPersons(1);
        bookingRequest.setStartTime(LocalTime.of(7, 0));
        bookingRequest.setEndTime(LocalTime.of(7,30));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test invalid end time
        bookingRequest = new BookingRequest();
        bookingRequest.setPersons(5);
        bookingRequest.setStartTime(LocalTime.of(6, 30));
        bookingRequest.setEndTime(LocalTime.of(5,30));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertFalse(actual);
        // Test valid request
        bookingRequest = new BookingRequest();
        bookingRequest.setPersons(5);
        bookingRequest.setStartTime(LocalTime.of(6, 30));
        bookingRequest.setEndTime(LocalTime.of(7,30));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertTrue(actual);

        bookingRequest = new BookingRequest();
        bookingRequest.setPersons(5);
        bookingRequest.setStartTime(LocalTime.of(6, 45));
        bookingRequest.setEndTime(LocalTime.of(8,0));
        actual = bookingRequestValidator.isValid(bookingRequest, constraintValidatorContext);
        Assertions.assertTrue(actual);

        actual = bookingRequestValidator.isValid(null, constraintValidatorContext);
        Assertions.assertTrue(actual);
    }
}