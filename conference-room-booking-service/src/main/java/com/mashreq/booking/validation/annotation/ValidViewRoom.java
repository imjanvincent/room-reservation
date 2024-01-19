package com.mashreq.booking.validation.annotation;

import com.mashreq.booking.validation.validator.ViewRoomRequestValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author janv@mashreq.com
 */
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = {ViewRoomRequestValidator.class})
public @interface ValidViewRoom {

    Class<?>[] groups() default {};

    String message() default "Invalid view room request";

    Class<? extends Payload>[] payload() default {};
}
