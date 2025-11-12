package com.hereliesaz.julesapisdk.testapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.hereliesaz.julesapisdk.Source

class SourcesAdapter(
    private val sources: MutableList<Source> = mutableListOf(),
    private val onSourceSelected: (Source) -> Unit
) : RecyclerView.Adapter<SourcesAdapter.SourceViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SourceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(android.R.layout.simple_list_item_1, parent, false)
        return SourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: SourceViewHolder, position: Int) {
        val source = sources[position]
        holder.bind(source, position == selectedPosition)
        holder.itemView.setOnClickListener {
            notifyItemChanged(selectedPosition)
            selectedPosition = holder.adapterPosition
            notifyItemChanged(selectedPosition)
            onSourceSelected(source)
        }
    }

    override fun getItemCount(): Int = sources.size

    fun getSelectedSource(): Source? {
        return if (selectedPosition != RecyclerView.NO_POSITION) {
            sources[selectedPosition]
        } else {
            null
        }
    }

    fun setSources(newSources: List<Source>) {
        sources.clear()
        sources.addAll(newSources)
        notifyDataSetChanged()
    }

    fun setSelectedSource(sourceName: String) {
        val position = sources.indexOfFirst { it.name == sourceName }
        if (position != -1) {
            selectedPosition = position
            notifyDataSetChanged()
        }
    }

    class SourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val textView: TextView = itemView.findViewById(android.R.id.text1)

        fun bind(source: Source, isSelected: Boolean) {
            textView.text = source.url
            itemView.isActivated = isSelected
        }
    }
}
