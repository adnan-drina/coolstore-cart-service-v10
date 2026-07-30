# S03 Services Specification

## Legacy Behavior & API Contract

This specification documents the observed behavior of the service layer interfaces and core business services in the legacy Spring Boot application, providing the authoritative contract that the Quarkus migration must preserve.

### Service Interface Contract

**ShoppingCartService Interface** (`src/main/java/com/redhat/coolstore/service/ShoppingCartService.java`)

The interface defines seven core cart operations with specific method signatures and contracts:

- `ShoppingCart getShoppingCart(String cartId)` - Retrieves cart by ID, returns null if cart doesn't exist
- `Product getProduct(String itemId)` - Retrieves product from catalog service by item ID  
- `ShoppingCart deleteItem(String cartId, String itemId, int quantity)` - Removes specified quantity from cart item
- `ShoppingCart checkout(String cartId)` - Processes cart checkout with pricing and shipping calculations
- `ShoppingCart addItem(String cartId, String itemId, int quantity)` - Adds items to cart with catalog lookup
- `ShoppingCart set(String cartId, String tmpId)` - Sets cart to match another cart's state
- `void priceShoppingCart(ShoppingCart sc)` - Calculates totals including shipping and promotion discounts

### Business Service Behavior

**PromoService** (`src/main/java/com/redhat/coolstore/service/PromoService.java`)

Promotion calculation service implementing item-level and shipping promotions:

- **Item Promotions**: Applies percentage discounts to specific products by mapping product IDs to Promotion rules
  - Product "329299" receives 25% discount (line 27)
  - Promotions stored in HashSet<Promotion> with itemId and percentOff fields
  - applyCartItemPromotions() method traverses cart items and applies discount to price, sets promoSavings as negative value

- **Shipping Promotions**: Free shipping when cart total >= $75
  - applyShippingPromotions() sets shippingPromoSavings to negative shippingTotal amount
  - Sets shippingTotal to 0.0 when free shipping threshold met

- **Data Management**: 
  - getPromotions() returns copy of promotion set (thread-safety concern with direct access)
  - setPromotions() replaces entire promotion set

**ShippingService** (`src/main/java/com/redhat/coolstore/service/ShippingService.java`)

Shipping calculation service with tier-based pricing structure:

- **Tier Structure**:
  - $0.00 - $24.99: $2.99 shipping
  - $25.00 - $49.99: $4.99 shipping  
  - $50.00 - $74.99: $6.99 shipping
  - $75.00 - $99.99: $8.99 shipping
  - $100.00 - $9999.99: $10.99 shipping

- **Business Rule**: calculateShipping() only processes non-null carts, otherwise leaves cart state unchanged

**CatalogService** (`src/main/java/com/redhat/coolstore/service/CatalogService.java`)

Feign client interface for product catalog integration:

- **Endpoint Configuration**: Uses ${CATALOG_ENDPOINT} environment variable for base URL
- **API Contract**: GET /api/products returns List<Product>
- **Service Integration**: OpenFeign client with @FeignClient annotation
- **Error Handling**: No explicit error handling in interface, relies on Feign exception propagation

### Integration Surfaces

**CATALOG_ENDPOINT Configuration** (`src/main/resources/application.properties:6`)

Environment-driven configuration for external catalog service:

- Environment variable convention (ALL_CAPS) preserved per demo-env-integration-00001
- Feign client URL resolution via property placeholder
- Used by CatalogService for all product retrieval calls
- Supports both direct environment variable and default value substitution

**Spring Component Stereotypes**

All services use @Component annotation:
- PromoService.java:15 - @Component with Serializable implementation
- ShippingService.java:7 - @Component
- ShoppingCartServiceImpl.java:28 - @Component with @Autowired field injection patterns

### Data Flow Contracts

**Cart Pricing Pipeline**: ShoppingCartService.priceShoppingCart() orchestrates:
1. CatalogService.getProduct() for each cart item
2. PromoService.applyCartItemPromotions() for item-level discounts  
3. ShippingService.calculateShipping() for shipping cost calculation
4. PromoService.applyShippingPromotions() for free shipping logic

**Thread Safety Concerns**: Legacy implementation uses:
- HashSet for promotion storage (not thread-safe for concurrent modification)
- No explicit synchronization in calculation methods
- Potential race conditions on promotion data updates

### Side Effects & State Management

**Promotion Service State**:
- Initializes default promotion for product "329299" on construction
- Maintains mutable promotionSet field accessible via getters/setters
- No bounded refresh or cache invalidation strategy

**Cart Service State**:  
- ShoppingCartServiceImpl uses HashMap<String, ShoppingCart> for cart storage
- No session expiration or cleanup mechanism
- Product cache using Map<String, Product> with no invalidation policy

### Exception Handling Patterns

- **CatalogService**: No explicit exception handling, relies on Feign client defaults
- **ShoppingCartServiceImpl**: Logs warnings for invalid product IDs, continues processing
- **No global exception mapping**: No @ControllerAdvice or equivalent error handling strategy

### Legacy File References

- Service contracts: `/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartService.java`
- Promotion logic: `/projects/legacy/src/main/java/com/redhat/coolstore/service/PromoService.java:27,50-54`
- Shipping calculations: `/projects/legacy/src/main/java/com/redhat/coolstore/service/ShippingService.java:10-24`
- Catalog integration: `/projects/legacy/src/main/java/com/redhat/coolstore/service/CatalogService.java:10`
- Configuration surface: `/projects/legacy/src/main/resources/application.properties:6`

### Preservation Requirements

The migration must preserve:
1. **Method Signatures**: All ShoppingCartService interface methods unchanged
2. **Promotion Logic**: 25% discount on product "329299" exact value preserved
3. **Shipping Tiers**: All five shipping cost tiers with exact dollar amounts preserved  
4. **Environment Configuration**: CATALOG_ENDPOINT variable functionality maintained
5. **Integration Contract**: GET /api/products returns List<Product> contract preserved
6. **Thread Safety**: Concurrent access patterns improved without changing business logic
