package com.demo.model;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.jupiter.api.Test;

class ShoppingCartItemModelTest {

  @Test
  void defaultConstructorLeavesFieldsNullOrZero() {
    ShoppingCartItem item = new ShoppingCartItem();

    assertEquals(0.0, item.getPrice(), 0.0);
    assertEquals(0, item.getQuantity());
    assertEquals(0.0, item.getPromoSavings(), 0.0);
    assertNull(item.getProduct());
  }

  @Test
  void gettersAndSettersRoundTrip() {
    ShoppingCartItem item = new ShoppingCartItem();
    Product product = new Product("ITEM001", "Widget", "A fine widget", 29.99);

    item.setPrice(29.99);
    item.setQuantity(3);
    item.setPromoSavings(5.0);
    item.setProduct(product);

    assertAll("ShoppingCartItem getters",
        () -> assertEquals(29.99, item.getPrice(), 0.0),
        () -> assertEquals(3, item.getQuantity()),
        () -> assertEquals(5.0, item.getPromoSavings(), 0.0),
        () -> assertEquals(product, item.getProduct())
    );
  }

  @Test
  void productReferencePreserved() {
    ShoppingCartItem item = new ShoppingCartItem();
    Product product = new Product("ITEM002", "Gadget", "A useful gadget", 49.95);

    item.setProduct(product);

    assertEquals(product, item.getProduct());
    assertEquals("ITEM002", item.getProduct().getItemId());
    assertEquals("Gadget", item.getProduct().getName());
  }

  @Test
  void serialVersionUidPreserved() {
    try {
      var field = ShoppingCartItem.class.getDeclaredField("serialVersionUID");
      field.setAccessible(true);
      assertEquals(6964558044240061049L, field.get(null));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError("serialVersionUID not accessible", e);
    }
  }

  @Test
  void serializationRoundTripPreservesFields() throws Exception {
    Product product = new Product("ITEM003", "Doohickey", "A neat doohickey", 15.50);
    ShoppingCartItem original = new ShoppingCartItem();
    original.setPrice(15.50);
    original.setQuantity(2);
    original.setPromoSavings(3.0);
    original.setProduct(product);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(original);
    }

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    try (ObjectInputStream ois = new ObjectInputStream(bais)) {
      ShoppingCartItem deserialized = (ShoppingCartItem) ois.readObject();

      assertAll("Deserialized ShoppingCartItem",
          () -> assertEquals(15.50, deserialized.getPrice(), 0.0),
          () -> assertEquals(2, deserialized.getQuantity()),
          () -> assertEquals(3.0, deserialized.getPromoSavings(), 0.0),
          () -> assertEquals("ITEM003", deserialized.getProduct().getItemId())
      );
    }
  }

  @Test
  void toStringContainsAllFields() {
    Product product = new Product("ITEM004", "Thingamajig", "A cool thing", 9.99);
    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(9.99);
    item.setQuantity(1);
    item.setPromoSavings(1.0);
    item.setProduct(product);

    String str = item.toString();

    assertAll("toString content",
        () -> assertTrue(str.contains("9.99")),
        () -> assertTrue(str.contains("1")),
        () -> assertTrue(str.contains("1.0"))
    );
  }

  @Test
  void toStringMatchesLegacyFormat() {
    Product product = new Product("ITEM001", "Widget", "A fine widget", 29.99);
    ShoppingCartItem item = new ShoppingCartItem();
    item.setPrice(29.99);
    item.setQuantity(3);
    item.setPromoSavings(5.0);
    item.setProduct(product);

    String expected = "ShoppingCartItem [price=29.99, quantity=3, promoSavings=5.0, product=Product [itemId=ITEM001, name=Widget, desc=A fine widget, price=29.99]]";
    assertEquals(expected, item.toString());
  }
}
