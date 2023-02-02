package com.mtvu.messagingservice.client;

import com.mtvu.messagingservice.domain.group.ChatGroup;
import org.eclipse.microprofile.rest.client.annotation.RegisterProvider;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

@Path("/api/group")
@RegisterProvider(ClientResponseExceptionMapper.class)
@RegisterRestClient(configKey = "user-management-service")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface GroupManagementClient {

    @GET
    @Path("/{groupId}")
    ChatGroup.Response.Public getGroupById(@PathParam("groupId") String groupId);
}
