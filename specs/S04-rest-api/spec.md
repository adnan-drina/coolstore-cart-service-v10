# S04: REST API modernization specification

## Overview

This story modernizes the REST API layer from Spring Boot @RestController to native JAX-RS with Quarkus session management. The legacy implementation uses Spring's session scoping and manual JAX-RS resource registration, which will be replaced with Quarkus-native equivalents.

## Legacy API contract

### CartEndpoint REST interface

**Location**: `/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java`

**Package**: `com.redhat.coolstore.rest`

**Session scope**: `@Scope(scopeName = WebApplicationContext.SCOPE_SESSION)` maintains cart state across HTTP requests within user sessions.

**Endpoint matrix**:

| Method | Path | Behavior | Response |
|--------|------|----------|----------|
| GET | `/cart/{cartId}` | Retrieve cart by ID | ShoppingCart JSON |
| POST | `/cart/{cartId}/{itemId}/{quantity}` | Add/update item quantity | ShoppingCart JSON |
| POST | `/cart/{cartId}/{tmpId}` | Set cart from template | ShoppingCart JSON |
| DELETE | `/cart/{cartId}/{itemId}/{quantity}` | Remove item quantity | ShoppingCart JSON |
| POST | `/cart/checkout/{cartId}` | Process cart checkout | ShoppingCart JSON |

**Media types**: All endpoints produce and consume `application/json`.

**Session management**: Cart state maintained across requests via Spring WebApplicationContext.SCOPE_SESSION (line 22).

**Service integration**: Uses @Autowired ShoppingCartService field injection (lines 28-29) with legacy service method signatures preserved.

### JerseyConfig configuration

**Location**: `/projects/legacy/src/main/java/com/redhat/coolstore/rest/JerseyConfig.java`

**Purpose**: Manual JAX-RS resource registration extending ResourceConfig (line 7).

**Registration**: Explicitly registers CartEndpoint.class in constructor (line 9).

**Spring integration**: Annotated with @Component for Spring bean management (line 6).

## Behavioral contracts

### Cart operations
- **GET /cart/{cartId}**: Returns ShoppingCart JSON, delegates to ShoppingCartService.getShoppingCart()
- **POST /cart/{cartId}/{itemId}/{quantity}**: Adds/updates items, delegates to ShoppingCartService.addItem()
- **POST /cart/{cartId}/{tmpId}**: Cart template operation, delegates to ShoppingCartService.set()
- **DELETE /cart/{cartId}/{itemId}/{quantity}**: Removes items, delegates to ShoppingCartService.deleteItem()
- **POST /cart/checkout/{cartId}**: Processes checkout, delegates to ShoppingCartService.checkout()

### Session state preservation
Cart state must persist across HTTP requests within user sessions. The session scope ensures cart data remains consistent for the same cartId across multiple operations.

### Service contracts
All endpoints maintain exact legacy service method signatures:
- `ShoppingCart getShoppingCart(String cartId)`
- `ShoppingCart addItem(String cartId, String itemId, int quantity) throws Exception`
- `ShoppingCart set(String cartId, String tmpId) throws Exception`
- `ShoppingCart deleteItem(String cartId, String itemId, int quantity) throws Exception`
- `ShoppingCart checkout(String cartId)`

## Integration surfaces

### Environment configuration
- **CATALOG_ENDPOINT**: Environment variable for catalog service URL (inherited from service layer)
- Used by ShoppingCartService for product catalog integration

### Session management mechanism
Current: Spring WebApplicationContext.SCOPE_SESSION  
Target: Quarkus session management equivalent preserving cart state persistence

### JAX-RS configuration
Current: Manual resource registration via JerseyConfig  
Target: Quarkus auto-discovery eliminating JerseyConfig

## Package mapping

**Legacy package**: `com.redhat.coolstore`  
**Target package**: `com.demo` (full prefix replacement)

**Affected classes**:
- `com.redhat.coolstore.rest.CartEndpoint` → `com.demo.rest.CartEndpoint`
- `com.redhat.coolstore.rest.JerseyConfig` → `com.demo.rest.JerseyConfig`

## Evidence sources

- Legacy endpoint behavior: `/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java:31-69`
- Session scope definition: `/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java:22`
- Service integration: `/projects/legacy/src/main/java/com/redhat/coolstore/rest/CartEndpoint.java:28-29`
- Jersey configuration: `/projects/legacy/src/main/java/com/redhat/coolstore/rest/JerseyConfig.java:8-9`
- Boundary test expectations: CartServiceBoundaryTest.java (as referenced in brief)
