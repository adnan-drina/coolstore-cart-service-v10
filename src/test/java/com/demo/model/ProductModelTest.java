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

class ProductModelTest {

  @Test
  void defaultConstructorLeavesFieldsNullOrZero() {
    Product product = new Product();

    assertNull(product.getItemId());
    assertNull(product.getName());
    assertNull(product.getDesc());
    assertEquals(0.0, product.getPrice(), 0.0);
  }

  @Test
  void parameterizedConstructorSetsAllFields() {
    Product product = new Product("ITEM001", "Widget", "A fine widget", 29.99);

    assertAll("Product fields",
        () -> assertEquals("ITEM001", product.getItemId()),
        () -> assertEquals("Widget", product.getName()),
        () -> assertEquals("A fine widget", product.getDesc()),
        () -> assertEquals(29.99, product.getPrice(), 0.0)
    );
  }

  @Test
  void gettersAndSettersRoundTrip() {
    Product product = new Product();

    product.setItemId("ITEM002");
    product.setName("Gadget");
    product.setDesc("A useful gadget");
    product.setPrice(49.95);

    assertAll("Product getters",
        () -> assertEquals("ITEM002", product.getItemId()),
        () -> assertEquals("Gadget", product.getName()),
        () -> assertEquals("A useful gadget", product.getDesc()),
        () -> assertEquals(49.95, product.getPrice(), 0.0)
    );
  }

  @Test
  void serialVersionUidPreserved() {
    try {
      var field = Product.class.getDeclaredField("serialVersionUID");
      field.setAccessible(true);
      assertEquals(-7304814269819778382L, field.get(null));
    } catch (NoSuchFieldException | IllegalAccessException e) {
      throw new AssertionError("serialVersionUID not accessible", e);
    }
  }

  @Test
  void serializationRoundTripPreservesFields() throws Exception {
    Product original = new Product("ITEM003", "Doohickey", "A neat doohickey", 15.50);

    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
      oos.writeObject(original);
    }

    ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
    try (ObjectInputStream ois = new ObjectInputStream(bais)) {
      Product deserialized = (Product) ois.readObject();

      assertAll("Deserialized Product",
          () -> assertEquals("ITEM003", deserialized.getItemId()),
          () -> assertEquals("Doohickey", deserialized.getName()),
          () -> assertEquals("A neat doohickey", deserialized.getDesc()),
          () -> assertEquals(15.50, deserialized.getPrice(), 0.0)
      );
    }
  }

  @Test
  void toStringContainsAllFields() {
    Product product = new Product("ITEM004", "Thingamajig", "A cool thing", 9.99);
    String str = product.toString();

    assertAll("toString content",
        () -> assertTrue(str.contains("ITEM004")),
        () -> assertTrue(str.contains("Thingamajig")),
        () -> assertTrue(str.contains("A cool thing")),
        () -> assertTrue(str.contains("9.99"))
    );
  }

  @Test
  void toStringMatchesLegacyFormat() {
    Product product = new Product("ITEM001", "Widget", "A fine widget", 29.99);

    String expected = "Product [itemId=ITEM001, name=Widget, desc=A fine widget, price=29.99]";
    assertEquals(expected, product.toString());
  }
}
