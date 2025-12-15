package com.ismaindra.alquranapp.ui.doa

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.data.model.Doa
import com.ismaindra.alquranapp.databinding.ItemDetailDoaBinding
import com.ismaindra.alquranapp.databinding.ItemDoaBinding

class DoaAdapter(
    private val items: List<Doa>,
    private val onClick: (Doa) -> Unit
) : RecyclerView.Adapter<DoaAdapter.DoaViewHolder>() {

    inner class DoaViewHolder(val binding: ItemDetailDoaBinding)
        : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Doa) {
            binding.tvJudulDoa.text = item.judul
            binding.tvSumberDoa.text = item.sumber

            binding.root.setOnClickListener {
                onClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoaViewHolder {
        val binding = ItemDetailDoaBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DoaViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DoaViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size
}
