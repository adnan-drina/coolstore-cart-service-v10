package com.demo.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;

import com.demo.model.ShoppingCart;
import com.demo.service.ShoppingCartService;

@Path("/cart")
@RequestScoped
public class CartEndpoint {

    private final ShoppingCartService shoppingCartService;

    public CartEndpoint(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GET
    @Path("/{cartId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart getCart(@PathParam("cartId") String cartId) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException("Cart ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.getShoppingCart(cartId);
        } catch (Exception e) {
            throw new WebApplicationException("Failed to retrieve cart: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/{cartId}/{itemId}/{quantity}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart add(@PathParam("cartId") String cartId,
                            @PathParam("itemId") String itemId,
                            @PathParam("quantity") int quantity) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException("Cart ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new WebApplicationException("Item ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        if (quantity <= 0) {
            throw new WebApplicationException("Quantity must be positive", Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.addItem(cartId, itemId, quantity);
        } catch (Exception e) {
            throw new WebApplicationException("Failed to add item to cart: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/{cartId}/{tmpId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart set(@PathParam("cartId") String cartId,
                            @PathParam("tmpId") String tmpId) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException("Cart ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        if (tmpId == null || tmpId.trim().isEmpty()) {
            throw new WebApplicationException("Temp ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.set(cartId, tmpId);
        } catch (Exception e) {
            throw new WebApplicationException("Failed to set cart: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("/{cartId}/{itemId}/{quantity}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart delete(@PathParam("cartId") String cartId,
                               @PathParam("itemId") String itemId,
                               @PathParam("quantity") int quantity) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException("Cart ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new WebApplicationException("Item ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        if (quantity <= 0) {
            throw new WebApplicationException("Quantity must be positive", Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.deleteItem(cartId, itemId, quantity);
        } catch (Exception e) {
            throw new WebApplicationException("Failed to delete item from cart: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/checkout/{cartId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart checkout(@PathParam("cartId") String cartId) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException("Cart ID cannot be null or empty", Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.checkout(cartId);
        } catch (Exception e) {
            throw new WebApplicationException("Failed to checkout cart: " + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
