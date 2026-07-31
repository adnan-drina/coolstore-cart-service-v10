package com.demo.rest;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

@QuarkusTest
class AcceptanceEndpointTest {

    @Test
    void acceptanceCheckReturns200() {
        given()
            .when().get("/api/cart/acceptance-check")
            .then()
            .statusCode(200)
            .contentType("application/json")
            .body("size()", notNullValue());
    }
}
