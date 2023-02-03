package com.mtvu.messagingservice.exception;

import javax.ws.rs.core.Response;

public class NotFoundException extends RestClientException {

    public NotFoundException() {
        super(Response.Status.NOT_FOUND);
    }

    public NotFoundException(Throwable cause, Response response) {
        super(cause, validate(response, Response.Status.NOT_FOUND));
    }

    public NotFoundException(Response response) {
        super(validate(response, Response.Status.NOT_FOUND));
    }

    public NotFoundException(String message, Throwable cause, Response response) {
        super(message, cause, validate(response, Response.Status.NOT_FOUND));
    }

    public NotFoundException(String message) {
        super(message, Response.Status.NOT_FOUND);
    }
}
