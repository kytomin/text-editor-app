package com.meowplex.text_editor_app.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.meowplex.text_editor_app.R
import com.meowplex.text_editor_app.model.FileModel

class MainAdapter(
    private var dataSet: List<FileModel>,
    private val onItemClick: ((FileModel) -> Unit)? = null,
    private val onChangeSelectItems: ((List<FileModel>) -> Unit)? = null
) :
    SelectableAdapter<MainAdapter.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textView: TextView
        val imageView: ImageView
        val checkBox: CheckBox

        init {
            textView = itemView.findViewById(R.id.textView)
            imageView = itemView.findViewById<ImageView?>(R.id.imageView)
                .also { it.visibility = View.VISIBLE }
            checkBox =
                itemView.findViewById<CheckBox>(R.id.checkBox).also { it.visibility = View.GONE }
        }
    }

    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.text_row_item, viewGroup, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        viewHolder.textView.text = dataSet[position].fileName

        val drawableRes = when (dataSet[position].extension) {
            "json" -> R.drawable.ic_json_file
            "js" -> R.drawable.ic_js_file
            "py" -> R.drawable.ic_py_file
            "rs" -> R.drawable.ic_rust_file
            "cpp" -> R.drawable.ic_cpp_file
            "c" -> R.drawable.ic_c_file
            "cs" -> R.drawable.ic_cs_file
            "html" -> R.drawable.ic_html_file
            "xml" -> R.drawable.ic_xml_file
            "css" -> R.drawable.ic_css_file
            "php" -> R.drawable.ic_php_file
            "kt" -> R.drawable.ic_kt_file
            "java" -> R.drawable.ic_java_file
            "gitignore" -> R.drawable.ic_git_file
            "swift" -> R.drawable.ic_swift_file
            else -> R.drawable.ic_text_file
        }

        viewHolder.imageView.setImageResource(drawableRes)

        if (isSelectMode) {
            viewHolder.imageView.visibility = View.GONE
            viewHolder.checkBox.visibility = View.VISIBLE
            viewHolder.checkBox.isChecked = isSelected(position)

        } else {
            viewHolder.imageView.visibility = View.VISIBLE
            viewHolder.checkBox.visibility = View.GONE
        }


        viewHolder.checkBox.setOnClickListener {
            toggleSelection(position)
            onChangeSelectItems?.invoke(getSelectedItems())
        }


        viewHolder.itemView.setOnClickListener {
            if (isSelectMode) {
                toggleSelection(position)
                onChangeSelectItems?.invoke(getSelectedItems())
            } else {
                onItemClick?.invoke(dataSet[position])
            }
        }

        viewHolder.itemView.setOnLongClickListener {
            if (!isSelectMode) {
                isSelectMode = true
                toggleSelection(position)
                onChangeSelectItems?.invoke(getSelectedItems())
                true
            } else false
        }

    }

    fun updateDataset(dataSet: List<FileModel>){
        this.dataSet = dataSet
        isSelectMode = false
        notifyDataSetChanged()
    }

    fun getSelectedItems() = getSelectedIndexes().map { dataSet[it] }
    override fun getItemCount() = dataSet.size

}
