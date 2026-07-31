package com.demo;

import com.demo.model.ShoppingCart;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

@QuarkusTest
class CartServiceBoundaryTest {

    @Test
    void should_add_item_to_shopping_cart() {
        final ShoppingCart shoppingCart = given()
            .when()
            .post("/cart/1/1111/2")
            .as(ShoppingCart.class);

        assertThat(shoppingCart)
            .returns(0.0, ShoppingCart::getCartItemPromoSavings)
            .returns(2000.0, ShoppingCart::getCartItemTotal)
            .returns(-10.99, ShoppingCart::getShippingPromoSavings)
            .returns(2000.0, ShoppingCart::getCartTotal)
            .extracting(ShoppingCart::getShoppingCartItemList)
            .asList()
            .hasSize(1);
    }
}
