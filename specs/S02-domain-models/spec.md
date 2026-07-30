# S02 Domain Model Harvest - Specification

## Legacy Behavior & API Contract

This story harvests the core domain models with their exact legacy behavior preserved. Following the dependency order (dependency-order.md lines 18-24), Product, Promotion, ShoppingCartItem, and ShoppingCart are the god nodes with highest fan-in that must be stabilized before dependent services. These HARVEST classes preserve all legacy behavior and provide the foundation for service layer modernization in S03.

### In-Scope Legacy Files

**`src/main/java/com/redhat/coolstore/model/Product.java`** - Product domain model

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
    
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDesc() {
        return desc;
    }
    public void setDesc(String desc) {
        this.desc = desc;
    }
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return "Product [itemId=" + itemId + ", name=" + name + ", desc="
                + desc + ", price=" + price + "]";
    }
}
```

**`src/main/java/com/redhat/coolstore/model/Promotion.java`** - Promotion domain model

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
    
    public String getItemId() {
        return itemId;
    }
    public void setItemId(String itemId) {
        this.itemId = itemId;
    }
    public double getPercentOff() {
        return percentOff;
    }
    public void setPercentOff(double percentOff) {
        this.percentOff = percentOff;
    }
    
    @Override
    public String toString() {
        return "Promotion [itemId=" + itemId + ", percentOff=" + percentOff
                + "]";
    }
}
```

**`src/main/java/com/redhat/coolstore/model/ShoppingCartItem.java`** - Cart item domain model

```java
package com.redhat.coolstore.model;

import java.io.Serializable;

public class ShoppingCartItem implements Serializable {
    
    private static final long serialVersionUID = 6964558044240061049L;
    private double price;
    private int quantity;
    private double promoSavings;
    private Product product;
    
    public ShoppingCartItem() {
        
    }
    
    public double getPrice() {
        return price;
    }
    public void setPrice(double price) {
        this.price = price;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public int getQuantity() {
        return quantity;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public double getPromoSavings() {
        return promoSavings;
    }
    public void setPromoSavings(double promoSavings) {
        this.promoSavings = promoSavings;
    }
    
    @Override
    public String toString() {
        return "ShoppingCartItem [price=" + price + ", quantity=" + quantity
                + ", promoSavings=" + promoSavings + ", product=" + product
                + "]";
    }
}
```

