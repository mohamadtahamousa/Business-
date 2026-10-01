package com.example

import com.example.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testProductMarginAndCostCalculation() {
        val product = Product(
            supplierId = 1,
            supplierName = "مصنع النور",
            name = "هودي أوفر سايز",
            category = "شتوي",
            sku = "HD-01",
            purchaseCost = 160.0,
            landedCostExtra = 15.0, // 175 total landed cost
            sellingPrice = 350.0,
            stockQuantity = 20
        )

        assertEquals(175.0, product.effectiveCostPerUnit, 0.001)
        assertEquals(175.0, product.profitPerUnit, 0.001)
        assertEquals(50.0, product.marginPercentage, 0.001)
    }
}
