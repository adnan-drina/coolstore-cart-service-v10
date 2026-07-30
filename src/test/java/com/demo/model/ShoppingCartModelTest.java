package com.demo.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

class ShoppingCartModelTest {

  @Test
  void defaultConstructorLeavesFieldsNullOrZero() {
    ShoppingCart cart = new ShoppingCart();

    assertNull(cart.getCartId());
    assertEquals(0.0, cart.getCartItemTotal(), 0.0);
    assertEquals(0.0, cart.getCartItemPromoSavings(), 0.0);
    assertEquals(0.0, cart.getShippingTotal(), 0.0);
    assertEquals(0.0, cart.getShippingPromoSavings(), 0.0);
    assertEquals(0.0, cart.getCartTotal(), 0.0);
    assertTrue(cart.getShoppingCartItemList().isEmpty());
  }

  @Test
  void parameterizedConstructorSetsCartId() {
    ShoppingCart cart = new ShoppingCart("CART-001");

    assertEquals("CART-001", cart.getCartId());
    assertTrue(cart.getShoppingCartItemList().isEmpty());
  }

  @Test
  void gettersAndSettersRoundTrip() {
    ShoppingCart cart = new ShoppingCart();

    cart.setCartId("CART-002");
    cart.setCartItemTotal(100.0);
    cart.setCartItemPromoSavings(10.0);
    cart.setShippingTotal(5.0);
    cart.setShippingPromoSavings(1.0);
    cart.setCartTotal(94.0);

    assertAll("ShoppingCart getters",
        () -> assertEquals("CART-002", cart.getCartId()),
        () -> assertEquals(100.0, cart.getCartItemTotal(), 0.0),
        () -> assertEquals(10.0, cart.getCartItemPromoSavings(), 0.0),
        () -> assertEquals(5.0, cart.getShippingTotal(), 0.0),
        () -> assertEquals(1.0, cart.getShippingPromoSavings(), 0.0),
        () -> assertEquals(94.0, cart.getCartTotal(), 0.0)
    );
  }

  @Test
  void addItemListRoundTrip() {
    ShoppingCart cart = new ShoppingCart();
    Product product = new Product("ITEM001", "Widget", "A fine widget", 29.99);
    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(29.99);
    item.setQuantity(2);
    item.setProduct(product);

    List<ShoppingCartItem> items = new ArrayList<>();
    items.add(item);
    cart.setShoppingCartItemList(items);

    assertSame(items, cart.getShoppingCartItemList());
    assertEquals(1, cart.getShoppingCartItemList().size());
    assertSame(item, cart.getShoppingCartItemList().get(0));
  }

  @Test
  void addShoppingCartItemAddsNonNullItem() {
    ShoppingCart cart = new ShoppingCart();
    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(10.0);
    item.setQuantity(1);

    cart.addShoppingCartItem(item);

    assertEquals(1, cart.getShoppingCartItemList().size());
    assertSame(item, cart.getShoppingCartItemList().get(0));
  }

  @Test
  void addShoppingCartItemIgnoresNull() {
    ShoppingCart cart = new ShoppingCart();

    cart.addShoppingCartItem(null);

    assertTrue(cart.getShoppingCartItemList().isEmpty());
  }

  @Test
  void addShoppingCartItemAddsMultipleItems() {
    ShoppingCart cart = new ShoppingCart();

    ShoppingCartItem item1 = new ShoppingCartItem();
    item1.setPrice(10.0);
    item1.setQuantity(1);

    ShoppingCartItem item2 = new ShoppingCartItem();
    item2.setPrice(20.0);
    item2.setQuantity(2);

    cart.addShoppingCartItem(item1);
    cart.addShoppingCartItem(item2);

    assertEquals(2, cart.getShoppingCartItemList().size());
    assertSame(item1, cart.getShoppingCartItemList().get(0));
    assertSame(item2, cart.getShoppingCartItemList().get(1));
  }

  @Test
  void removeShoppingCartItemRemovesMatchingItem() {
    ShoppingCart cart = new ShoppingCart();
    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(10.0);
    item.setQuantity(1);

    cart.addShoppingCartItem(item);

    boolean removed = cart.removeShoppingCartItem(item);

    assertTrue(removed);
    assertTrue(cart.getShoppingCartItemList().isEmpty());
  }

