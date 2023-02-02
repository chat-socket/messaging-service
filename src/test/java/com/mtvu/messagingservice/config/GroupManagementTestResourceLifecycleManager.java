package com.mtvu.messagingservice.config;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.HashMap;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class GroupManagementTestResourceLifecycleManager implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;

    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer();
        wireMockServer.start();

        wireMockServer.stubFor(get(urlEqualTo("/api/group/direct:abc"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody(
                                "{\"groupId\":\"direct:abc\"," +
                                        "\"name\":\"Abc\"," +
                                        "\"description\":\"AbcGroup\"," +
                                        "\"avatar\":\"\"," +
                                        "\"participants\":[\"bob\",\"alice\"]," +
                                        "\"createdAt\":\"2023-02-02T15:40:06.284841+07:00\"}"
                        )));


        Map<String, String> conf = new HashMap<>();
        conf.put("quarkus.rest-client.user-management-service.url", wireMockServer.baseUrl());
        return conf;
    }

    @Override
    public void stop() {

    }
}
