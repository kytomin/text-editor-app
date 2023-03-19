package com.meowplex.text_editor_app.adapters

import android.util.SparseBooleanArray
import androidx.core.util.size
import androidx.recyclerview.widget.RecyclerView

abstract class SelectableAdapter<VH : RecyclerView.ViewHolder>: RecyclerView.Adapter<VH>() {

    private val selectedItems: SparseBooleanArray = SparseBooleanArray()

    var isSelectMode: Boolean = false
    set(value) {
        if (field != value){
            selectedItems.clear()
        }
        field = value
    }

    fun isSelected(position: Int): Boolean {
        return getSelectedIndexes().contains(position)
    }

    fun toggleSelection(position: Int) {
        if (selectedItems[position, false]) {
            selectedItems.delete(position)
        } else {
            selectedItems.put(position, true)
        }
        isSelectMode = selectedItems.size != 0
        notifyItemChanged(position)
        notifyDataSetChanged()
    }

    fun selectAll() {
        isSelectMode = true
        for (i in 0 until itemCount) {
            if (!selectedItems[i, false]) {
                selectedItems.put(i, true)
            }
            notifyItemChanged(i)
        }
        notifyDataSetChanged()
    }

    fun clearSelection() {
        isSelectMode = false
        val selection = getSelectedIndexes()
        selectedItems.clear()
        for (i in selection) {
            notifyItemChanged(i)
        }
        notifyDataSetChanged()
    }

    val selectedItemCount: Int
        get() = selectedItems.size()

    fun getSelectedIndexes(): List<Int> {
        val items: MutableList<Int> = ArrayList(selectedItems.size())
        for (i in 0 until selectedItems.size()) {
            items.add(selectedItems.keyAt(i))
        }
        return items
    }
}