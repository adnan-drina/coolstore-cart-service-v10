# S02 Domain Model Harvest - Tasks

**UI surface coverage**: The cart service exposes REST API endpoints `/api/cart/*` via CartEndpoint (legacy user-facing interface for web and mobile clients). REST API endpoint modernization is covered in service layer stories (S03/S04). This domain model story (S02) focuses on model migration with exact behavior preservation.

## Task List (ordered: rewrite → infer)

#### T-001: Create model package structure
**Class**: rewrite
**Findings**: none
**Goal**: Establish Quarkus-compatible package directory structure for migrated models
**Target design**: → `src/main/java/com/demo/model/`
- **Package creation**: src/main/java/com/demo/model/ directory with .gitkeep
**Acceptance**: `src/main/java/com/demo/model/` directory with .gitkeep exists; sensors green

#### T-002: Harvest Product model
**Class**: rewrite
**Findings**: javax-to-jakarta-import-00001 (recipe-executed)
**Goal**: Migrate Product from legacy to Quarkus with exact behavior preservation
**Target design**: → `src/main/java/com/demo/model/Product.java`
- **Owns**: src/main/java/com/redhat/coolstore/model/Product.java
- **Package rename**: com.redhat.coolstore.model → com.demo.model
- **Field preservation**: itemId, name, desc, price fields exactly as legacy
- **Method preservation**: all getters/setters, constructors, toString() exactly as legacy
- **Serialization**: serialVersionUID = -7304814269819778382L preserved
**Acceptance**: Product.java compiles; field/method signatures match legacy; sensors green

#### T-003: Harvest Promotion model  
**Class**: rewrite
**Findings**: javax-to-jakarta-import-00001 (recipe-executed)
**Goal**: Migrate Promotion from legacy to Quarkus with exact behavior preservation
**Target design**: → `src/main/java/com/demo/model/Promotion.java`
- **Owns**: src/main/java/com/redhat/coolstore/model/Promotion.java
- **Package rename**: com.redhat.coolstore.model → com.demo.model
- **Field preservation**: itemId, percentOff fields exactly as legacy
- **Method preservation**: all getters/setters, constructors, toString() exactly as legacy
**Acceptance**: Promotion.java compiles; field/method signatures match legacy; sensors green

#### T-004: Harvest ShoppingCartItem model
**Class**: rewrite
**Findings**: javax-to-jakarta-import-00001 (recipe-executed)
**Goal**: Migrate ShoppingCartItem from legacy to Quarkus with exact behavior preservation
**Target design**: → `src/main/java/com/demo/model/ShoppingCartItem.java`
- **Owns**: src/main/java/com/redhat/coolstore/model/ShoppingCartItem.java
- **Package rename**: com.redhat.coolstore.model → com.demo.model
- **Field preservation**: price, quantity, promoSavings, product reference exactly as legacy
- **Method preservation**: all getters/setters, constructors, toString() exactly as legacy
- **Serialization**: serialVersionUID = 6964558044240061049L preserved
**Acceptance**: ShoppingCartItem.java compiles; field/method signatures match legacy; sensors green

#### T-005: Harvest ShoppingCart model
**Class**: rewrite
**Findings**: javax-to-jakarta-import-00001 (recipe-executed)
**Goal**: Migrate ShoppingCart from legacy to Quarkus with exact behavior preservation
**Target design**: → `src/main/java/com/demo/model/ShoppingCart.java`
- **Owns**: src/main/java/com/redhat/coolstore/model/ShoppingCart.java
- **Package rename**: com.redhat.coolstore.model → com.demo.model
- **Field preservation**: cartId, cartItemTotal, cartItemPromoSavings, shippingTotal, shippingPromoSavings, cartTotal, shoppingCartItemList exactly as legacy
- **Method preservation**: all getters/setters, constructors, collection manipulation methods (add/remove/reset) exactly as legacy
- **Serialization**: serialVersionUID = -1108043957592113528L preserved
**Acceptance**: ShoppingCart.java compiles; field/method signatures match legacy; sensors green

#### T-006: Characterize Product model behavior
**Class**: infer
**Findings**: none
**Goal**: Validate Product model preserves legacy behavior through characterization tests
**Target design**: → `src/test/java/com/demo/model/ProductModelTest.java`
- **Behavior pins**: Product field values exactly as legacy (itemId, name, desc, price)
- **Constructor validation**: default and parameterized constructors work as legacy
- **Serialization compatibility**: serialVersionUID preserved for backward compatibility
- **Method contract**: getters/setters return/set values exactly as legacy
**Acceptance**: All characterization tests pass; legacy behavior verified; sensors green

#### T-007: Characterize ShoppingCartItem model behavior
**Class**: infer  
**Findings**: none
**Goal**: Validate ShoppingCartItem model preserves legacy behavior through characterization tests
**Target design**: → `src/test/java/com/demo/model/ShoppingCartItemModelTest.java`
- **Behavior pins**: ShoppingCartItem field values exactly as legacy (price, quantity, promoSavings, product)
- **Product reference**: ShoppingCartItem-Product association preserved as legacy
- **Constructor validation**: default constructor works as legacy
- **Serialization compatibility**: serialVersionUID preserved for backward compatibility
**Acceptance**: All characterization tests pass; legacy behavior verified; sensors green

#### T-008: Characterize ShoppingCart model behavior
**Class**: infer
**Findings**: none
**Goal**: Validate ShoppingCart model preserves legacy behavior through characterization tests
**Target design**: → `src/test/java/com/demo/model/ShoppingCartModelTest.java`
- **Behavior pins**: ShoppingCart field values exactly as legacy (cartId, totals, item list)
- **Collection operations**: addShoppingCartItem, removeShoppingCartItem, resetShoppingCartItemList work as legacy
- **Constructor validation**: default and parameterized constructors work as legacy  
- **Serialization compatibility**: serialVersionUID preserved for backward compatibility
**Acceptance**: All characterization tests pass; legacy behavior verified; sensors green

#### T-009: Verify CATALOG_ENDPOINT integration preservation
**Class**: infer
**Findings**: demo-env-integration-00001
**Goal**: Ensure CATALOG_ENDPOINT environment variable integration preserved for service layer
**Target design**: → `migration.yaml`
- **Configuration verification**: CATALOG_ENDPOINT already preserved in migration.yaml preserve list (line 42)
- **Service layer impact**: ShoppingCartService will use migrated model packages with CATALOG_ENDPOINT config in S03
- **Integration validation**: Verify CATALOG_ENDPOINT usage in legacy PromoService.java:25 and CatalogService.java:10
- **Documentation**: Add comment to migration.yaml explaining CATALOG_ENDPOINT preservation for service layer
**Acceptance**: CATALOG_ENDPOINT preservation verified in migration.yaml; integration documented; sensors green

#### T-010: Verify model compilation and build compatibility
|**Class**: infer
|**Findings**: none
|**Goal**: Ensure all migrated models compile correctly with Quarkus platform
|**Target design**: 
- **Build verification**: `pom.xml` compatibility with migrated model packages
- **Import updates**: ShoppingCartService, PromoService, ShippingService will update imports to migrated model packages in S03
- **Test compatibility**: ShoppingCartServiceTest can import and use migrated model classes
- **Package verification**: All model classes in com.demo.model package compile successfully
|**Acceptance**: `mvn clean test` passes; all model classes compile; no compilation errors; sensors green