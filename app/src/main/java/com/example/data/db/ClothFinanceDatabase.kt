package com.example.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.model.Expense
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.Product
import com.example.data.model.Supplier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [
        Supplier::class,
        Product::class,
        Order::class,
        OrderItem::class,
        Expense::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ClothFinanceDatabase : RoomDatabase() {

    abstract fun supplierDao(): SupplierDao
    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun expenseDao(): ExpenseDao

    companion object {
        @Volatile
        private var INSTANCE: ClothFinanceDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): ClothFinanceDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ClothFinanceDatabase::class.java,
                    "cloth_finance_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    populateInitialData(database)
                }
            }
        }

        private suspend fun populateInitialData(db: ClothFinanceDatabase) {
            val supplierDao = db.supplierDao()
            val productDao = db.productDao()
            val orderDao = db.orderDao()
            val expenseDao = db.expenseDao()

            // 1. الموردين (مصانع ومكاتب جملة)
            val s1Id = supplierDao.insertSupplier(
                Supplier(
                    name = "مصنع النور للتريكو والميلتون (المحلة)",
                    type = "مصنع ملابس",
                    phone = "01098765432",
                    city = "المحلة الكبرى",
                    paymentTerms = "كاش بنسبة خصم 5%",
                    notes = "أعلى جودة قطن، دقة ممتازة في المقاسات ومعدل مرتجعات منخفض جداً"
                )
            )

            val s2Id = supplierDao.insertSupplier(
                Supplier(
                    name = "مكتب الأهرام لتجارة الجملة (العتبة)",
                    type = "مكتب جملة",
                    phone = "01123456789",
                    city = "القاهرة - العتبة",
                    paymentTerms = "آجل 15 يوم",
                    notes = "موديلات سريعة التغيير، أسعار تنافسية، تسليم فوري بالدستة"
                )
            )

            val s3Id = supplierDao.insertSupplier(
                Supplier(
                    name = "مصنع الدلتا للووتربروف والبامب (العاشر)",
                    type = "مصنع ملابس",
                    phone = "01234567890",
                    city = "العاشر من رمضان",
                    paymentTerms = "دفعة 50% والباقي عند الاستلام",
                    notes = "متخصص في الجواكت الشتوية والخامات العازلة للمياه، هوامش ربح ممتازة"
                )
            )

            val s4Id = supplierDao.insertSupplier(
                Supplier(
                    name = "مكتب الفيروز للاستيراد والجملة (بورسعيد)",
                    type = "مستورد جملة",
                    phone = "01543216789",
                    city = "بورسعيد",
                    paymentTerms = "كاش",
                    notes = "خامات مستوردة وبليزرات كاجوال، ينصح بمراجعة جدول المقاسات مع العميل لتقليل المرتجع"
                )
            )

            // 2. المنتجات
            val p1Id = productDao.insertProduct(
                Product(
                    supplierId = s1Id,
                    supplierName = "مصنع النور للتريكو والميلتون",
                    name = "هودي شتوي مبطن أوفر سايز 100% قطن",
                    category = "شتوي / كاجوال",
                    sku = "HD-NOOR-01",
                    purchaseCost = 160.0,
                    landedCostExtra = 10.0, // كارتون وتغليف خاص
                    sellingPrice = 380.0,
                    stockQuantity = 45,
                    minStockAlert = 10,
                    sizes = "M, L, XL, 2XL",
                    colors = "أسود, زيتي, بيج ميلانج",
                    totalUnitsSold = 28
                )
            )

            val p2Id = productDao.insertProduct(
                Product(
                    supplierId = s1Id,
                    supplierName = "مصنع النور للتريكو والميلتون",
                    name = "سويت شيرت راوند مطرز قطن تقيل",
                    category = "شبابي / كاجوال",
                    sku = "SW-NOOR-02",
                    purchaseCost = 125.0,
                    landedCostExtra = 8.0,
                    sellingPrice = 295.0,
                    stockQuantity = 32,
                    minStockAlert = 8,
                    sizes = "L, XL, 2XL",
                    colors = "كحلي, عنابي, أسود",
                    totalUnitsSold = 19
                )
            )

            val p3Id = productDao.insertProduct(
                Product(
                    supplierId = s2Id,
                    supplierName = "مكتب الأهرام لتجارة الجملة",
                    name = "بنطلون جينز وايد ليج كاجوال ستريت",
                    category = "جينز / كاجوال",
                    sku = "JN-AHRAM-01",
                    purchaseCost = 145.0,
                    landedCostExtra = 5.0,
                    sellingPrice = 320.0,
                    stockQuantity = 22,
                    minStockAlert = 10,
                    sizes = "30, 32, 34, 36, 38",
                    colors = "أزرق فاتح, أزرق كلاسيك, رمادي",
                    totalUnitsSold = 34
                )
            )

            val p4Id = productDao.insertProduct(
                Product(
                    supplierId = s3Id,
                    supplierName = "مصنع الدلتا للووتربروف والبامب",
                    name = "جاكت بامب دبل وتربروف كابيشون قابل للفصل",
                    category = "جواكت شتوية",
                    sku = "JK-DELTA-01",
                    purchaseCost = 280.0,
                    landedCostExtra = 15.0,
                    sellingPrice = 640.0,
                    stockQuantity = 18,
                    minStockAlert = 5,
                    sizes = "M, L, XL, 2XL",
                    colors = "أسود فاحم, كحلي غامق, بترولي",
                    totalUnitsSold = 16
                )
            )

            val p5Id = productDao.insertProduct(
                Product(
                    supplierId = s4Id,
                    supplierName = "مكتب الفيروز للاستيراد والجملة",
                    name = "بليزر كاجوال سيمي فورمال قماش ليكرا مستورد",
                    category = "سيمي فورمال",
                    sku = "BL-FAYROUZ-01",
                    purchaseCost = 230.0,
                    landedCostExtra = 15.0,
                    sellingPrice = 490.0,
                    stockQuantity = 12,
                    minStockAlert = 6,
                    sizes = "48, 50, 52, 54",
                    colors = "رمادي كاروهات, كحلي, جملي",
                    totalUnitsSold = 14
                )
            )

            // 3. الطلبات والمبيعات
            val o1Id = orderDao.insertOrder(
                Order(
                    orderNumber = "ORD-2001",
                    customerName = "أحمد محمود كمال",
                    customerPhone = "01011223344",
                    customerCity = "القاهرة - التجمع الخامس",
                    customerAddress = "حي النرجس عمارة 42",
                    shippingCompany = "بوسطة Bosta",
                    shippingFeeToCustomer = 50.0,
                    actualShippingCost = 45.0,
                    status = "DELIVERED",
                    isCollected = true,
                    totalAmount = 675.0,
                    totalCogs = 295.0,
                    notes = "تم الاستلام مع إشادة بجودة خامة الهودي",
                    createdAt = System.currentTimeMillis() - (86400000L * 4)
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItem(orderId = o1Id, productId = p1Id, productName = "هودي شتوي مبطن أوفر سايز 100% قطن", supplierId = s1Id, supplierName = "مصنع النور للتريكو", quantity = 1, unitCost = 170.0, unitPrice = 380.0, selectedSize = "XL", selectedColor = "أسود"),
                    OrderItem(orderId = o1Id, productId = p2Id, productName = "سويت شيرت راوند مطرز قطن تقيل", supplierId = s1Id, supplierName = "مصنع النور للتريكو", quantity = 1, unitCost = 133.0, unitPrice = 295.0, selectedSize = "L", selectedColor = "كحلي")
                )
            )

            val o2Id = orderDao.insertOrder(
                Order(
                    orderNumber = "ORD-2002",
                    customerName = "كريم سامي عبد الفتاح",
                    customerPhone = "01199887766",
                    customerCity = "الجيزة - الشيخ زايد",
                    customerAddress = "كمبوند بيفرلي هيلز",
                    shippingCompany = "ريد بوكس RedBox",
                    shippingFeeToCustomer = 55.0,
                    actualShippingCost = 50.0,
                    status = "DELIVERED",
                    isCollected = true,
                    totalAmount = 640.0,
                    totalCogs = 295.0,
                    notes = "تحصيل كاش تم توريده للفيزا",
                    createdAt = System.currentTimeMillis() - (86400000L * 3)
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItem(orderId = o2Id, productId = p4Id, productName = "جاكت بامب دبل وتربروف كابيشون", supplierId = s3Id, supplierName = "مصنع الدلتا للووتربروف", quantity = 1, unitCost = 295.0, unitPrice = 640.0, selectedSize = "XL", selectedColor = "كحلي غامق")
                )
            )

            val o3Id = orderDao.insertOrder(
                Order(
                    orderNumber = "ORD-2003",
                    customerName = "محمد طارق إبراهيم",
                    customerPhone = "01233445566",
                    customerCity = "الإسكندرية - سموحة",
                    customerAddress = "شارع فوزي معاذ",
                    shippingCompany = "بوسطة Bosta",
                    shippingFeeToCustomer = 60.0,
                    actualShippingCost = 55.0,
                    status = "SHIPPED",
                    isCollected = false, // لم تورد بعد
                    totalAmount = 700.0,
                    totalCogs = 320.0,
                    notes = "مع مندوب الشحن جاري التسليم اليوم",
                    createdAt = System.currentTimeMillis() - (86400000L * 1)
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItem(orderId = o3Id, productId = p1Id, productName = "هودي شتوي مبطن أوفر سايز 100% قطن", supplierId = s1Id, supplierName = "مصنع النور للتريكو", quantity = 1, unitCost = 170.0, unitPrice = 380.0, selectedSize = "2XL", selectedColor = "بيج ميلانج"),
                    OrderItem(orderId = o3Id, productId = p3Id, productName = "بنطلون جينز وايد ليج كاجوال", supplierId = s2Id, supplierName = "مكتب الأهرام للجملة", quantity = 1, unitCost = 150.0, unitPrice = 320.0, selectedSize = "34", selectedColor = "أزرق فاتح")
                )
            )

            val o4Id = orderDao.insertOrder(
                Order(
                    orderNumber = "ORD-2004",
                    customerName = "يوسف عادل شريف",
                    customerPhone = "01555667788",
                    customerCity = "المنصورة - المشاية",
                    customerAddress = "شارع المشاية السفلية",
                    shippingCompany = "بوسطة Bosta",
                    shippingFeeToCustomer = 60.0,
                    actualShippingCost = 60.0,
                    status = "RETURNED",
                    isCollected = false,
                    totalAmount = 490.0,
                    totalCogs = 245.0,
                    notes = "مرتجع بسبب اختيار العميل مقاس ضيق من البليزر - تم استرجاع القطعة للمخزن ودفع تكلفة الشحن",
                    createdAt = System.currentTimeMillis() - (86400000L * 2)
                )
            )
            orderDao.insertOrderItems(
                listOf(
                    OrderItem(orderId = o4Id, productId = p5Id, productName = "بليزر كاجوال سيمي فورمال ليكرا", supplierId = s4Id, supplierName = "مكتب الفيروز للاستيراد", quantity = 1, unitCost = 245.0, unitPrice = 490.0, selectedSize = "50", selectedColor = "رمادي كاروهات")
                )
            )

            // 4. المصروفات التشغيلية والتسويقية
            expenseDao.insertExpense(
                Expense(
                    title = "إعلانات ممولة فيسبوك وانستجرام (حملة الشتاء المميزة)",
                    category = "إعلانات ممولة",
                    amount = 1450.0,
                    date = System.currentTimeMillis() - (86400000L * 5),
                    notes = "حملة استهداف لجمهور الملابس الشتوية والشبابية"
                )
            )
            expenseDao.insertExpense(
                Expense(
                    title = "أكياس شحن بابلز سميكة + ستيكر شكر وهدايا لوجو (400 قطعة)",
                    category = "أكياس وتغليف",
                    amount = 380.0,
                    date = System.currentTimeMillis() - (86400000L * 7),
                    notes = "شراء بالجملة من منطقة باب الشعرية"
                )
            )
            expenseDao.insertExpense(
                Expense(
                    title = "خسارة شحن مرتجعات معلقة (شحنتين لم يستلمهما العميل)",
                    category = "شحن مرتجعات",
                    amount = 110.0,
                    date = System.currentTimeMillis() - (86400000L * 2),
                    notes = "رسوم شحن رايح جاي تتحملها المنظومة لعدم رد العميل"
                )
            )
            expenseDao.insertExpense(
                Expense(
                    title = "اشتراك أداة إدارة الرسائل ومنصة الرد السريع",
                    category = "برمجيات وأدوات",
                    amount = 190.0,
                    date = System.currentTimeMillis() - (86400000L * 10),
                    notes = "اشتراك شهري لأتمتة الرد على استفسارات الطلبات"
                )
            )
        }
    }
}
