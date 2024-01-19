package com.mashreq.booking.controller;

import com.mashreq.booking.model.BookingRequest;
import com.mashreq.booking.model.ViewRoomRequest;
import com.mashreq.booking.service.BookingService;
import com.mashreq.booking.util.CommonUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Book;


/**
 * @author janv@mashreq.com
 */
@RestController
@Validated
@Slf4j
@RequestMapping("/v1/conference/room")
public class ConferenceRoomController {

    private final BookingService bookingService;

    public ConferenceRoomController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Book conference room by time range and capacity")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully booked conference room",
                    content = {@Content(mediaType = "application/json",
                            schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request received",
                    content = @Content)})
    @PostMapping("/book")
    public ResponseEntity<Object> bookRoom(@Valid @RequestBody BookingRequest bookingRequest) {
        log.info("Book conference room request {}", bookingRequest);
        return ResponseEntity.ok(CommonUtil.buildSuccessResponse(bookingService.bookConferenceRoom(bookingRequest)));
    }

    @Operation(summary = "View available conference rooms by time range")
    @ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Successfully fetched available conference rooms",
            content = {@Content(mediaType = "application/json",
                    schema = @Schema(implementation = Book.class))}),
            @ApiResponse(responseCode = "400", description = "Invalid request received",
                    content = @Content)})
    @PostMapping("/view")
    public ResponseEntity<Object> viewAvailableRooms(@Valid @RequestBody ViewRoomRequest viewRoomRequest) {
        log.info("View available rooms request {}", viewRoomRequest);
        return ResponseEntity.ok(CommonUtil.buildSuccessResponse(bookingService.findAvailableRooms(viewRoomRequest)));
    }
}
