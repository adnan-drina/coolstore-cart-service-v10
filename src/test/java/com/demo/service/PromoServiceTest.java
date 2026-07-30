package com.demo.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.demo.model.Product;
import com.demo.model.Promotion;
import com.demo.model.ShoppingCart;
import com.demo.model.ShoppingCartItem;

import java.util.HashSet;
import java.util.Set;

class PromoServiceTest {

    PromoService service;

    @BeforeEach
    void setUp() {
        service = new PromoService();
    }

    @Test
    void seedsPromotionForItemId329299() {
        Set<Promotion> promotions = service.getPromotions();

        assertEquals(1, promotions.size());
        assertTrue(promotions.stream().anyMatch(p -> p.getItemId().equals("329299") && p.getPercentOff() == 0.25));
    }

    @Test
    void applies25PercentDiscountToPromotedProduct() {
        Product product = new Product("329299", "Promoted Item", "Desc", 100.0);
        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setPrice(100.0);

        ShoppingCart cart = new ShoppingCart();
        cart.getShoppingCartItemList().add(item);

        service.applyCartItemPromotions(cart);

        assertAll("item promotion",
            () -> assertEquals(-25.0, item.getPromoSavings(), 0.001, "promo savings"),
            () -> assertEquals(75.0, item.getPrice(), 0.001, "discounted price")
        );
    }

    @Test
    void doesNotPromoteNonPromotedProduct() {
        Product product = new Product("999999", "Regular Item", "Desc", 50.0);
        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setPrice(50.0);

        ShoppingCart cart = new ShoppingCart();
        cart.getShoppingCartItemList().add(item);

        service.applyCartItemPromotions(cart);

        assertAll("no promotion",
            () -> assertEquals(0.0, item.getPromoSavings(), 0.001, "promo savings unchanged"),
            () -> assertEquals(50.0, item.getPrice(), 0.001, "price unchanged")
        );
    }

    @Test
    void appliesPromotionToMixedCart() {
        Product promoted = new Product("329299", "Promoted", "Desc", 80.0);
        Product regular = new Product("111111", "Regular", "Desc", 40.0);

        ShoppingCartItem promotedItem = new ShoppingCartItem();
        promotedItem.setProduct(promoted);
        promotedItem.setPrice(80.0);

        ShoppingCartItem regularItem = new ShoppingCartItem();
        regularItem.setProduct(regular);
        regularItem.setPrice(40.0);

        ShoppingCart cart = new ShoppingCart();
        cart.getShoppingCartItemList().add(promotedItem);
        cart.getShoppingCartItemList().add(regularItem);

        service.applyCartItemPromotions(cart);

        assertAll("mixed cart promotions",
            () -> assertEquals(-20.0, promotedItem.getPromoSavings(), 0.001, "promoted savings"),
            () -> assertEquals(60.0, promotedItem.getPrice(), 0.001, "promoted price"),
            () -> assertEquals(0.0, regularItem.getPromoSavings(), 0.001, "regular savings unchanged"),
            () -> assertEquals(40.0, regularItem.getPrice(), 0.001, "regular price unchanged")
        );
    }

    @Test
    void ignoresNullCartForItemPromotions() {
        // Should not throw exception for null cart
        service.applyCartItemPromotions(null);
        
        // Verify service state remains unchanged
        assertEquals(1, service.getPromotions().size());
    }

    @Test
    void ignoresEmptyCartForItemPromotions() {
        ShoppingCart cart = new ShoppingCart();

        service.applyCartItemPromotions(cart);
        
        // Verify cart state is unchanged
        assertEquals(0, cart.getShoppingCartItemList().size());
    }

    @Test
    void grantsFreeShippingWhenCartTotalAtThreshold() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(75.0);
        cart.setShippingTotal(8.99);

        service.applyShippingPromotions(cart);

