package com.demo.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PromotionModelTest {

    @Test
    void testDefaultConstructor() {
        Promotion promotion = new Promotion();
        assertNull(promotion.getItemId());
        assertEquals(0.0, promotion.getPercentOff(), 0.01);
    }

    @Test
    void testParameterizedConstructor() {
        Promotion promotion = new Promotion("test-item-123", 15.5);
        assertEquals("test-item-123", promotion.getItemId());
        assertEquals(15.5, promotion.getPercentOff(), 0.01);
    }

    @Test
    void testSettersAndGetters() {
        Promotion promotion = new Promotion();
        promotion.setItemId("new-item-456");
        promotion.setPercentOff(25.0);
        
        assertEquals("new-item-456", promotion.getItemId());
        assertEquals(25.0, promotion.getPercentOff(), 0.01);
    }

    @Test
    void testToString() {
        Promotion promotion = new Promotion("item-789", 10.0);
        String result = promotion.toString();
        
        assertTrue(result.contains("item-789"));
        assertTrue(result.contains("10.0"));
    }

    @Test
    void testZeroPercentOff() {
        Promotion promotion = new Promotion("item-zero", 0.0);
        assertEquals(0.0, promotion.getPercentOff(), 0.01);
    }

    @Test
    void testNegativePercentOff() {
        Promotion promotion = new Promotion("item-negative", -5.0);
        assertEquals(-5.0, promotion.getPercentOff(), 0.01);
    }

    @Test
    void testEmptyItemId() {
        Promotion promotion = new Promotion("", 20.0);
        assertEquals("", promotion.getItemId());
        assertEquals(20.0, promotion.getPercentOff(), 0.01);
    }
}