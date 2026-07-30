package com.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertAll;

import org.junit.jupiter.api.Test;

import com.demo.model.ShoppingCart;

class ShippingServiceTest {

    final ShippingService service = new ShippingService();

    @Test
    void calculatesTier1ShippingForCartTotalBelow25() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(0);

        service.calculateShipping(cart);

        assertEquals(2.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier1ShippingForCartTotal19() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(19);

        service.calculateShipping(cart);

        assertEquals(2.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier1ShippingForCartTotal24_99() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(24.99);

        service.calculateShipping(cart);

        assertEquals(2.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier2ShippingForCartTotal25() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(25);

        service.calculateShipping(cart);

        assertEquals(4.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier2ShippingForCartTotal37() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(37);

        service.calculateShipping(cart);

        assertEquals(4.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier2ShippingForCartTotal49_99() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(49.99);

        service.calculateShipping(cart);

        assertEquals(4.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier3ShippingForCartTotal50() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(50);

        service.calculateShipping(cart);

        assertEquals(6.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier3ShippingForCartTotal62() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(62);

        service.calculateShipping(cart);

        assertEquals(6.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier3ShippingForCartTotal74_99() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(74.99);

        service.calculateShipping(cart);

        assertEquals(6.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier4ShippingForCartTotal75() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(75);

        service.calculateShipping(cart);

        assertEquals(8.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier4ShippingForCartTotal88() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(88);

        service.calculateShipping(cart);

        assertEquals(8.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier4ShippingForCartTotal99_99() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(99.99);

        service.calculateShipping(cart);

        assertEquals(8.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier5ShippingForCartTotal100() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(100);

        service.calculateShipping(cart);

        assertEquals(10.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier5ShippingForCartTotal500() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(500);

        service.calculateShipping(cart);

        assertEquals(10.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void calculatesTier5ShippingForCartTotal9999() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(9999);

        service.calculateShipping(cart);

        assertEquals(10.99, cart.getShippingTotal(), 0.001);
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
        service.calculateShipping(null);
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
        cart.setCartItemTotal(50);
        cart.setShippingTotal(99.99);

        service.calculateShipping(cart);

        assertEquals(6.99, cart.getShippingTotal(), 0.001);
    }

    @Test
    void allShippingTiersCovered() {
        assertAll("shipping tiers",
            () -> {
                ShoppingCart c = new ShoppingCart();
                c.setCartItemTotal(0);
                service.calculateShipping(c);
                assertEquals(2.99, c.getShippingTotal(), 0.001, "tier 1");
            },
            () -> {
                ShoppingCart c = new ShoppingCart();
                c.setCartItemTotal(25);
                service.calculateShipping(c);
                assertEquals(4.99, c.getShippingTotal(), 0.001, "tier 2");
            },
            () -> {
                ShoppingCart c = new ShoppingCart();
                c.setCartItemTotal(50);
                service.calculateShipping(c);
                assertEquals(6.99, c.getShippingTotal(), 0.001, "tier 3");
            },
            () -> {
                ShoppingCart c = new ShoppingCart();
                c.setCartItemTotal(75);
                service.calculateShipping(c);
                assertEquals(8.99, c.getShippingTotal(), 0.001, "tier 4");
            },
            () -> {
                ShoppingCart c = new ShoppingCart();
                c.setCartItemTotal(100);
                service.calculateShipping(c);
                assertEquals(10.99, c.getShippingTotal(), 0.001, "tier 5");
            }
        );
    }
}
