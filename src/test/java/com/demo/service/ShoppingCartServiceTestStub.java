package com.demo.service;

import jakarta.annotation.Priority;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.inject.Alternative;

import com.demo.model.Product;
import com.demo.model.ShoppingCart;

/**
 * O-LATERCDI: satisfies CartEndpoint injection for @QuarkusTest while
 * ShoppingCartServiceImpl remains a later-story (S05) src/main class.
 */
@Alternative
@Priority(1)
@ApplicationScoped
public class ShoppingCartServiceTestStub implements ShoppingCartService {

    @Override
    public ShoppingCart getShoppingCart(String cartId) {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartId(cartId);
        return cart;
    }

    @Override
    public Product getProduct(String itemId) {
        return null;
    }

    @Override
    public ShoppingCart deleteItem(String cartId, String itemId, int quantity) {
        return getShoppingCart(cartId);
    }

    @Override
    public ShoppingCart checkout(String cartId) {
        return getShoppingCart(cartId);
    }

    @Override
    public ShoppingCart addItem(String cartId, String itemId, int quantity) {
        return getShoppingCart(cartId);
    }

    @Override
    public ShoppingCart set(String cartId, String tmpId) {
        return getShoppingCart(cartId);
    }

    @Override
    public void priceShoppingCart(ShoppingCart sc) {
        // no-op stub
    }
}
