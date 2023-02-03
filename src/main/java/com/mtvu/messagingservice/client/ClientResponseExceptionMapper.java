package com.mtvu.messagingservice.client;

import com.mtvu.messagingservice.exception.NotFoundException;
import org.eclipse.microprofile.rest.client.ext.ResponseExceptionMapper;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class ClientResponseExceptionMapper implements ResponseExceptionMapper<RuntimeException> {

    @Override
    public RuntimeException toThrowable(Response response) {
        return new NotFoundException(response);
    }

    @Override
    public boolean handles(int status, MultivaluedMap<String, Object> headers) {
        return status == Response.Status.NOT_FOUND.getStatusCode();
    }
}