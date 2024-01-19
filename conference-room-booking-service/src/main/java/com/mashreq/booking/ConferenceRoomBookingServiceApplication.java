package com.mashreq.booking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;

import java.time.Clock;

/**
 * @author janv@mashreq.com
 */
@SpringBootApplication
public class ConferenceRoomBookingServiceApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(ConferenceRoomBookingServiceApplication.class, args);
    }

    @Override
    protected SpringApplicationBuilder configure(final SpringApplicationBuilder builder) {
        return builder.sources(this.getClass());
    }

    @Bean
    public Clock clock() {
        return Clock.systemDefaultZone();
    }


}