package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.model.Order
import com.example.data.model.OrderItem
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id LIMIT 1")
    suspend fun getOrderById(id: Long): Order?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    // Order Items
    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    fun getOrderItemsByOrderId(orderId: Long): Flow<List<OrderItem>>

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsSync(orderId: Long): List<OrderItem>

    @Query("SELECT * FROM order_items")
    fun getAllOrderItems(): Flow<List<OrderItem>>

    @Query("SELECT * FROM order_items")
    suspend fun getAllOrderItemsSync(): List<OrderItem>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(item: OrderItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItem>)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItemsByOrderId(orderId: Long)

    @Transaction
    suspend fun insertFullOrder(order: Order, items: List<OrderItem>): Long {
        val orderId = insertOrder(order)
        val itemsWithId = items.map { it.copy(orderId = orderId) }
        insertOrderItems(itemsWithId)
        return orderId
    }

    @Query("UPDATE orders SET status = :status, isCollected = :isCollected WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Long, status: String, isCollected: Boolean)
}
