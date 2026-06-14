package com.example.util

import android.content.Context
import com.example.data.InventoryItem
import com.example.data.Transaction
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object CsvExporter {
    fun exportInventoryToCsv(context: Context, items: List<InventoryItem>): File? {
        val fileName = "inventory_export_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        return try {
            FileWriter(file).use { writer ->
                // Header
                writer.append("ID,Title,Type,Karat,Weight(g),Value(BDT),Paid(BDT),Due(BDT),Sold,Date\n")
                
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                
                for (item in items) {
                    writer.append("${item.id},")
                    writer.append("${item.title.replace(",", " ")},")
                    writer.append("${item.itemType},")
                    writer.append("${item.karat},")
                    writer.append("${item.weightGrams},")
                    writer.append("${item.valueBdt},")
                    writer.append("${item.paidBdt},")
                    writer.append("${item.dueBdt},")
                    writer.append("${item.isSold},")
                    writer.append("${dateFormat.format(Date(item.createdAt))}\n")
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportTransactionsToCsv(context: Context, transactions: List<Transaction>): File? {
        val fileName = "transactions_export_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        return try {
            FileWriter(file).use { writer ->
                writer.append("ID,CustomerID,Type,Amount(BDT),Paid(BDT),Due(BDT),Description,Date\n")
                
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                
                for (txn in transactions) {
                    writer.append("${txn.id},")
                    writer.append("${txn.customerId},")
                    writer.append("${txn.transactionType},")
                    writer.append("${txn.amountBdt},")
                    writer.append("${txn.paidBdt},")
                    writer.append("${txn.dueBdt},")
                    writer.append("${txn.itemDescription.replace(",", " ")},")
                    writer.append("${dateFormat.format(Date(txn.date))}\n")
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun exportCustomersToCsv(context: Context, customers: List<com.example.data.Customer>): File? {
        val fileName = "customers_export_${System.currentTimeMillis()}.csv"
        val file = File(context.cacheDir, fileName)
        
        return try {
            FileWriter(file).use { writer ->
                writer.append("ID,Name,Phone,Email,Notes,DateAdded\n")
                
                val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                
                for (cust in customers) {
                    writer.append("${cust.id},")
                    writer.append("${cust.name.replace(",", " ")},")
                    writer.append("${cust.phone},")
                    writer.append("${cust.email},")
                    writer.append("${cust.notes.replace(",", " ")},")
                    writer.append("${dateFormat.format(Date(cust.createdAt))}\n")
                }
            }
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
