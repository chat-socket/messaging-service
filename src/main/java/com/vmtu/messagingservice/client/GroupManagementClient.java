package com.vmtu.messagingservice.client;

import com.vmtu.messagingservice.domain.Group;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * @author mvu
 * @project chat-socket
 **/
@FeignClient("${microservices.group-management-endpoint}")
public interface GroupManagementClient {

    @RequestMapping(method = RequestMethod.GET, value = "/group/{groupId}", consumes = "application/json")
    Group getGroup(@PathVariable("groupId") String groupId);
}
