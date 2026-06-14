package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface JewelryDao {

    // --- Customer Queries ---
    @Query("SELECT * FROM customers ORDER BY name ASC")
    fun getAllCustomers(): Flow<List<Customer>>

    @Query("SELECT * FROM customers WHERE id = :id LIMIT 1")
    suspend fun getCustomerById(id: Long): Customer?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Delete
    suspend fun deleteCustomer(customer: Customer)


    // --- Inventory Queries ---
    @Query("SELECT * FROM inventory_items ORDER BY createdAt DESC")
    fun getAllInventoryItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE id = :id LIMIT 1")
    suspend fun getInventoryItemById(id: Long): InventoryItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInventoryItem(item: InventoryItem): Long

    @Update
    suspend fun updateInventoryItem(item: InventoryItem)

    @Delete
    suspend fun deleteInventoryItem(item: InventoryItem)


    // --- Transaction Queries ---
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE customerId = :customerId ORDER BY date DESC")
    fun getTransactionsByCustomerId(customerId: Long): Flow<List<Transaction>>

    @Query("SELECT SUM(amountBdt) FROM transactions WHERE transactionType = 'Sale' AND date >= :startOfDay")
    suspend fun getDailyRevenue(startOfDay: Long): Double?

    @Query("SELECT COUNT(*) FROM transactions WHERE transactionType = 'Sale' AND date >= :startOfDay")
    suspend fun getDailyItemsSold(startOfDay: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: Transaction): Long

    @Update
    suspend fun updateTransaction(transaction: Transaction)

    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    // --- Branch Queries ---
    @Query("SELECT * FROM branches ORDER BY branchName ASC")
    fun getAllBranches(): Flow<List<Branch>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBranch(branch: Branch): Long

    @Delete
    suspend fun deleteBranch(branch: Branch)

    // --- Supplier Queries ---
    @Query("SELECT * FROM suppliers ORDER BY name ASC")
    fun getAllSuppliers(): Flow<List<Supplier>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSupplier(supplier: Supplier): Long

    @Update
    suspend fun updateSupplier(supplier: Supplier)

    @Delete
    suspend fun deleteSupplier(supplier: Supplier)

    // --- Artisan Queries ---
    @Query("SELECT * FROM artisans ORDER BY name ASC")
    fun getAllArtisans(): Flow<List<Artisan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArtisan(artisan: Artisan): Long

    @Update
    suspend fun updateArtisan(artisan: Artisan)

    @Delete
    suspend fun deleteArtisan(artisan: Artisan)

    // --- Employee Queries ---
    @Query("SELECT * FROM employees ORDER BY joiningDate DESC")
    fun getAllEmployees(): Flow<List<Employee>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmployee(employee: Employee): Long

    @Delete
    suspend fun deleteEmployee(employee: Employee)

    // --- Banking Queries ---
    @Query("SELECT * FROM bank_accounts ORDER BY bankName ASC")
    fun getAllBankAccounts(): Flow<List<BankAccount>>

    @Update
    suspend fun updateBankAccount(account: BankAccount)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBankAccount(account: BankAccount): Long

    @Delete
    suspend fun deleteBankAccount(account: BankAccount)

    // --- Business Account Queries (Income/Expense) ---
    @Query("SELECT * FROM business_accounts ORDER BY date DESC")
    fun getAllBusinessAccounts(): Flow<List<BusinessAccount>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinessAccount(account: BusinessAccount): Long

    @Delete
    suspend fun deleteBusinessAccount(account: BusinessAccount)

    // --- Authentication Queries ---
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): User?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User): Long
}
