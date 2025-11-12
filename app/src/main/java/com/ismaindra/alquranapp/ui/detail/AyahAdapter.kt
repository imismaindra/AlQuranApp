package com.ismaindra.alquranapp.ui.detail

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.data.model.Ayat
import com.ismaindra.alquranapp.data.model.BookmarkItem
import com.ismaindra.alquranapp.databinding.ItemAyahBinding
import com.ismaindra.alquranapp.utils.BookmarkManager

class AyahAdapter(
    private val showBismillah: Boolean = true
) : ListAdapter<Ayat, RecyclerView.ViewHolder>(AyahDiffCallback()) {

    companion object {
        private const val VIEW_TYPE_BISMILLAH = 0
        private const val VIEW_TYPE_AYAH = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (showBismillah && position == 0) VIEW_TYPE_BISMILLAH else VIEW_TYPE_AYAH
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == VIEW_TYPE_BISMILLAH) {
            // Buat card Bismillah
            val context = parent.context
            val cardView = MaterialCardView(context).apply {
                val params = ViewGroup.MarginLayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
                params.setMargins(32, 32, 32, 16)
                layoutParams = params
                radius = 24f
                cardElevation = 4f

                val textView = TextView(context).apply {
                    text = "بِسْمِ اللَّهِ الرَّحْمَٰنِ الرَّحِيمِ"
                    textSize = 28f
                    setTextColor(ContextCompat.getColor(context, R.color.arabic_text))
                    setBackgroundColor(ContextCompat.getColor(context, R.color.background_white))
                    typeface = Typeface.SERIF
                    textAlignment = TextView.TEXT_ALIGNMENT_CENTER
                    setPadding(24, 24, 24, 24)
                }
                addView(textView)
            }
            BismillahViewHolder(cardView)
        } else {
            val binding = ItemAyahBinding.inflate(inflater, parent, false)
            AyahViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == VIEW_TYPE_AYAH) {
            val actualPosition = if (showBismillah) position - 1 else position
            val ayat = getItem(actualPosition)
            (holder as AyahViewHolder).bind(ayat)
        }
        // VIEW_TYPE_BISMILLAH tidak perlu di-bind apa pun karena teks-nya statis
    }

    override fun getItemCount(): Int {
        return super.getItemCount() + if (showBismillah) 1 else 0
    }

    // --- ViewHolder Bismillah (hanya tampilan teks Arab) ---
    class BismillahViewHolder(view: View) : RecyclerView.ViewHolder(view)

    // --- ViewHolder untuk Ayat ---
    inner class AyahViewHolder(
        private val binding: ItemAyahBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val context: Context = binding.root.context

        fun bind(ayat: Ayat) {
            binding.apply {
                tvAyahNumber.text = ayat.nomor?.toString() ?: "0"
                tvArabic.text = ayat.arabic ?: ""
                tvTranslation.text = ayat.terjemahan ?: ""

                btnCopy.setOnClickListener { copyToClipboard(ayat) }
                btnPlay.setOnClickListener {
                    Toast.makeText(context, "Fitur audio akan segera hadir", Toast.LENGTH_SHORT).show()
                }
                btnBookmark.setOnClickListener { bookmarkAyah(ayat) }
            }
        }

        private fun bookmarkAyah(ayat: Ayat) {
            val bookmarkManager = BookmarkManager(context)
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
        override fun areItemsTheSame(oldItem: Ayat, newItem: Ayat): Boolean =
            oldItem.nomor == newItem.nomor

        override fun areContentsTheSame(oldItem: Ayat, newItem: Ayat): Boolean =
            oldItem == newItem
    }
}
