package com.ismaindra.alquranapp.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ismaindra.alquranapp.data.model.Ayat
import com.ismaindra.alquranapp.data.model.BookmarkItem
import com.ismaindra.alquranapp.databinding.ItemAyahBinding
import com.ismaindra.alquranapp.utils.BookmarkManager

class AyahAdapter : ListAdapter<Ayat, AyahAdapter.AyahViewHolder>(AyahDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AyahViewHolder {
        val binding = ItemAyahBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return AyahViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AyahViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class AyahViewHolder(
        private val binding: ItemAyahBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context = binding.root.context

        fun bind(ayat: Ayat) {
            binding.apply {
                // Gunakan property yang sesuai dengan model Ayat Anda
                tvAyahNumber.text = ayat.nomor?.toString() ?: "0"
                tvArabic.text = ayat.arabic ?: ""
                tvTranslation.text = ayat.terjemahan ?: ""

                // Copy button click
                btnCopy.setOnClickListener {
                    copyToClipboard(ayat)
                }

                // Play audio button - Note: model Ayat tidak punya audio
                btnPlay.setOnClickListener {
                    // playAudio() // Uncomment jika Anda tambah audio di model
                    Toast.makeText(context, "Fitur audio akan segera hadir", Toast.LENGTH_SHORT).show()
                }

                // Bookmark button
                btnBookmark.setOnClickListener {
                    bookmarkAyah(ayat)
                }
            }
        }
        private fun bookmarkAyah(ayat: Ayat) {
            val bookmarkManager = BookmarkManager(context)

            // Get surah info dari activity
            val activity = context as? SurahDetailActivity
            val surahNumber = activity?.intent?.getIntExtra(SurahDetailActivity.EXTRA_SURAH_NUMBER, 0) ?: 0
            val surahName = activity?.intent?.getStringExtra(SurahDetailActivity.EXTRA_SURAH_NAME) ?: ""

            val bookmark = BookmarkItem(
                surahNumber = surahNumber,
                surahName = surahName,
                ayahNumber = ayat.nomor ?: 0,
                arabicText = ayat.arabic ?: "",
                translation = ayat.terjemahan ?: ""
            )

            if (bookmarkManager.isBookmarked(surahNumber, ayat.nomor ?: 0)) {
                bookmarkManager.removeBookmark(surahNumber, ayat.nomor ?: 0)
                Toast.makeText(context, "Bookmark dihapus", Toast.LENGTH_SHORT).show()
            } else {
                bookmarkManager.addBookmark(bookmark)
                Toast.makeText(context, "Ayat ditandai", Toast.LENGTH_SHORT).show()
            }
        }

        private fun copyToClipboard(ayat: Ayat) {
            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText(
                "Ayat ${ayat.nomor}",
                "${ayat.arabic}\n\n${ayat.terjemahan}"
            )
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "Ayat disalin", Toast.LENGTH_SHORT).show()
        }


    }

    class AyahDiffCallback : DiffUtil.ItemCallback<Ayat>() {
        override fun areItemsTheSame(oldItem: Ayat, newItem: Ayat): Boolean {
            return oldItem.nomor == newItem.nomor
        }

        override fun areContentsTheSame(oldItem: Ayat, newItem: Ayat): Boolean {
            return oldItem == newItem
        }
    }
}