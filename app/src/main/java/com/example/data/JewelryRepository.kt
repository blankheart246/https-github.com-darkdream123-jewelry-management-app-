package com.example.data

import kotlinx.coroutines.flow.Flow

class JewelryRepository(private val jewelryDao: JewelryDao) {

    val allCustomers: Flow<List<Customer>> = jewelryDao.getAllCustomers()
    val allInventoryItems: Flow<List<InventoryItem>> = jewelryDao.getAllInventoryItems()
    val allTransactions: Flow<List<Transaction>> = jewelryDao.getAllTransactions()

    suspend fun getCustomerById(id: Long): Customer? {
        return jewelryDao.getCustomerById(id)
    }

    suspend fun insertCustomer(customer: Customer): Long {
        return jewelryDao.insertCustomer(customer)
    }

    suspend fun updateCustomer(customer: Customer) {
        jewelryDao.updateCustomer(customer)
    }

    suspend fun deleteCustomer(customer: Customer) {
        jewelryDao.deleteCustomer(customer)
    }

    suspend fun getInventoryItemById(id: Long): InventoryItem? {
        return jewelryDao.getInventoryItemById(id)
    }

    suspend fun insertInventoryItem(item: InventoryItem): Long {
        return jewelryDao.insertInventoryItem(item)
    }

    suspend fun updateInventoryItem(item: InventoryItem) {
        jewelryDao.updateInventoryItem(item)
    }

    suspend fun deleteInventoryItem(item: InventoryItem) {
        jewelryDao.deleteInventoryItem(item)
    }

    fun getTransactionsByCustomerId(customerId: Long): Flow<List<Transaction>> {
        return jewelryDao.getTransactionsByCustomerId(customerId)
    }

    suspend fun insertTransaction(transaction: Transaction): Long {
        return jewelryDao.insertTransaction(transaction)
    }

    suspend fun updateTransaction(transaction: Transaction) {
        jewelryDao.updateTransaction(transaction)
    }

    suspend fun deleteTransaction(transaction: Transaction) {
        jewelryDao.deleteTransaction(transaction)
    }

    // --- New Business Modules ---
    val allBranches: Flow<List<Branch>> = jewelryDao.getAllBranches()
    val allSuppliers: Flow<List<Supplier>> = jewelryDao.getAllSuppliers()
    val allArtisans: Flow<List<Artisan>> = jewelryDao.getAllArtisans()
    val allEmployees: Flow<List<Employee>> = jewelryDao.getAllEmployees()
    val allBankAccounts: Flow<List<BankAccount>> = jewelryDao.getAllBankAccounts()
    val allBusinessAccounts: Flow<List<BusinessAccount>> = jewelryDao.getAllBusinessAccounts()

    suspend fun insertBranch(branch: Branch) = jewelryDao.insertBranch(branch)
    suspend fun insertSupplier(supplier: Supplier) = jewelryDao.insertSupplier(supplier)
    suspend fun updateSupplier(supplier: Supplier) = jewelryDao.updateSupplier(supplier)
    suspend fun insertArtisan(artisan: Artisan) = jewelryDao.insertArtisan(artisan)
    suspend fun updateArtisan(artisan: Artisan) = jewelryDao.updateArtisan(artisan)
    suspend fun insertEmployee(employee: Employee) = jewelryDao.insertEmployee(employee)
    suspend fun insertBankAccount(account: BankAccount) = jewelryDao.insertBankAccount(account)
    suspend fun updateBankAccount(account: BankAccount) = jewelryDao.updateBankAccount(account)
    suspend fun insertBusinessAccount(account: BusinessAccount) = jewelryDao.insertBusinessAccount(account)

    // --- Authentication ---
    suspend fun getUserByUsername(username: String): User? = jewelryDao.getUserByUsername(username)
    suspend fun insertUser(user: User): Long = jewelryDao.insertUser(user)
}
