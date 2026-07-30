package com.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import com.demo.model.ShoppingCart;

class ShippingServiceTest {

    final ShippingService service = new ShippingService();

    @ParameterizedTest
    @CsvSource({
        "0, 2.99",
        "19, 2.99",
        "24.99, 2.99",
        "25, 4.99",
        "37, 4.99",
        "49.99, 4.99",
        "50, 6.99",
        "62, 6.99",
        "74.99, 6.99",
        "75, 8.99",
        "88, 8.99",
        "99.99, 8.99",
        "100, 10.99",
        "500, 10.99",
        "9999, 10.99"
    })
    void calculatesShippingTierForCartTotal(double cartTotal, double expectedShipping) {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(cartTotal);

        service.calculateShipping(cart);

        assertEquals(expectedShipping, cart.getShippingTotal(), 0.001);
    }

    @Test
    void doesNotSetShippingForCartTotalAt10000() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(10000);
        cart.setShippingTotal(0);

        service.calculateShipping(cart);

        assertEquals(0, cart.getShippingTotal(), 0.001);
    }

    @Test
    void doesNotSetShippingForNullCart() {
        assertDoesNotThrow(() -> service.calculateShipping(null));
    }

    @Test
    void doesNotSetShippingForNegativeCartTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(-1);
        cart.setShippingTotal(0);

        service.calculateShipping(cart);

        assertEquals(0, cart.getShippingTotal(), 0.001);
    }

    @Test
    void overwritesExistingShippingTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(30);
        cart.setShippingTotal(99.0);

        service.calculateShipping(cart);

        assertEquals(4.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void allShippingTiersCovered() {
        double[][] cases = {
            {0, 2.99}, {25, 4.99}, {50, 6.99}, {75, 8.99}, {100, 10.99}
        };
        for (double[] c : cases) {
            ShoppingCart cart = new ShoppingCart();
            cart.setCartItemTotal(c[0]);
            service.calculateShipping(cart);
            assertEquals(c[1], cart.getShippingTotal(), 0.001);
        }
    }
}
