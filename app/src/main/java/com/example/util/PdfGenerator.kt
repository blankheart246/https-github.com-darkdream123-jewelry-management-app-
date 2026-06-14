package com.example.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.os.Environment
import com.example.data.Customer
import com.example.data.Transaction
import com.example.ui.JewelryViewModel
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object PdfGenerator {

    fun generateInvoicePdf(
        context: Context,
        transaction: Transaction,
        customer: Customer?,
        businessConfig: JewelryViewModel.BusinessConfig
    ): File? {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4 Size
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()
        val titlePaint = Paint()

        val sdf = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        val dateStr = sdf.format(Date(transaction.date))
        val memoNo = "#SS-${1000 + transaction.id}"

        // Header
        titlePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        titlePaint.textSize = 24f
        titlePaint.color = Color.BLACK
        canvas.drawText(businessConfig.shopName, 40f, 60f, titlePaint)

        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        canvas.drawText(businessConfig.address, 40f, 80f, paint)
        canvas.drawText("Phone: ${businessConfig.phone}", 40f, 100f, paint)
        canvas.drawText("Owner: ${businessConfig.ownerName}", 40f, 120f, paint)

        // Invoice Header
        titlePaint.textSize = 18f
        canvas.drawText("INVOICE / CASH MEMO", 400f, 60f, titlePaint)
        paint.textSize = 12f
        canvas.drawText("Date: $dateStr", 400f, 80f, paint)
        canvas.drawText("Memo No: $memoNo", 400f, 100f, paint)

        // Customer Info
        canvas.drawLine(40f, 140f, 555f, 140f, paint)
        titlePaint.textSize = 14f
        canvas.drawText("BILL TO:", 40f, 170f, titlePaint)
        paint.textSize = 12f
        canvas.drawText("Name: ${customer?.name ?: "Walking Customer"}", 40f, 190f, paint)
        canvas.drawText("Phone: ${customer?.phone ?: "N/A"}", 40f, 210f, paint)
        canvas.drawText("Notes: ${customer?.notes ?: ""}", 40f, 230f, paint)

        // Item Table
        canvas.drawLine(40f, 250f, 555f, 250f, paint)
        titlePaint.textSize = 12f
        canvas.drawText("DESCRIPTION", 45f, 270f, titlePaint)
        canvas.drawText("TYPE", 250f, 270f, titlePaint)
        canvas.drawText("AMOUNT (BDT)", 450f, 270f, titlePaint)
        canvas.drawLine(40f, 280f, 555f, 280f, paint)

        // Item Data
        canvas.drawText(transaction.itemDescription, 45f, 310f, paint)
        canvas.drawText(transaction.transactionType, 250f, 310f, paint)
        canvas.drawText("৳ ${String.format("%,.2f", transaction.amountBdt)}", 450f, 310f, paint)

        // Totals
        canvas.drawLine(40f, 500f, 555f, 500f, paint)
        val statsY = 530f
        canvas.drawText("Total Amount:", 350f, statsY, paint)
        canvas.drawText("৳ ${String.format("%,.2f", transaction.amountBdt)}", 450f, statsY, paint)
        
        canvas.drawText("Paid Amount:", 350f, statsY + 25f, paint)
        canvas.drawText("৳ ${String.format("%,.2f", transaction.paidBdt)}", 450f, statsY + 25f, paint)

        val duePaint = Paint(paint)
        if (transaction.dueBdt > 0) {
            duePaint.color = Color.RED
            duePaint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        }
        canvas.drawText("Due Balance:", 350f, statsY + 50f, duePaint)
        canvas.drawText("৳ ${String.format("%,.2f", transaction.dueBdt)}", 450f, statsY + 50f, duePaint)

        // Footer
        paint.textSize = 10f
        paint.color = Color.GRAY
        canvas.drawText("Generated via Shornoly Jewelry Management System", 40f, 780f, paint)
        canvas.drawText("Authorized Signature: _______________________", 350f, 780f, paint)

        pdfDocument.finishPage(page)

        val folder = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(folder, "Invoice_${memoNo.replace("#", "")}.pdf")

        return try {
            val fos = FileOutputStream(file)
            pdfDocument.writeTo(fos)
            pdfDocument.close()
            fos.close()
            file
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