  @Test
  void removeShoppingCartItemReturnsFalseForNull() {
    ShoppingCart cart = new ShoppingCart();

    boolean removed = cart.removeShoppingCartItem(null);

    assertFalse(removed);
  }

  @Test
  void removeShoppingCartItemReturnsFalseForAbsentItem() {
    ShoppingCart cart = new ShoppingCart();
    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(10.0);
    item.setQuantity(1);

    boolean removed = cart.removeShoppingCartItem(item);

    assertFalse(removed);
  }

  @Test
  void resetShoppingCartItemListClearsList() {
    ShoppingCart cart = new ShoppingCart();

    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(10.0);
    item.setQuantity(1);
    cart.addShoppingCartItem(item);

    assertEquals(1, cart.getShoppingCartItemList().size());

    cart.resetShoppingCartItemList();

    assertTrue(cart.getShoppingCartItemList().isEmpty());
  }

  @Test
  void serialVersionUidPreserved() {
    try {
      var field = ShoppingCart.class.getDeclaredField("serialVersionUID");
      field.setAccessible(true);
      assertEquals(-1108043957592113528L, field.get(null));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError("serialVersionUID not accessible", e);
    }
  }

  @Test
  void serializationRoundTripPreservesFields() throws Exception {
    ShoppingCart original = new ShoppingCart("CART-003");
    original.setCartItemTotal(50.0);
    original.setCartItemPromoSavings(5.0);
    original.setShippingTotal(3.0);
    original.setShippingPromoSavings(0.5);
    original.setCartTotal(47.5);

    Product product = new Product("ITEM001", "Widget", "A fine widget", 29.99);
    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(29.99);
    item.setQuantity(2);
    item.setPromoSavings(5.0);
    item.setProduct(product);
    original.addShoppingCartItem(item);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(original);
    }

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    try (ObjectInputStream ois = new ObjectInputStream(bais)) {
      ShoppingCart deserialized = (ShoppingCart) ois.readObject();

      assertAll("Deserialized ShoppingCart",
          () -> assertEquals("CART-003", deserialized.getCartId()),
          () -> assertEquals(50.0, deserialized.getCartItemTotal(), 0.0),
          () -> assertEquals(5.0, deserialized.getCartItemPromoSavings(), 0.0),
          () -> assertEquals(3.0, deserialized.getShippingTotal(), 0.0),
          () -> assertEquals(0.5, deserialized.getShippingPromoSavings(), 0.0),
          () -> assertEquals(47.5, deserialized.getCartTotal(), 0.0),
          () -> assertEquals(1, deserialized.getShoppingCartItemList().size()),
          () -> assertEquals(29.99, deserialized.getShoppingCartItemList().get(0).getPrice(), 0.0)
      );
    }
  }

  @Test
  void toStringContainsAllFields() {
    ShoppingCart cart = new ShoppingCart("CART-004");
    cart.setCartItemTotal(100.0);
    cart.setCartItemPromoSavings(10.0);
    cart.setShippingTotal(5.0);
    cart.setShippingPromoSavings(1.0);
    cart.setCartTotal(94.0);

    String str = cart.toString();

    assertAll("toString content",
        () -> assertTrue(str.contains("CART-004")),
        () -> assertTrue(str.contains("cartItemTotal=100.0")),
        () -> assertTrue(str.contains("cartItemPromoSavings=10.0")),
        () -> assertTrue(str.contains("shippingTotal=5.0")),
        () -> assertTrue(str.contains("shippingPromoSavings=1.0")),
        () -> assertTrue(str.contains("cartTotal=94.0"))
    );
  }

  @Test
  void toStringMatchesLegacyFormat() {
    ShoppingCart cart = new ShoppingCart("CART-001");
    cart.setCartItemTotal(100.0);
    cart.setCartItemPromoSavings(10.0);
    cart.setShippingTotal(5.0);
    cart.setShippingPromoSavings(1.0);
    cart.setCartTotal(94.0);

    String str = cart.toString();

    assertTrue(str.startsWith("ShoppingCart [cartId=CART-001"));
    assertTrue(str.contains("cartItemTotal=100.0"));
    assertTrue(str.contains("cartItemPromoSavings=10.0"));
    assertTrue(str.contains("shippingTotal=5.0"));
    assertTrue(str.contains("shippingPromoSavings=1.0"));
    assertTrue(str.contains("cartTotal=94.0"));
    assertTrue(str.contains("shoppingCartItemList="));
    assertTrue(str.endsWith("]"));
  }
}
