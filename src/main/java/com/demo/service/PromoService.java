package com.demo.service;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import jakarta.enterprise.context.ApplicationScoped;

import com.demo.model.Promotion;
import com.demo.model.ShoppingCart;
import com.demo.model.ShoppingCartItem;

@ApplicationScoped
public class PromoService {

    private final ConcurrentHashMap<String, Promotion> promotionMap = new ConcurrentHashMap<>();

    public PromoService() {
        // Coolstore seed item also used by inventory/catalog demos
        promotionMap.put("329299", new Promotion("329299", .25));
    }

    public void applyCartItemPromotions(ShoppingCart shoppingCart) {
        if (shoppingCart != null && !shoppingCart.getShoppingCartItemList().isEmpty()) {
            for (ShoppingCartItem sci : shoppingCart.getShoppingCartItemList()) {
                String productId = sci.getProduct().getItemId();
                Promotion promo = promotionMap.get(productId);
                if (promo != null) {
                    sci.setPromoSavings(sci.getProduct().getPrice() * promo.getPercentOff() * -1);
                    sci.setPrice(sci.getProduct().getPrice() * (1 - promo.getPercentOff()));
                }
            }
        }
    }

    public void applyShippingPromotions(ShoppingCart shoppingCart) {
        // PROMO: if cart total is greater than 75, free shipping
        if (shoppingCart != null && shoppingCart.getCartItemTotal() >= 75) {
            shoppingCart.setShippingPromoSavings(shoppingCart.getShippingTotal() * -1);
            shoppingCart.setShippingTotal(0);
        }
    }

    public Set<Promotion> getPromotions() {
        return new HashSet<>(promotionMap.values());
    }

    public void setPromotions(Set<Promotion> promotionSet) {
        promotionMap.clear();
        if (promotionSet != null) {
            for (Promotion p : promotionSet) {
                promotionMap.put(p.getItemId(), p);
            }
        }
    }

    @Override
    public String toString() {
        return "PromoService [promotionSet=" + getPromotions() + "]";
    }
}
