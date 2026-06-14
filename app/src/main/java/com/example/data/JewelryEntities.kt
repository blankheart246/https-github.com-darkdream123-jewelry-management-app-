package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val email: String,
    val notes: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val itemType: String, // Ring, Necklace, Bracelet, Earrings, etc.
    val karat: String, // 14K, 18K, 22K, 24K, Platinum, etc.
    val weightGrams: Double,
    val estimatedValue: Double,
    val valueBdt: Double = 0.0,
    val paidBdt: Double = 0.0,
    val dueBdt: Double = 0.0,
    val tags: String = "", // Comma-separated tags, e.g., "vintage, earrings, gold"
    val notes: String,
    val imageBase64: String? = null, // Easy base64 storage
    val isSold: Boolean = false,
    val soldToCustomerId: Long? = null,
    val branchId: Long? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val username: String,
    val passwordHash: String,
    val role: String = "Admin",
    val lastLogin: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "transactions",
    foreignKeys = [
        androidx.room.ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = androidx.room.ForeignKey.CASCADE
        )
    ],
    indices = [androidx.room.Index(value = ["customerId"])]
)
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val inventoryItemId: Long? = null,
    val itemDescription: String,
    val transactionType: String, // "Purchase", "Repair", "Custom Order"
    val amount: Double,
    val amountBdt: Double = 0.0,
    val paidBdt: Double = 0.0,
    val dueBdt: Double = 0.0,
    val date: Long = System.currentTimeMillis(),
    val notes: String
)

@Entity(tableName = "branches")
data class Branch(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val branchName: String,
    val location: String,
    val phone: String,
    val isMainBranch: Boolean = false
)

@Entity(tableName = "suppliers")
data class Supplier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contact: String,
    val address: String,
    val totalDueBdt: Double = 0.0,
    val totalPaidBdt: Double = 0.0
)

@Entity(tableName = "artisans")
data class Artisan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val contact: String,
    val specialty: String, // e.g., "Stone Setting", "Polish", "Molding"
    val goldBalanceGrams: Double = 0.0,
    val wageDueBdt: Double = 0.0
)

@Entity(tableName = "employees")
data class Employee(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val role: String, // Manager, Sales, Artisan, Security
    val phone: String,
    val salary: Double,
    val joiningDate: Long = System.currentTimeMillis()
)

@Entity(tableName = "bank_accounts")
data class BankAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val bankName: String,
    val accountNo: String,
    val balanceBdt: Double = 0.0
)

@Entity(tableName = "business_accounts")
data class BusinessAccount(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // "Income", "Expense"
    val category: String, // "Salary", "Rent", "Utility", "Sale", "Investment"
    val amountBdt: Double,
    val date: Long = System.currentTimeMillis(),
    val notes: String,
    val branchId: Long? = null
)
