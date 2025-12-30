package com.ismaindra.alquranapp.ui.bookmark

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.ismaindra.alquranapp.databinding.FragmentBookmarkBinding
import com.ismaindra.alquranapp.ui.detail.SurahDetailActivity
import com.ismaindra.alquranapp.utils.AuthManager
import com.ismaindra.alquranapp.utils.BookmarkManager
import kotlinx.coroutines.launch

class BookmarkFragment : Fragment() {

    private var _binding: FragmentBookmarkBinding? = null
    private val binding get() = _binding!!

    private lateinit var bookmarkAdapter: BookmarkAdapter
    private lateinit var bookmarkManager: BookmarkManager
    private lateinit var authManager: AuthManager

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
        authManager = AuthManager(requireContext())

        setupRecyclerView()

        // Cek apakah user sudah login
        if (authManager.isLoggedIn()) {
            syncBookmarks()
        } else {
            loadLocalBookmarks()
        }

        binding.btnClearAll.setOnClickListener {
            showClearAllDialog()
        }

        // Tambahkan swipe refresh jika ada
        binding.swipeRefresh?.setOnRefreshListener {
            if (authManager.isLoggedIn()) {
                syncBookmarks()
            } else {
                binding.swipeRefresh?.isRefreshing = false
                Toast.makeText(requireContext(), "Login untuk sync bookmark", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        // Refresh bookmarks saat kembali ke fragment
        if (authManager.isLoggedIn()) {
            syncBookmarks()
        } else {
            loadLocalBookmarks()
        }
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

    // Sync bookmark dari API
    private fun syncBookmarks() {
        binding.swipeRefresh?.isRefreshing = true

        lifecycleScope.launch {
            val result = bookmarkManager.syncBookmarksFromApi()

            binding.swipeRefresh?.isRefreshing = false

            result.onSuccess { bookmarks ->
                displayBookmarks(bookmarks)
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
                // Fallback ke local bookmarks
                loadLocalBookmarks()
            }
        }
    }

    // Load bookmark dari local storage
    private fun loadLocalBookmarks() {
        val bookmarks = bookmarkManager.getBookmarksFromLocal()
        displayBookmarks(bookmarks)
    }

    private fun displayBookmarks(bookmarks: List<com.ismaindra.alquranapp.data.model.BookmarkItem>) {
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
                deleteBookmark(bookmark)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteBookmark(bookmark: com.ismaindra.alquranapp.data.model.BookmarkItem) {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "Login untuk menghapus bookmark", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val result = bookmarkManager.removeBookmark(bookmark.surahNumber, bookmark.ayahNumber)

            result.onSuccess {
                Toast.makeText(requireContext(), "Bookmark dihapus", Toast.LENGTH_SHORT).show()
                loadLocalBookmarks()
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showClearAllDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Hapus Semua Bookmark")
            .setMessage("Anda yakin ingin menghapus semua bookmark?")
            .setPositiveButton("Hapus Semua") { _, _ ->
                clearAllBookmarks()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun clearAllBookmarks() {
        if (!authManager.isLoggedIn()) {
            Toast.makeText(requireContext(), "Login untuk menghapus semua bookmark", Toast.LENGTH_SHORT).show()
            return
        }

        lifecycleScope.launch {
            val result = bookmarkManager.clearAllBookmarks()

            result.onSuccess {
                Toast.makeText(requireContext(), "Semua bookmark dihapus", Toast.LENGTH_SHORT).show()
                loadLocalBookmarks()
            }.onFailure { error ->
                Toast.makeText(requireContext(), "Error: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}