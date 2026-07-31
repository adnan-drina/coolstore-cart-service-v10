# S05 Service Implementation Specification

## Legacy Behavior Analysis

ShoppingCartServiceImpl serves as the core business service orchestrating cart operations, pricing calculations, and external service integration. The service demonstrates the following observed behaviors from legacy analysis:

### Service Architecture
- **Package**: `com.redhat.coolstore.service`
- **Implementation**: `ShoppingCartServiceImpl.java` 
- **Interface**: `ShoppingCartService` (preserved unchanged)
- **Spring Stereotype**: `@Service` annotation for bean registration
- **Dependency Injection**: Field injection via `@Autowired` annotations
- **Initialization**: `@PostConstruct` method for cart storage setup

### Core Service Methods (Interface Contract)
The service implements all ShoppingCartService interface methods unchanged:

1. **`getShoppingCart(String cartId)`** - Retrieves or creates cart by ID
   - Creates new cart if ID doesn't exist
   - Automatically prices cart on retrieval
   - Returns fully initialized ShoppingCart object

2. **`getProduct(String itemId)`** - Retrieves product information
   - Fetches from catalog service when not cached
   - Caches products in memory using HashMap
   - Logs warnings for invalid product requests

3. **`deleteItem(String cartId, String itemId, int quantity)`** - Removes items from cart
   - Handles partial quantity removal
   - Removes entire item if quantity >= existing quantity
   - Reprices cart after modification

4. **`checkout(String cartId)`** - Empties cart contents
   - Resets cart item list
   - Reprices to zero state
   - Maintains cart object for continued use

5. **`addItem(String cartId, String itemId, int quantity)`** - Adds items to cart
   - Validates product existence before addition
   - Deduplicates items by product ID
   - Reprices cart after addition
   - Additive behavior: two add(cartId, itemId, 2) results in quantity 4

6. **`set(String cartId, String tmpId)`** - Transfers items between carts
   - Copies items from temporary cart to target cart
   - Deduplicates items after transfer
   - Reprices target cart

7. **`priceShoppingCart(ShoppingCart sc)`** - Calculates all cart totals
   - Initializes cart totals to zero
   - Applies item-level promotions
   - Calculates shipping costs
   - Applies shipping promotions
   - Derives final cart total

### Cart Storage Implementation
- **Storage**: HashMap<String, ShoppingCart> for in-memory cart persistence
- **Key**: cartId String
- **Value**: ShoppingCart object with all state
- **Initialization**: @PostConstruct creates HashMap instance
- **Thread Safety**: NOT thread-safe (concurrent access not protected)

### Product Caching Behavior
- **Cache**: HashMap<String, Product> for product information
- **Population**: Fetches from CatalogService.products() on cache miss
- **Strategy**: Complete cache refresh on each miss (no partial updates)
- **Warning**: Logs warning for invalid product IDs, returns null

### Pricing Logic Integration
- **Promotion Service**: ps.applyCartItemPromotions() and ps.applyShippingPromotions()
- **Shipping Service**: ss.calculateShipping() with tiered pricing
- **Total Derivation**: cartTotal = cartItemTotal + shippingTotal
- **Promotion Savings**: Tracks both item-level and shipping-level savings

### Deduplication Strategy
- **Method**: `dedupeCartItems()` merges items by product ID
- **Behavior**: Accumulates quantities for same product ID
- **Processing**: Creates new ShoppingCartItem with aggregated quantity
- **Integration**: Called after every addItem() and set() operation

### Error Handling Patterns
- **Product Validation**: Returns existing cart on invalid product, logs warning
- **Exception Safety**: removeShoppingCartItem() on pricing exceptions during add
- **Null Safety**: Guards against null shopping cart in priceShoppingCart()

## External Dependencies
- **ShippingService**: ss - shipping calculation logic
- **CatalogService**: catalogServie - product catalog integration
- **PromoService**: ps - promotional pricing logic

## Test Contract Validation
All behaviors validated through comprehensive test suite:

### Unit Test Contracts (ShoppingCartServiceTest.java)
- **Cart Initialization**: Empty cart returns zero totals (lines 32-35)
  - cartItemPromoSavings = 0.0
  - cartItemTotal = 0.0  
  - shippingPromoSavings = 0.0
  - cartTotal = 0.0

- **Cart Pricing**: Item total 2000.0, shipping promo savings -10.99, cart total 2000.0 (lines 49-53)
  - cartItemPromoSavings = 0.0
  - cartItemTotal = 2000.0
  - shippingPromoSavings = -10.99
  - cartTotal = 2000.0

- **Product Retrieval**: catalogService.products() integration and productMap caching

### Integration Test Contracts (CartServiceBoundaryTest.java)
- **REST API Integration**: End-to-end cart operations through CartEndpoint
- **Boundary Oracles**: 2000.0 item total, -10.99 shipping savings (lines 38-42)
- **Hoverfly Simulation**: Catalog service mocking for product data

## Legacy File Evidence
- `/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartServiceImpl.java` - Core implementation
- `/projects/legacy/src/main/java/com/redhat/coolstore/service/ShoppingCartService.java` - Service interface
- `/projects/legacy/src/test/java/com/redhat/coolstore/service/ShoppingCartServiceTest.java` - Unit tests
- `/projects/legacy/src/test/java/com/redhat/coolstore/CartServiceBoundaryTest.java` - Integration tests

## API Contract Summary
The service maintains a stable interface contract with external callers while managing internal cart state through:
- Cart CRUD operations (get, add, delete, checkout, set)
- Automatic pricing calculation
- Product caching and validation  
- Promotion and shipping integration
- Item deduplication
- Error handling with logging