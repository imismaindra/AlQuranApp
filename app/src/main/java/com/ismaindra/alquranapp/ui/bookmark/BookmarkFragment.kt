package com.ismaindra.alquranapp.ui.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ismaindra.alquranapp.databinding.FragmentBookmarkBinding
import com.ismaindra.alquranapp.ui.detail.SurahDetailActivity
import com.ismaindra.alquranapp.utils.BookmarkManager

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var bookmarkManager: BookmarkManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBookmarkBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bookmarkManager = BookmarkManager(requireContext())

        setupRecyclerView()
        loadBookmarks()

        binding.btnClearAll.setOnClickListener {
            showClearAllDialog()
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh bookmarks saat kembali ke fragment
        loadBookmarks()
    }

    private fun setupRecyclerView() {
        bookmarkAdapter = BookmarkAdapter(
            onItemClick = { bookmark ->
                // Buka detail surah
                val intent = Intent(requireContext(), SurahDetailActivity::class.java).apply {
                    putExtra(SurahDetailActivity.EXTRA_SURAH_NUMBER, bookmark.surahNumber)
                    putExtra(SurahDetailActivity.EXTRA_SURAH_NAME, bookmark.surahName)
                }
                startActivity(intent)
            },
            onDeleteClick = { bookmark ->
                showDeleteDialog(bookmark)
            }
        )

        binding.rvBookmark.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = bookmarkAdapter
            setHasFixedSize(true)
        }
    }

    private fun loadBookmarks() {
        val bookmarks = bookmarkManager.getBookmarks()

        bookmarkAdapter.submitList(bookmarks)

        // Update count
        binding.tvBookmarkCount.text = "${bookmarks.size} Ayat tersimpan"

        // Show/hide empty state
        if (bookmarks.isEmpty()) {
            binding.emptyState.visibility = View.VISIBLE
            binding.rvBookmark.visibility = View.GONE
            binding.btnClearAll.visibility = View.GONE
        } else {
            binding.emptyState.visibility = View.GONE
            binding.rvBookmark.visibility = View.VISIBLE
            binding.btnClearAll.visibility = View.VISIBLE
        }
    }

    private fun showDeleteDialog(bookmark: com.ismaindra.alquranapp.data.model.BookmarkItem) {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Bookmark")
            .setMessage("Hapus bookmark ${bookmark.surahName} : ${bookmark.ayahNumber}?")
            .setPositiveButton("Hapus") { _, _ ->
                bookmarkManager.removeBookmark(bookmark.surahNumber, bookmark.ayahNumber)
                loadBookmarks()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun showClearAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Semua Bookmark")
            .setMessage("Anda yakin ingin menghapus semua bookmark?")
            .setPositiveButton("Hapus Semua") { _, _ ->
                bookmarkManager.clearAllBookmarks()
                loadBookmarks()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}