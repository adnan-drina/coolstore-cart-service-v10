package com.demo.service;

import com.demo.ProductsObjectMother;
import com.demo.model.Product;
import com.demo.model.ShoppingCart;
import com.demo.model.ShoppingCartItem;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@QuarkusTest
class ShoppingCartServiceTest {

    @InjectMock
    @RestClient
    CatalogService catalogService;

    @Inject
    ShoppingCartService shoppingCartService;

    @BeforeEach
    void stubCatalog() {
        when(this.catalogService.getProducts())
            .thenReturn(ProductsObjectMother.createVehicleProducts());
    }

    @Test
    void should_get_initialized_shopping_cart_in_case_of_not_exists() {
        final ShoppingCart shoppingCart = shoppingCartService.getShoppingCart("cart-init-empty");

        assertThat(shoppingCart)
            .returns(0.0, ShoppingCart::getCartItemPromoSavings)
            .returns(0.0, ShoppingCart::getCartItemTotal)
            .returns(0.0, ShoppingCart::getShippingPromoSavings)
            .returns(0.0, ShoppingCart::getCartTotal);
    }

    @Test
    void should_calculate_price_of_cart() {
        final ShoppingCart shoppingCart = shoppingCartService.getShoppingCart("cart-price-1");
        ShoppingCartItem sci = new ShoppingCartItem();
        sci.setProduct(new Product("1111", "Car", "Super car", 1000));
        sci.setQuantity(2);
        sci.setPrice(1000);
        shoppingCart.addShoppingCartItem(sci);

        shoppingCartService.priceShoppingCart(shoppingCart);

        assertThat(shoppingCart)
            .returns(0.0, ShoppingCart::getCartItemPromoSavings)
            .returns(2000.0, ShoppingCart::getCartItemTotal)
            .returns(-10.99, ShoppingCart::getShippingPromoSavings)
            .returns(2000.0, ShoppingCart::getCartTotal);
    }

    @Test
    void should_get_product_id() {
        final Product product = shoppingCartService.getProduct("2222");

        assertThat(product)
            .isNotNull()
            .returns("2222", Product::getItemId)
            .returns("Bike", Product::getName);
    }
}
