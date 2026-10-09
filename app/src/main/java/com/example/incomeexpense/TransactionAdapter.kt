package com.example.incomeexpense

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TransactionAdapter(
    private var items: List<Transaction>,
    private val onDelete: (Transaction) -> Unit
) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val typeText: TextView = itemView.findViewById(R.id.typeText)
        val categoryText: TextView = itemView.findViewById(R.id.categoryText)
        val noteText: TextView = itemView.findViewById(R.id.noteText)
        val amountText: TextView = itemView.findViewById(R.id.amountText)
        val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val item = items[position]
        holder.typeText.text = item.type
        holder.categoryText.text = item.category
        holder.noteText.text = item.note

        val display = if (item.type == "收入") "+NT$ ${String.format("%.0f", item.amount)}" else "-NT$ ${String.format("%.0f", item.amount)}"
        holder.amountText.text = display

        holder.deleteButton.setOnClickListener {
            onDelete(item)
        }
    }

    override fun getItemCount(): Int = items.size

    fun submitList(newItems: List<Transaction>) {
        items = newItems
        notifyDataSetChanged()
    }
}
