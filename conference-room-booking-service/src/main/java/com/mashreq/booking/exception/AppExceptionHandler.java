package com.mashreq.booking.exception;

import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.mashreq.booking.enums.AppErrorCode;
import com.mashreq.booking.enums.ResponseStatus;
import com.mashreq.booking.model.Response;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.SQLException;
import java.util.stream.Collectors;

/**
 * The type App exception handler.
 *
 * @author janv @mashreq.com
 */
@ControllerAdvice
@Slf4j
public class AppExceptionHandler extends ResponseEntityExceptionHandler {

    /**
     * Handle custom exception response entity.
     *
     * @param exception the exception
     * @param request   the request
     * @return the response entity
     */
    @ExceptionHandler
    public ResponseEntity<Object> handleCustomException(Exception exception, WebRequest request) {
        if (exception instanceof AppException) {
            String errorDetails = ((AppException) exception).getErrorDetails();
            String messageId = ((AppException) exception).getMessageId();
            if (StringUtils.isNotBlank(messageId)) {
                errorDetails = errorDetails + ". Message ID " + messageId;
            }

            return this.buildErrorResponse(exception, ((AppException) exception).getErrorCode(), errorDetails, request, "AppException");
        } else {
            return exception instanceof UnrecognizedPropertyException
                    ? this.buildErrorResponse(exception, AppErrorCode.INTERFACE_ERROR_UNKNOWN_PARAMETERS.name(), request, "UnrecognizedPropertyException")
                    : this.buildErrorResponse(exception, AppErrorCode.SYSTEM_ERROR.name(), request, "System Error");
        }
    }

    /**
     * Handle bad request
     *
     * @param ex      the MethodArgumentNotValidException
     * @param headers the HttpHeaders
     * @param status  the HttpStatus
     * @param request the WebRequest
     * @return the bad request response
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
                                                                  HttpStatusCode status, WebRequest request) {
        String errorMessage = ex.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ":" + e.getDefaultMessage()).collect(Collectors.joining(";"));
        if (StringUtils.isBlank(errorMessage) && CollectionUtils.isNotEmpty(ex.getBindingResult().getGlobalErrors())) {
            errorMessage = ex.getBindingResult().getGlobalErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.joining(";"));
        }
        return this.buildErrorResponse(ex, AppErrorCode.INVALID_REQUEST.name(), errorMessage, request, "MethodArgumentNotValidException");
    }

    /**
     * Handle constraint violation
     *
     * @param ex      the ConstraintViolationException
     * @param request the WebRequest
     * @return the bad request response
     */
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex, WebRequest request) {
        return this.buildErrorResponse(ex, AppErrorCode.INVALID_REQUEST.name(), null, request, "ConstraintViolationException");
    }

    @Override
    public ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return this.buildErrorResponse(ex, AppErrorCode.INVALID_REQUEST_PARAMETER.name(), ex.getParameterName() + " Parameter Missing", request, "MissingServletRequestParameterException");

    }

    @Override
    public ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        MissingRequestHeaderException e = (MissingRequestHeaderException) ex;
        return this.buildErrorResponse(e, AppErrorCode.INVALID_REQUEST_HEADER_PARAMETER.name(), e.getHeaderName() + " Header Missing", request, "ServletRequestBindingException");
    }

    @Override
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return this.buildErrorResponse(ex, AppErrorCode.INTERFACE_ERROR_INVALID_PARAMETER_VALUES.name(), ex.getMessage(), request, "HttpMessageNotReadableException");

    }

    private ResponseEntity<Object> buildErrorResponse(Throwable exception, String errorId, WebRequest request, String type) {
        return this.buildErrorResponse(exception, errorId, null, request, type);
    }

    private ResponseEntity<Object> buildErrorResponse(Throwable exception, String errorId, String errorDetails, WebRequest request, String type) {
        String path = ((ServletWebRequest) request).getRequest().getRequestURI();
        log.error("{} caught. Building Error Response for path {}", type, path);
        Response errorResponse = Response.builder().status(ResponseStatus.ERROR).errorCode(errorId).uriPath(path).errorDetails(errorDetails).message(this.getErrorMessage(exception)).build();
        this.writeToLogfile(errorResponse, exception);
        return new ResponseEntity(errorResponse, HttpStatus.OK);
    }


    /**
     * Write to logfile.
     *
     * @param errorResponse the error response
     * @param ex            the ex
     */
    public void writeToLogfile(Response errorResponse, Throwable ex) {
        log.error("Error Response {}", errorResponse.toString());
        if (ex != null) {
          /*  if (ex instanceof AppException) {
                ex = ex.getCause();
            }*/
            StringWriter stack = new StringWriter();
            ex.printStackTrace(new PrintWriter(stack));
            log.error("Caught exception {}", ExceptionUtils.getMessage(ex));
        }
    }

    private String getErrorMessage(Throwable exception) {
        if (exception instanceof MethodArgumentNotValidException) {
            return "Validation Failed";
        } else if (exception instanceof HttpMessageNotReadableException) {
            return "Message Not Readable";
        } else if (exception instanceof UnrecognizedPropertyException) {
            return "Property unrecognised";
        } else if (exception instanceof ConstraintViolationException) {
            return ((ConstraintViolationException) exception).getConstraintViolations().stream().map(ConstraintViolation::getMessage).collect(Collectors.joining(";"));
        } else if (!(exception instanceof DataIntegrityViolationException) && !(exception instanceof SQLException)) {
            String exceptionString = exception.getCause() != null ? exception.getCause().getMessage() : exception.getMessage();
            return exceptionString.replaceFirst(AppException.class.getName() + ":\\s+", "");
        } else {
            return "Database Exception";
        }
    }


}

