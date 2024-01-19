package com.mashreq.booking.exception;


import com.mashreq.booking.enums.AppErrorCode;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The type App exception.
 *
 * @author janv @mashreq.com
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AppException extends RuntimeException {

    private String errorDetails;
    private String errorCode;
    private String messageId;

    /**
     * Instantiates a new App exception.
     *
     * @param ex the ex
     */
    public AppException(Exception ex) {
        super(ex);
    }

    /**
     * Instantiates a new App exception.
     *
     * @param ex        the ex
     * @param errorCode the error code
     */
    public AppException(Exception ex, String errorCode) {
        super(ex);
        this.errorCode = errorCode;
    }

    /**
     * Instantiates a new App exception.
     *
     * @param ex           the ex
     * @param appErrorCode the app error code
     * @param customMessage      the message
     */
    public AppException(Exception ex, AppErrorCode appErrorCode, String customMessage) {
        super(ex);
        this.errorCode = appErrorCode.getErrorCode();
        this.errorDetails = customMessage;
    }

    public AppException(AppErrorCode appErrorCode, String message) {
        super(message);
        this.errorCode = appErrorCode.getErrorCode();
        this.errorDetails = message;
    }

    /**
     * Instantiates a new App exception.
     *
     * @param ex           the ex
     * @param appErrorCode the app error code
     */
    public AppException(Exception ex, AppErrorCode appErrorCode) {
        super(ex);
        this.errorCode = appErrorCode.getErrorCode();
        this.errorDetails = appErrorCode.getErrorMessage();
    }

    public AppException(AppErrorCode appErrorCode) {
        super(appErrorCode.getErrorMessage());
        this.errorCode = appErrorCode.getErrorCode();
        this.errorDetails = appErrorCode.getErrorMessage();
    }



}
