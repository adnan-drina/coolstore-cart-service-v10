package com.demo.rest;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.demo.service.CatalogService;
import com.demo.model.Product;

import java.util.List;

@Path("/api/cart")
@ApplicationScoped
public class AcceptanceEndpoint {

    @Inject
    @RestClient
    CatalogService catalogService;

    @GET
    @Path("acceptance-check")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> acceptanceCheck() {
        try {
            // Test catalog service integration by fetching products
            return catalogService.getProducts();
        } catch (Exception e) {
            // Return empty list on failure to maintain API contract
            return List.of();
        }
    }
}
