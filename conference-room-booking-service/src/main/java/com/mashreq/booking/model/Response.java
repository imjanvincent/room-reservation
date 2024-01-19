package com.mashreq.booking.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.mashreq.booking.enums.ResponseStatus;
import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

/**
 * @author janv@mashreq.com
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Response implements Serializable {

    private ResponseStatus status;
    private String message;
    private transient Object data;
    private String errorCode;
    private String errorDetails;
    private String uriPath;

}