**`src/main/java/com/redhat/coolstore/model/ShoppingCart.java`** - Shopping cart domain model

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
    
    public ShoppingCart() {
    }
    
    public ShoppingCart(String cartId) {
        this.cartId = cartId;
    }
    
    public String getCartId() {
        return cartId;
    }
    public void setCartId(String cartId) {
        this.cartId = cartId;
    }
    public List<ShoppingCartItem> getShoppingCartItemList() {
        return shoppingCartItemList;
    }
    public void setShoppingCartItemList(List<ShoppingCartItem> shoppingCartItemList) {
        this.shoppingCartItemList = shoppingCartItemList;
    }
    public void resetShoppingCartItemList() {
        shoppingCartItemList = new ArrayList<ShoppingCartItem>();
    }
    public void addShoppingCartItem(ShoppingCartItem sci) {
        if ( sci != null ) {
            shoppingCartItemList.add(sci);
        }
    }
    public boolean removeShoppingCartItem(ShoppingCartItem sci) {
        boolean removed = false;
        if ( sci != null ) {
            removed = shoppingCartItemList.remove(sci);
        }
        return removed;
    }
    public double getCartItemTotal() {
        return cartItemTotal;
    }
    public void setCartItemTotal(double cartItemTotal) {
        this.cartItemTotal = cartItemTotal;
    }
    public double getShippingTotal() {
        return shippingTotal;
    }
    public void setShippingTotal(double shippingTotal) {
        this.shippingTotal = shippingTotal;
    }
    public double getCartTotal() {
        return cartTotal;
    }
    public void setCartTotal(double cartTotal) {
        this.cartTotal = cartTotal;
    }
    public double getCartItemPromoSavings() {
        return cartItemPromoSavings;
    }
    public void setCartItemPromoSavings(double cartItemPromoSavings) {
        this.cartItemPromoSavings = cartItemPromoSavings;
    }
    public double getShippingPromoSavings() {
        return shippingPromoSavings;
    }
    public void setShippingPromoSavings(double shippingPromoSavings) {
        this.shippingPromoSavings = shippingPromoSavings;
    }
    
    @Override
    public String toString() {
        return "ShoppingCart [cartId=" + cartId
                + ", cartItemTotal=" + cartItemTotal
                + ", cartItemPromoSavings=" + cartItemPromoSavings
                + ", shippingTotal=" + shippingTotal
                + ", shippingPromoSavings=" + shippingPromoSavings
                + ", cartTotal=" + cartTotal + ", shoppingCartItemList="
                + shoppingCartItemList + "]";
    }
}
```

### Behavioral Contract

The domain models must preserve all legacy behavior exactly:

- **Product field preservation**: itemId, name, desc, price fields exactly as legacy
- **ShoppingCart field preservation**: cartId, cartItemTotal, cartItemPromoSavings, shippingTotal, shippingPromoSavings, cartTotal, shoppingCartItemList
- **ShoppingCartItem field preservation**: price, quantity, promoSavings, product reference
- **Promotion field preservation**: itemId, percentOff fields exactly as legacy
- **Serialization compatibility**: serialVersionUID fields preserved for backward compatibility
- **Constructor behavior**: default and parameterized constructors preserve legacy behavior
- **Collection behavior**: ShoppingCart item list manipulation (add/remove/reset) preserved exactly

### Preserved Configuration

- **Package namespace**: All model packages migrate from `com.redhat.coolstore.model` to `com.demo.model`
- **Serialization compatibility**: All serialVersionUID values preserved exactly
- **Field types**: All field types maintained for test compatibility
- **CATALOG_ENDPOINT**: Environment variable for external catalog service integration preserved (demo-env-integration-00001)

### Integration Surfaces

- **Jackson JSON serialization**: Models used in REST API JSON marshalling
- **Service layer integration**: Models referenced by ShoppingCartService, PromoService, ShippingService
- **Catalog service integration**: Product models used with CatalogService Feign client
- **Test framework compatibility**: Models used in ShoppingCartServiceTest assertions

### Contracts to Preserve

1. **javax-to-jakarta-import-00001**: javax.* imports → jakarta.* imports for model classes (recipe-executed)
2. **Package rename**: com.redhat.coolstore → com.demo applied to model packages
3. **Field name compatibility**: All legacy field names preserved for test assertions
4. **Method signature compatibility**: All getter/setter method signatures match legacy exactly
5. **Serialization compatibility**: serialVersionUID values and Serializable interface preserved

### Out of Scope

Service classes (ShoppingCartService, PromoService, ShippingService, CatalogService) remain unchanged in Spring configuration. REST endpoints (CartEndpoint) continue using Spring annotations until S04. The application bootstrap (CartServiceApplication) remains Spring Boot until S06.

**Legacy UI surface coverage**: **WAIVED** - The cart service is a pure REST API backend service. The REST endpoints `/api/cart/*` (CartEndpoint.java:21-23) provide the interface for web and mobile clients but are covered in service layer story (S03/S04). Domain model story S02 focuses on model migration with exact behavior preservation, maintaining REST API contracts through domain model consistency.

### Evidence Sources

- Legacy model analysis from `/projects/legacy/src/main/java/com/redhat/coolstore/model/`
- MTA findings for javax-to-jakarta import transformation
- Migration architecture profile §7 target contract for HARVEST classes
- Dependency order analysis identifying god nodes requiring early stabilization
- ShoppingCartServiceTest behavioral assertions for contract validation