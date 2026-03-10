package com.example.smartshop

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ShoppingAdapter(
    private var items: List<ShoppingItem>,
    private val onToggle: (ShoppingItem) -> Unit,
    private val onDelete: (String) -> Unit
) : RecyclerView.Adapter<ShoppingAdapter.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val cbPurchased: CheckBox = view.findViewById(R.id.cbPurchased)
        val tvItemName: TextView = view.findViewById(R.id.tvItemName)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.list_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.tvItemName.text = item.name

        // Temporarily remove listener
        holder.cbPurchased.setOnCheckedChangeListener(null)
        holder.cbPurchased.isChecked = item.isPurchased

        // Add Strikethrough effect if purchased
        if (item.isPurchased) {
            holder.tvItemName.paintFlags = holder.tvItemName.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
            holder.tvItemName.alpha = 0.5f // Make it look faded
        } else {
            holder.tvItemName.paintFlags = holder.tvItemName.paintFlags and android.graphics.Paint.STRIKE_THRU_TEXT_FLAG.inv()
            holder.tvItemName.alpha = 1.0f // Normal text
        }

        holder.cbPurchased.setOnCheckedChangeListener { _, _ -> onToggle(item) }
        holder.btnDelete.setOnClickListener { onDelete(item.id) }
    }

    override fun getItemCount() = items.size

    fun updateData(newItems: List<ShoppingItem>) {
        items = newItems
        notifyDataSetChanged()
    }
}