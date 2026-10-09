package com.example.incomeexpense

import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: TransactionDbHelper
    private lateinit var adapter: TransactionAdapter

    private lateinit var todayIncomeValue: TextView
    private lateinit var todayExpenseValue: TextView
    private lateinit var todayNetValue: TextView
    private lateinit var transactionList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = TransactionDbHelper(this)

        todayIncomeValue = findViewById(R.id.todayIncomeValue)
        todayExpenseValue = findViewById(R.id.todayExpenseValue)
        todayNetValue = findViewById(R.id.todayNetValue)
        transactionList = findViewById(R.id.transactionList)

        adapter = TransactionAdapter(emptyList()) { transaction ->
            dbHelper.deleteTransaction(transaction.id)
            refreshSummary()
        }

        transactionList.layoutManager = LinearLayoutManager(this)
        transactionList.adapter = adapter

        findViewById<FloatingActionButton>(R.id.addButton).setOnClickListener {
            showAddRecordDialog()
        }

        refreshSummary()
    }

    private fun refreshSummary() {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val summary = dbHelper.getSummaryForDate(today)
        val items = dbHelper.getTransactionsForDate(today)

        todayIncomeValue.text = "NT$ ${String.format("%.0f", summary.income)}"
        todayExpenseValue.text = "NT$ ${String.format("%.0f", summary.expense)}"
        todayNetValue.text = "NT$ ${String.format("%.0f", summary.net)}"
        adapter.submitList(items)
    }

    private fun showAddRecordDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_transaction, null, false)

        val typeSpinner = dialogView.findViewById<Spinner>(R.id.typeSpinner)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.categorySpinner)
        val amountInput = dialogView.findViewById<EditText>(R.id.amountInput)
        val noteInput = dialogView.findViewById<EditText>(R.id.noteInput)

        val typeOptions = arrayOf("收入", "支出")
        val categories = arrayOf("食物", "交通", "住房", "娛樂", "薪資", "其他")

        typeSpinner.adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            typeOptions
        )

        categorySpinner.adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            categories
        )

        AlertDialog.Builder(this)
            .setTitle("新增收支記錄")
            .setView(dialogView)
            .setPositiveButton("儲存") { _, _ ->
                val amountText = amountInput.text.toString().trim()
                if (amountText.isNotEmpty()) {
                    val amount = amountText.toDouble()
                    val type = typeOptions[typeSpinner.selectedItemPosition]
                    val category = categories[categorySpinner.selectedItemPosition]
                    val note = noteInput.text.toString().trim().ifEmpty { category }
                    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                    val transaction = Transaction(
                        type = type,
                        category = category,
                        amount = amount,
                        note = note,
                        date = today
                    )

                    dbHelper.addTransaction(transaction)
                    refreshSummary()
                }
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
