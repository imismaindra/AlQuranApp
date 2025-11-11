package com.ismaindra.alquranapp.ui.surah

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.data.model.Surah
import com.ismaindra.alquranapp.databinding.ItemSurahBinding

class SurahAdapter(
    private val onItemClick: (Surah) -> Unit
) : ListAdapter<Surah, SurahAdapter.SurahViewHolder>(SurahDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SurahViewHolder {
        Log.d("SurahAdapter", "📦 onCreateViewHolder called")
        val binding = ItemSurahBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return SurahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SurahViewHolder, position: Int) {
        Log.d("SurahAdapter", "🔗 onBindViewHolder position: $position")
        holder.bind(getItem(position))
    }

    override fun submitList(list: List<Surah>?) {
        Log.d("SurahAdapter", "📝 submitList called with ${list?.size ?: 0} items")
        super.submitList(list)
    }

    inner class SurahViewHolder(
        private val binding: ItemSurahBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(surah: Surah) {
            Log.d("SurahAdapter", "✏️ Binding surah: ${surah.nomor} - ${surah.namaLatin}")
            binding.apply {
                tvNumber.text = surah.nomor.toString()
                tvName.text = surah.namaLatin
                tvArabicName.text = surah.nama
                tvRevelation.text = surah.arti
                tvAyahs.text = "${surah.jumlah_ayat} Ayat"

                root.setOnClickListener {
                    Log.d("SurahAdapter", "🖱️ Clicked surah: ${surah.namaLatin}")
                    onItemClick(surah)
                }
            }
        }
    }

    class SurahDiffCallback : DiffUtil.ItemCallback<Surah>() {
        override fun areItemsTheSame(oldItem: Surah, newItem: Surah): Boolean {
            return oldItem.nomor == newItem.nomor
        }

        override fun areContentsTheSame(oldItem: Surah, newItem: Surah): Boolean {
            return oldItem == newItem
        }
    }
}