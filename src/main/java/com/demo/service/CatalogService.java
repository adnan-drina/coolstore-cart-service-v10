package com.demo.service;

import java.util.List;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import com.demo.model.Product;

/**
 * CatalogService client for retrieving product information from catalog endpoint.
 * Migrated from OpenFeign to Quarkus REST Client with environment-driven configuration.
 */
@Path("/")
@RegisterRestClient(configKey = "catalog-service")
public interface CatalogService {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    List<Product> getProducts();
}
