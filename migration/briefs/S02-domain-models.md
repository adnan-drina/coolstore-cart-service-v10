# S02: Domain model harvest

<!-- The brief is the self-contained work order for one modernization
     story. Bar: a competent developer or a fresh session starts the
     story from THIS FILE ALONE. Fill every section; delete none. -->

## Goal & position

What this story achieves and why it is next: its place in the roadmap,
what it unblocks, which stories it depends on (cite
dependency-order.md / architecture-profile.md).

This story harvests the core domain models with their exact legacy behavior preserved. Following the dependency order (dependency-order.md lines 18-24), Product, Promotion, ShoppingCartItem, and ShoppingCart are the god nodes with highest fan-in that must be stabilized before dependent services. These HARVEST classes preserve all legacy behavior and provide the foundation for service layer modernization in S03.

## In scope

The exact legacy classes/files this story modernizes. For each, quote
the load-bearing legacy code (the lines being transformed — imports,
annotations, key methods), so the story never starts from a blank
read:

- `src/main/java/com/redhat/coolstore/model/Product.java` — Product domain model
  ```java
  package com.redhat.coolstore.model;
  
  import java.io.Serializable;
  
  public class Product implements Serializable {
  
      private static final long serialVersionUID = -7304814269819778382L;
      private String itemId;
      private String name;
      private String desc;
      private double price;
      
      public Product() {
          
      }
      
      public Product(String itemId, String name, String desc, double price) {
          super();
          this.itemId = itemId;
          this.name = name;
          this.desc = desc;
          this.price = price;
      }
  ```

- `src/main/java/com/redhat/coolstore/model/Promotion.java` — Promotion domain model
  ```java
  package com.redhat.coolstore.model;
  
  public class Promotion {
  
      private String itemId;
      private double percentOff;
      
      public Promotion() {
          
      }
      
      public Promotion(String itemId, double percentOff) {
          super();
          this.itemId = itemId;
          this.percentOff = percentOff;
      }
  ```

- `src/main/java/com/redhat/coolstore/model/ShoppingCartItem.java` — Cart item domain model
  ```java
  package com.redhat.coolstore.model;
  
  import java.io.Serializable;
  
  public class ShoppingCartItem implements Serializable {
      
      private static final long serialVersionUID = 6964558044240061049L;
      private double price;
      private int quantity;
      private double promoSavings;
      private Product product;
  ```

- `src/main/java/com/redhat/coolstore/model/ShoppingCart.java` — Shopping cart domain model
  ```java
  package com.redhat.coolstore.model;
  
  import java.io.Serializable;
  import java.util.ArrayList;
  import java.util.List;
  
  public class ShoppingCart implements Serializable {
  
      private static final long serialVersionUID = -1108043957592113528L;
      private double cartItemTotal;
      private double cartItemPromoSavings;
      private double shippingTotal;
      private double shippingPromoSavings;
      private double cartTotal;
      private String cartId;
      private List<ShoppingCartItem> shoppingCartItemList = new ArrayList<ShoppingCartItem>();
  ```

## Out of scope

What neighboring code this story must NOT touch, and which story owns
it. (The tree must stay buildable: name any temporary seams — e.g. a
dependent class that keeps compiling against the old shape until its
own story.)

Service classes (ShoppingCartService, PromoService, ShippingService, CatalogService) remain unchanged in Spring configuration. REST endpoints (CartEndpoint) continue using Spring annotations until S04. The application bootstrap (CartServiceApplication) remains Spring Boot until S06.

## Class roles & target contract (from architecture-profile §7)

For each in-scope class, its role and — for REDESIGN classes — the target
contract carried forward from profile §7, so M3 writes tasks and tests to
the target (not the legacy):

- `ShoppingCart` — HARVEST
  - Preserve existing behavior: cart ID management, item list manipulation, total calculation fields, serialization compatibility
  - Target: POJO with Jackson annotations for JSON serialization, preserved field names and types for test compatibility

- `Product` — HARVEST  
  - Preserve existing behavior: item ID, name, description, price fields, constructors, serialization compatibility
  - Target: POJO with Jackson annotations, preserved field names and types for integration with catalog service

- `ShoppingCartItem` — HARVEST
  - Preserve existing behavior: product reference, quantity management, price calculations, promo savings tracking
  - Target: POJO with Jackson annotations, preserved field names and types for cart operations

- `Promotion` — HARVEST
  - Preserve existing behavior: item ID and percent off fields, constructors, promotion rule data
  - Target: POJO with Jackson annotations, preserved field structure for promotion service compatibility

## Decided target shapes

The MAPPINGS.md rows that apply (quote the decided target, don't
re-decide). Recipe-executed rules already handled: reference
`migration/recipe-log.md` and `migration/staging/` where applicable.

**Story ordering:** extensions and BOM first, then models, then resources,
then config keys, then tests (`extensions → models → resources → config →
tests`).

This story applies javax-to-jakarta-import-00001 recipe transformation:
- javax.* imports → jakarta.* imports (line 3: `java.util` unchanged, javax-to-jakarta applies to javax.* packages only)

Model classes are HARVEST classes, preserving all legacy behavior, field names, and method signatures. Package rename com.redhat.coolstore → com.demo applied during harvest.

## Contracts owned by this story

- **Findings**: the mandatory rule ids this story resolves (from the
  roadmap entry).
  - javax-to-jakarta-import-00001 (recipe-executed for model classes)

- **Preserve**: the `preserve:` items whose surfaces live in scope —
  spell out the env var names/values mechanism to keep.
  - No preserve items directly in scope; CATALOG_ENDPOINT preserved at configuration level

- **Behavioral pins**: the assertion values that must hold after this
  story (quote numbers/strings and their test source). Harvest classes
  and behavior-preserving redesign pin LEGACY values; behavior-changing
  redesign pins the §7 TARGET (e.g. 404, not create-on-GET). Name the
  contract GAPS this story closes with characterization tests.
  - **Product field preservation**: itemId, name, desc, price fields exactly as legacy
  - **ShoppingCart field preservation**: cartId, cartItemTotal, cartItemPromoSavings, shippingTotal, shippingPromoSavings, cartTotal, shoppingCartItemList
  - **ShoppingCartItem field preservation**: price, quantity, promoSavings, product reference
  - **Promotion field preservation**: itemId, percentOff fields exactly as legacy
  - **Serialization compatibility**: serialVersionUID fields preserved for backward compatibility
  - **Constructor behavior**: default and parameterized constructors preserve legacy behavior

- **Forbidden**: the fabrication tripwires relevant here.
  - No method signature changes (getters/setters must match legacy)
  - No field type changes (maintain exact types for serialization compatibility)
  - No behavior changes (HARVEST classes are behavior-preserving)

## Done-criteria

Checkable, story-scoped:
- builds + `sensors.sh task` green at every commit; milestone green at
  story end
- All model classes compile with Jakarta imports and com.demo package
- Legacy test assertions from ShoppingCartServiceTest continue to pass
- Model classes preserve all legacy field names, types, and method signatures
- Package rename com.redhat.coolstore → com.demo applied correctly
