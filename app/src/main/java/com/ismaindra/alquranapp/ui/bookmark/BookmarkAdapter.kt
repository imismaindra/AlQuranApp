package com.ismaindra.alquranapp.ui.bookmark

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.data.model.BookmarkItem
import com.ismaindra.alquranapp.databinding.ItemBookmarkBinding
import com.ismaindra.alquranapp.databinding.ItemSurahBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

class BookmarkAdapter(
    private val onItemClick: (BookmarkItem) -> Unit,
    private val onDeleteClick: (BookmarkItem) -> Unit
) : ListAdapter<BookmarkItem, BookmarkAdapter.BookmarkViewHolder>(BookmarkDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookmarkViewHolder {
        val binding = ItemBookmarkBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookmarkViewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookmarkViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class BookmarkViewHolder(
        private val binding: ItemBookmarkBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(bookmark: BookmarkItem) {
            binding.apply {
                tvSurahInfo.text = "${bookmark.surahName} : ${bookmark.ayahNumber}"
                tvArabicPreview.text = bookmark.arabicText
                tvTranslationPreview.text = bookmark.translation
                tvTimestamp.text = getTimeAgo(bookmark.timestamp)

                root.setOnClickListener {
                    onItemClick(bookmark)
                }

                btnDelete.setOnClickListener {
                    onDeleteClick(bookmark)
                }
            }
        }

        private fun getTimeAgo(timestamp: Long): String {
            val now = System.currentTimeMillis()
            val diff = now - timestamp

            return when {
                diff < TimeUnit.MINUTES.toMillis(1) -> "Baru saja"
                diff < TimeUnit.HOURS.toMillis(1) -> {
                    val minutes = TimeUnit.MILLISECONDS.toMinutes(diff)
                    "$minutes menit yang lalu"
                }
                diff < TimeUnit.DAYS.toMillis(1) -> {
                    val hours = TimeUnit.MILLISECONDS.toHours(diff)
                    "$hours jam yang lalu"
                }
                diff < TimeUnit.DAYS.toMillis(7) -> {
                    val days = TimeUnit.MILLISECONDS.toDays(diff)
                    "$days hari yang lalu"
                }
                else -> {
                    val sdf = SimpleDateFormat("dd MMM yyyy", Locale("id", "ID"))
                    sdf.format(Date(timestamp))
                }
            }
        }
    }

    class BookmarkDiffCallback : DiffUtil.ItemCallback<BookmarkItem>() {
        override fun areItemsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
            return oldItem.surahNumber == newItem.surahNumber &&
                    oldItem.ayahNumber == newItem.ayahNumber
        }

        override fun areContentsTheSame(oldItem: BookmarkItem, newItem: BookmarkItem): Boolean {
            return oldItem == newItem
        }
    }
}