        assertAll("free shipping at threshold",
            () -> assertEquals(-8.99, cart.getShippingPromoSavings(), 0.001, "shipping promo savings"),
            () -> assertEquals(0.0, cart.getShippingTotal(), 0.001, "shipping zeroed")
        );
    }

    @Test
    void grantsFreeShippingWhenCartTotalAboveThreshold() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(100.0);
        cart.setShippingTotal(10.99);

        service.applyShippingPromotions(cart);

        assertAll("free shipping above threshold",
            () -> assertEquals(-10.99, cart.getShippingPromoSavings(), 0.001, "shipping promo savings"),
            () -> assertEquals(0.0, cart.getShippingTotal(), 0.001, "shipping zeroed")
        );
    }

    @Test
    void doesNotGrantFreeShippingBelowThreshold() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(74.99);
        cart.setShippingTotal(6.99);

        service.applyShippingPromotions(cart);

        assertAll("no free shipping below threshold",
            () -> assertEquals(0.0, cart.getShippingPromoSavings(), 0.001, "no shipping promo savings"),
            () -> assertEquals(6.99, cart.getShippingTotal(), 0.001, "shipping unchanged")
        );
    }

    @Test
    void doesNotGrantFreeShippingAtZeroCartTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(0.0);
        cart.setShippingTotal(2.99);

        service.applyShippingPromotions(cart);

        assertAll("no free shipping at zero",
            () -> assertEquals(0.0, cart.getShippingPromoSavings(), 0.001, "no shipping promo savings"),
            () -> assertEquals(2.99, cart.getShippingTotal(), 0.001, "shipping unchanged")
        );
    }

    @Test
    void ignoresNullCartForShippingPromotions() {
        // Should not throw exception for null cart
        service.applyShippingPromotions(null);
        
        // Verify service state remains unchanged
        assertEquals(1, service.getPromotions().size());
    }

    @Test
    void getPromotionsReturnsDefensiveCopy() {
        Set<Promotion> copy1 = service.getPromotions();
        copy1.clear();

        Set<Promotion> copy2 = service.getPromotions();

        assertEquals(1, copy2.size());
    }

    @Test
    void setPromotionsReplacesAllPromotions() {
        Promotion custom = new Promotion("CUSTOM", 0.50);
        Set<Promotion> customSet = new HashSet<>();
        customSet.add(custom);

        service.setPromotions(customSet);

        Set<Promotion> promotions = service.getPromotions();

        assertEquals(1, promotions.size());
        assertTrue(promotions.stream().anyMatch(p -> p.getItemId().equals("CUSTOM") && p.getPercentOff() == 0.50));
    }

    @Test
    void setPromotionsWithNullClearsPromotions() {
        service.setPromotions(null);

        Set<Promotion> promotions = service.getPromotions();

        assertTrue(promotions.isEmpty());
    }

    @Test
    void setPromotionsCreatesDefensiveCopy() {
        Promotion custom = new Promotion("CUSTOM", 0.50);
        Set<Promotion> customSet = new HashSet<>();
        customSet.add(custom);

        service.setPromotions(customSet);
        customSet.clear();

        Set<Promotion> promotions = service.getPromotions();

        assertEquals(1, promotions.size());
    }

    @Test
    void appliesItemPromotionCorrectlyWithFractionalPrice() {
        Product product = new Product("329299", "Promoted Item", "Desc", 33.33);
        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setPrice(33.33);

        ShoppingCart cart = new ShoppingCart();
        cart.getShoppingCartItemList().add(item);

        service.applyCartItemPromotions(cart);

        assertAll("fractional price promotion",
            () -> assertEquals(33.33 * 0.25 * -1, item.getPromoSavings(), 0.001, "promo savings"),
            () -> assertEquals(33.33 * 0.75, item.getPrice(), 0.001, "discounted price")
        );
    }

    @Test
    void appliesShippingPromotionWithZeroShippingTotal() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(80.0);
        cart.setShippingTotal(0.0);

        service.applyShippingPromotions(cart);

        assertAll("free shipping with zero shipping",
            () -> assertEquals(0.0, cart.getShippingPromoSavings(), 0.001, "no negative savings"),
            () -> assertEquals(0.0, cart.getShippingTotal(), 0.001, "shipping remains zero")
        );
    }

    @Test
    void promotionSavingsAreNegative() {
        Product product = new Product("329299", "Promoted Item", "Desc", 100.0);
        ShoppingCartItem item = new ShoppingCartItem();
        item.setProduct(product);
        item.setPrice(100.0);

        ShoppingCart cart = new ShoppingCart();
        cart.getShoppingCartItemList().add(item);

        service.applyCartItemPromotions(cart);

        assertTrue(item.getPromoSavings() < 0, "promo savings should be negative");
    }

    @Test
    void shippingPromoSavingsAreNegative() {
        ShoppingCart cart = new ShoppingCart();
        cart.setCartItemTotal(80.0);
        cart.setShippingTotal(5.0);

        service.applyShippingPromotions(cart);

        assertTrue(cart.getShippingPromoSavings() < 0, "shipping promo savings should be negative");
    }

    @Test
    void getPromotionsIsNeverNull() {
        service.setPromotions(null);

        assertNotNull(service.getPromotions());
    }

    @Test
    void multiplePromotionsCanBeSet() {
        Set<Promotion> multi = new HashSet<>();
        multi.add(new Promotion("A", 0.10));
        multi.add(new Promotion("B", 0.20));
        multi.add(new Promotion("C", 0.30));

        service.setPromotions(multi);

        assertEquals(3, service.getPromotions().size());
    }

    @Test
    void appliesMultiplePromotionsToCart() {
        Set<Promotion> multi = new HashSet<>();
        multi.add(new Promotion("P1", 0.10));
        multi.add(new Promotion("P2", 0.20));

        service.setPromotions(multi);

        Product p1 = new Product("P1", "Item A", "Desc", 100.0);
        Product p2 = new Product("P2", "Item B", "Desc", 100.0);

        ShoppingCartItem item1 = new ShoppingCartItem();
        item1.setProduct(p1);
        item1.setPrice(100.0);

        ShoppingCartItem item2 = new ShoppingCartItem();
        item2.setProduct(p2);
        item2.setPrice(100.0);

        ShoppingCart cart = new ShoppingCart();
        cart.getShoppingCartItemList().add(item1);
        cart.getShoppingCartItemList().add(item2);

        service.applyCartItemPromotions(cart);

        assertAll("multiple promotions",
            () -> assertEquals(-10.0, item1.getPromoSavings(), 0.001, "item1 savings"),
            () -> assertEquals(90.0, item1.getPrice(), 0.001, "item1 price"),
            () -> assertEquals(-20.0, item2.getPromoSavings(), 0.001, "item2 savings"),
            () -> assertEquals(80.0, item2.getPrice(), 0.001, "item2 price")
        );
    }
}
