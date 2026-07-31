package com.demo.rest;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;

import java.math.BigDecimal;
import java.util.UUID;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static io.restassured.config.JsonConfig.jsonConfig;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import io.restassured.RestAssured;
import io.restassured.path.json.config.JsonPathConfig;

@QuarkusTest
@QuarkusTestResource(CatalogWireMockResource.class)
class CartEndpointTest {

    private String currentTestCartId;

    @BeforeAll
    static void bigDecimalJson() {
        RestAssured.config = RestAssured.config().jsonConfig(
            jsonConfig().numberReturnType(JsonPathConfig.NumberReturnType.BIG_DECIMAL));
    }

    @BeforeEach
    void setupTestIsolation() {
        currentTestCartId = "cart-" + UUID.randomUUID().toString().substring(0, 8);
    }

    private String getCartId() {
        // Unique id per call (O-TESTISO) — BeforeEach field alone makes source==target in set().
        return "cart-" + UUID.randomUUID().toString().substring(0, 8);
    }

    // --- GET /cart/{cartId} ---

    @Test
    void returnsEmptyCartForUnknownCartId() {
        given()
            .when().get("/cart/unknown")
            .then()
            .statusCode(200)
            .body("cartId", is("unknown"))
            .body("cartItemTotal", comparesEqualTo(BigDecimal.ZERO))
            .body("cartItemPromoSavings", comparesEqualTo(BigDecimal.ZERO))
            .body("shippingTotal", comparesEqualTo(BigDecimal.ZERO))
            .body("shippingPromoSavings", comparesEqualTo(BigDecimal.ZERO))
            .body("cartTotal", comparesEqualTo(BigDecimal.ZERO))
            .body("shoppingCartItemList", is(empty()));
    }

    // --- POST /cart/{cartId}/{itemId}/{quantity} (add) ---

    @Test
    void addsItemToCartWithCorrectPricing() {
        String cartId = getCartId();
        given()
            .when().post("/cart/" + cartId + "/1111/2")
            .then()
            .statusCode(200)
            .body("cartId", is(cartId))
            .body("cartItemTotal", comparesEqualTo(new BigDecimal("2000.0")))
            .body("cartItemPromoSavings", comparesEqualTo(BigDecimal.ZERO))
            .body("shippingPromoSavings", comparesEqualTo(new BigDecimal("-10.99")))
            .body("cartTotal", comparesEqualTo(new BigDecimal("2000.0")))
            .body("shoppingCartItemList", notNullValue())
            .body("shoppingCartItemList.size()", is(1));
    }

    @Test
    void addsPromotedItemWithDiscount() {
        String cartId = getCartId();
        given()
            .when().post("/cart/" + cartId + "/329299/1")
            .then()
            .statusCode(200)
            .body("cartId", is(cartId))
            .body("cartItemPromoSavings", notNullValue());
    }

    @Test
    void returnsBadRequestForZeroQuantity() {
        String cartId = getCartId();
        given()
            .when().post("/cart/" + cartId + "/1111/0")
            .then()
            .statusCode(400);
    }

    @Test
    void returnsCartUnchangedForUnknownProduct() {
        String cartId = getCartId();
        given()
            .when().post("/cart/" + cartId + "/999999/1")
            .then()
            .statusCode(200)
            .body("cartId", is(cartId))
            .body("shoppingCartItemList", is(empty()));
    }

    // --- POST /cart/{cartId}/{tmpId} (set) ---

    @Test
    void setsCartContentsFromTempCart() {
        String sourceCartId = getCartId();
        String targetCartId = getCartId();
        given().when().post("/cart/" + sourceCartId + "/1111/2")
            .then()
            .statusCode(200)
            .body("cartItemTotal", comparesEqualTo(new BigDecimal("2000.0")))
            .body("shoppingCartItemList.size()", is(1));

        given()
            .when().post("/cart/" + targetCartId + "/" + sourceCartId)
            .then()
            .statusCode(200)
            .body("cartId", is(targetCartId))
            .body("shoppingCartItemList.size()", is(1))
            .body("cartItemTotal", comparesEqualTo(new BigDecimal("2000.0")));
    }

    // --- DELETE /cart/{cartId}/{itemId}/{quantity} ---

    @Test
    void removesItemFromCart() {
        String cartId = getCartId();
        // Add item to cart first
        given().when().post("/cart/" + cartId + "/1111/2")
            .then()
            .statusCode(200);

        // Remove one item (quantity 1) from the cart
        given()
            .when().delete("/cart/" + cartId + "/1111/1")
            .then()
            .statusCode(200)
            .body("cartId", is(cartId))
            .body("shoppingCartItemList.size()", is(1))
            .body("shoppingCartItemList.find { it.product.itemId == '1111' }.quantity", is(1));
    }

    @Test
    void removesAllItemsWhenQuantityExceeds() {
        String cartId = getCartId();
        given().when().post("/cart/" + cartId + "/1111/2");

        given()
            .when().delete("/cart/" + cartId + "/1111/5")
            .then()
            .statusCode(200)
            .body("cartId", is(cartId))
            .body("shoppingCartItemList", is(empty()));
    }

    @Test
    void returnsBadRequestForZeroDeleteQuantity() {
        String cartId = getCartId();
        given()
            .when().delete("/cart/" + cartId + "/1111/0")
            .then()
            .statusCode(400);
    }

    // --- POST /cart/checkout/{cartId} ---

    @Test
    void clearsCartAfterCheckout() {
        String cartId = getCartId();
        given().when().post("/cart/" + cartId + "/1111/2");

        given()
            .when().post("/cart/checkout/" + cartId)
            .then()
            .statusCode(200)
            .body("cartId", is(cartId))
            .body("shoppingCartItemList", is(empty()))
            .body("cartTotal", comparesEqualTo(BigDecimal.ZERO));
    }

    // --- Session / state persistence across requests ---

    @Test
    void preservesCartStateAcrossMultipleRequests() {
        String cartId = getCartId();
        given().when().post("/cart/" + cartId + "/1111/1")
            .then()
            .statusCode(200)
            .body("shoppingCartItemList.size()", is(1));

        given().when().post("/cart/" + cartId + "/2222/1")
            .then()
            .statusCode(200)
            .body("shoppingCartItemList.size()", is(2));

        given()
            .when().get("/cart/" + cartId)
            .then()
            .statusCode(200)
            .body("shoppingCartItemList.size()", is(2));
    }

    // --- CATALOG_ENDPOINT / WireMock integration ---

    @Test
    void usesCatalogEndpointForProductData() {
        String cartId = getCartId();
        given()
            .when().post("/cart/" + cartId + "/1111/1")
            .then()
            .statusCode(200)
            .body("shoppingCartItemList.find { it.product.itemId == '1111' }.price", comparesEqualTo(new BigDecimal("1000")));
    }
}
