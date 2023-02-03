package com.mtvu.messagingservice.exception;

import javax.ws.rs.core.Response;

/**
 * Currently, it seems to be an issue with Rest Client exception customization
 * See: https://github.com/quarkusio/quarkus/issues/24185
 * Therefore, we create our own exception classes
 */
public class RestClientException extends RuntimeException {

    private Response response;

    public RestClientException(Response.Status status) {
        this(null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public RestClientException(String message, Response.Status status) {
        this(message, null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public RestClientException(String message, int status) {
        this(message, null, validate(Response.status(status).build(), Response.Status.Family.CLIENT_ERROR));
    }

    public RestClientException(final Throwable cause, final Response response) {
        this(computeExceptionMessage(response), cause, response);
    }

    public RestClientException(Response response) {
        this(null, validate(response, Response.Status.Family.CLIENT_ERROR));
    }

    public RestClientException(final String message, final Throwable cause, final Response response) {
        super(message, cause);
        if (response == null) {
            this.response = Response.serverError().build();
        } else {
            this.response = response;
        }
    }

    /**
     * Validate that a {@link javax.ws.rs.core.Response} object has an expected HTTP response
     * status code set.
     *
     * @param response       response object.
     * @param expectedStatus expected response status code.
     * @return validated response object.
     * @throws IllegalArgumentException if the response validation failed.
     * @since 2.0
     */
    static Response validate(final Response response, Response.Status expectedStatus) {
        if (expectedStatus.getStatusCode() != response.getStatus()) {
            throw new IllegalArgumentException(String.format("Invalid response status code. Expected [%d], was [%d].",
                    expectedStatus.getStatusCode(), response.getStatus()));
        }
        return response;
    }

    static Response validate(final Response response, Response.Status.Family expectedStatusFamily) {
        if (response.getStatusInfo().getFamily() != expectedStatusFamily) {
            throw new IllegalArgumentException(String.format(
                    "Status code of the supplied response [%d] is not from the required status code family \"%s\".",
                    response.getStatus(), expectedStatusFamily));
        }
        return response;
    }

    private static String computeExceptionMessage(Response response) {
        final Response.StatusType statusInfo;
        if (response != null) {
            statusInfo = response.getStatusInfo();
        } else {
            statusInfo = Response.Status.INTERNAL_SERVER_ERROR;
        }
        return "HTTP " + statusInfo.getStatusCode() + ' ' + statusInfo.getReasonPhrase();
    }

    public Response getResponse() {
        return response;
    }
}
