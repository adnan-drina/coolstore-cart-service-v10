package com.demo.rest;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class CatalogWireMockResource implements QuarkusTestResourceLifecycleManager {

    static WireMockServer server;

    @Override
    public Map<String, String> start() {
        server = new WireMockServer(0);
        server.start();
        server.stubFor(get(urlEqualTo("/"))
            .willReturn(okJson("[{"
                + "\"itemId\":\"1111\",\"name\":\"Car\",\"desc\":\"Super car\",\"price\":1000"
                + "},{"
                + "\"itemId\":\"2222\",\"name\":\"Bike\",\"desc\":\"Super bike\",\"price\":200"
                + "}]")));
        return Map.of("quarkus.rest-client.catalog-service.url", server.baseUrl());
    }

    @Override
    public void stop() {
        if (server != null) server.stop();
    }
}
