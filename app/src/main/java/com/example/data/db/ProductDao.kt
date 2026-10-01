package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE supplierId = :supplierId ORDER BY name ASC")
    fun getProductsBySupplier(supplierId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id LIMIT 1")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE stockQuantity <= minStockAlert")
    fun getLowStockProducts(): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = stockQuantity - :qty, totalUnitsSold = totalUnitsSold + :qty WHERE id = :id AND stockQuantity >= :qty")
    suspend fun deductStock(id: Long, qty: Int): Int

    @Query("UPDATE products SET stockQuantity = stockQuantity + :qty, totalUnitsSold = totalUnitsSold - :qty WHERE id = :id")
    suspend fun returnStock(id: Long, qty: Int): Int
}
