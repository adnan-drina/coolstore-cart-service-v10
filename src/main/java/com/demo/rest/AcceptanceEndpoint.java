package com.demo.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/api/cart")
@ApplicationScoped
public class AcceptanceEndpoint {

    @GET
    @Path("acceptance-check")
    @Produces(MediaType.APPLICATION_JSON)
    public AcceptanceStatus acceptanceCheck() {
        return new AcceptanceStatus("accepted", "cart service is healthy");
    }

    public record AcceptanceStatus(String status, String message) {
    }
}
