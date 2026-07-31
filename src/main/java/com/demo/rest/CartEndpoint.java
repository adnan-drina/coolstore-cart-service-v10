package com.demo.rest;

import jakarta.enterprise.context.RequestScoped;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import com.demo.model.ShoppingCart;
import com.demo.service.ShoppingCartService;

@Path("/cart")
@RequestScoped
public class CartEndpoint {

    private static final String CART_ID_CANNOT_BE_NULL = "Cart ID cannot be null or empty";
    private static final String ITEM_ID_CANNOT_BE_NULL = "Item ID cannot be null or empty";
    private static final String TEMP_ID_CANNOT_BE_NULL = "Temp ID cannot be null or empty";
    private static final String FAILED_TO_ADD_ITEM = "Failed to add item to cart: ";
    private static final String FAILED_TO_DELETE_ITEM = "Failed to delete item from cart: ";
    private static final String FAILED_TO_RETRIEVE = "Failed to retrieve cart: ";
    private static final String FAILED_TO_SET_CART = "Failed to set cart: ";
    private static final String FAILED_TO_CHECKOUT = "Failed to checkout cart: ";
    private static final String QUANTITY_MUST_BE_POSITIVE = "Quantity must be positive";
    
    private final ShoppingCartService shoppingCartService;

    public CartEndpoint(ShoppingCartService shoppingCartService) {
        this.shoppingCartService = shoppingCartService;
    }

    @GET
    @Path("/{cartId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart getCart(@PathParam("cartId") String cartId) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException(CART_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.getShoppingCart(cartId);
        } catch (Exception e) {
            throw new WebApplicationException(FAILED_TO_RETRIEVE + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/{cartId}/{itemId}/{quantity}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart add(@PathParam("cartId") String cartId,
                            @PathParam("itemId") String itemId,
                            @PathParam("quantity") int quantity) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException(CART_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new WebApplicationException(ITEM_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        if (quantity <= 0) {
            throw new WebApplicationException(QUANTITY_MUST_BE_POSITIVE, Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.addItem(cartId, itemId, quantity);
        } catch (Exception e) {
            throw new WebApplicationException(FAILED_TO_ADD_ITEM + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/{cartId}/{tmpId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart set(@PathParam("cartId") String cartId,
                            @PathParam("tmpId") String tmpId) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException(CART_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        if (tmpId == null || tmpId.trim().isEmpty()) {
            throw new WebApplicationException(TEMP_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.set(cartId, tmpId);
        } catch (Exception e) {
            throw new WebApplicationException(FAILED_TO_SET_CART + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @DELETE
    @Path("/{cartId}/{itemId}/{quantity}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart delete(@PathParam("cartId") String cartId,
                               @PathParam("itemId") String itemId,
                               @PathParam("quantity") int quantity) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException(CART_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new WebApplicationException(ITEM_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        if (quantity <= 0) {
            throw new WebApplicationException(QUANTITY_MUST_BE_POSITIVE, Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.deleteItem(cartId, itemId, quantity);
        } catch (Exception e) {
            throw new WebApplicationException(FAILED_TO_DELETE_ITEM + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }

    @POST
    @Path("/checkout/{cartId}")
    @Produces(MediaType.APPLICATION_JSON)
    public ShoppingCart checkout(@PathParam("cartId") String cartId) {
        if (cartId == null || cartId.trim().isEmpty()) {
            throw new WebApplicationException(CART_ID_CANNOT_BE_NULL, Response.Status.BAD_REQUEST);
        }
        try {
            return shoppingCartService.checkout(cartId);
        } catch (Exception e) {
            throw new WebApplicationException(FAILED_TO_CHECKOUT + e.getMessage(), Response.Status.INTERNAL_SERVER_ERROR);
        }
    }
}
