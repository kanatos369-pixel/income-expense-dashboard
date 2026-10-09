package com.example.incomeexpense

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

data class Transaction(
    val id: Long = 0,
    val type: String,
    val category: String,
    val amount: Double,
    val note: String,
    val date: String
)

class TransactionDbHelper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    companion object {
        const val DB_NAME = "income_expense.db"
        const val DB_VERSION = 1
        const val TABLE_NAME = "transactions"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_NAME (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                type TEXT NOT NULL,
                category TEXT NOT NULL,
                amount REAL NOT NULL,
                note TEXT,
                date TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(db)
    }

    fun addTransaction(transaction: Transaction) {
        val db = writableDatabase
        val values = android.content.ContentValues().apply {
            put("type", transaction.type)
            put("category", transaction.category)
            put("amount", transaction.amount)
            put("note", transaction.note)
            put("date", transaction.date)
        }
        db.insert(TABLE_NAME, null, values)
    }

    fun getTransactionsForDate(date: String): List<Transaction> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, type, category, amount, note, date FROM $TABLE_NAME WHERE date = ? ORDER BY id DESC",
            arrayOf(date)
        )

        val items = mutableListOf<Transaction>()
        while (cursor.moveToNext()) {
            items.add(
                Transaction(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    note = cursor.getString(cursor.getColumnIndexOrThrow("note")) ?: "",
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                )
            )
        }
        cursor.close()
        return items
    }

    fun getAllTransactions(): List<Transaction> {
        val db = readableDatabase
        val cursor = db.rawQuery(
            "SELECT id, type, category, amount, note, date FROM $TABLE_NAME ORDER BY id DESC",
            null
        )

        val items = mutableListOf<Transaction>()
        while (cursor.moveToNext()) {
            items.add(
                Transaction(
                    id = cursor.getLong(cursor.getColumnIndexOrThrow("id")),
                    type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
                    category = cursor.getString(cursor.getColumnIndexOrThrow("category")),
                    amount = cursor.getDouble(cursor.getColumnIndexOrThrow("amount")),
                    note = cursor.getString(cursor.getColumnIndexOrThrow("note")) ?: "",
                    date = cursor.getString(cursor.getColumnIndexOrThrow("date"))
                )
            )
        }
        cursor.close()
        return items
    }

    fun deleteTransaction(id: Long) {
        val db = writableDatabase
        db.delete(TABLE_NAME, "id = ?", arrayOf(id.toString()))
    }

    fun getSummaryForDate(date: String): Summary {
        val records = getTransactionsForDate(date)
        var income = 0.0
        var expense = 0.0

        records.forEach { item ->
            if (item.type == "收入") {
                income += item.amount
            } else {
                expense += item.amount
            }
        }

        return Summary(income = income, expense = expense, net = income - expense)
    }
}

data class Summary(
    val income: Double,
    val expense: Double,
    val net: Double
)
