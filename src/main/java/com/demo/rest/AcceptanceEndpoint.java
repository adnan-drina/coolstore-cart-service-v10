package com.demo.rest;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.rest.client.inject.RestClient;

import com.demo.model.Product;
import com.demo.service.CatalogService;

@Path("/api/cart")
@ApplicationScoped
public class AcceptanceEndpoint {

    private final CatalogService catalogService;

    public AcceptanceEndpoint(@RestClient CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GET
    @Path("acceptance-check")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Product> acceptanceCheck() {
        return catalogService.getProducts();
    }
}
