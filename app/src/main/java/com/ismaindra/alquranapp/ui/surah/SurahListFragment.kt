package com.ismaindra.alquranapp.ui.surah

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.Surah
import com.ismaindra.alquranapp.databinding.FragmentSurahListBinding
import com.ismaindra.alquranapp.ui.detail.SurahDetailActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SurahListFragment: Fragment() {
    private var _binding: FragmentSurahListBinding? = null
    private val binding get() = _binding!!

    private lateinit var surahAdapter: SurahAdapter
    private var surahList = listOf<Surah>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSurahListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        binding.rvSurah.bringToFront()
        binding.rvSurah.viewTreeObserver.addOnGlobalLayoutListener {
            if (_binding != null) {
                Log.d("RV_SIZE", "width=${binding.rvSurah.width}, height=${binding.rvSurah.height}")
            }
        }


        setupSearch()
        loadSurahList()

        binding.btnRetry.setOnClickListener {
            loadSurahList()
        }
    }

    private fun setupRecyclerView() {
        surahAdapter = SurahAdapter { surah ->
            openSurahDetail(surah)
        }

        binding.rvSurah.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = surahAdapter
            setHasFixedSize(true)
        }

        Log.d("SurahListFragment", "✅ RecyclerView setup complete")
    }

    private fun setupSearch() {
        binding.searchEditText.addTextChangedListener { text ->
            val query = text.toString().lowercase()
            val filteredList = surahList.filter { surah ->
                surah.namaLatin.lowercase().contains(query) ||
                        surah.namaLatin.lowercase().contains(query) ||
                        surah.nomor.toString().contains(query) ||
                        surah.tempatTurun.lowercase().contains(query)
            }

            surahAdapter.submitList(filteredList)

            if (filteredList.isEmpty() && query.isNotEmpty()) {
                binding.emptyState.visibility = View.VISIBLE
                binding.rvSurah.visibility = View.GONE
            } else {
                binding.emptyState.visibility = View.GONE
                binding.rvSurah.visibility = View.VISIBLE
            }
        }
    }

    private fun loadSurahList() {
        showLoading(true)
        showError(false)

        lifecycleScope.launch {
            try {
                Log.d("SurahListFragment", "🔄 Starting API call...")

                val response = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.getAllSurah()
                }
                surahList = response.data
                surahAdapter.submitList(surahList)
                binding.rvSurah.visibility = View.VISIBLE
                binding.emptyState.visibility = View.GONE
                showLoading(false)

            } catch (e: Exception) {
                Log.e("SurahListFragment", "❌ Exception occurred", e)
                showLoading(false)
                showError(true, "Periksa koneksi internet: ${e.localizedMessage}")
            }
        }
        Log.d("UI_CHECK", "rv=${binding.rvSurah.visibility}, progress=${binding.progressBar.visibility}, empty=${binding.emptyState.visibility}, error=${binding.errorState.visibility}")

    }

    private fun openSurahDetail(surah: Surah) {
        val intent = Intent(requireContext(), SurahDetailActivity::class.java).apply {
            putExtra(SurahDetailActivity.EXTRA_SURAH_NUMBER, surah.nomor)
            putExtra(SurahDetailActivity.EXTRA_SURAH_NAME, surah.namaLatin)
            putExtra(SurahDetailActivity.EXTRA_SURAH_REVELATION, surah.tempatTurun)
            putExtra(SurahDetailActivity.EXTRA_AYAH_COUNT, surah.jumlahAyat)
        }
        startActivity(intent)
    }

    private fun showLoading(isLoading: Boolean) {
        Log.d("SurahListFragment", "⏳ showLoading: $isLoading")

        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        // Jangan sembunyikan RecyclerView saat loading selesai
        if (isLoading) {
            binding.rvSurah.visibility = View.GONE
        } else {
            binding.rvSurah.visibility = View.VISIBLE
        }

        binding.errorState.visibility = View.GONE
        binding.emptyState.visibility = View.GONE
    }


    private fun showError(isError: Boolean, message: String = "") {
        Log.d("SurahListFragment", "❌ showError: $isError, message: $message")
        binding.errorState.visibility = if (isError) View.VISIBLE else View.GONE
        binding.rvSurah.visibility = if (isError) View.GONE else View.VISIBLE

        if (isError && message.isNotEmpty()) {
            binding.tvError.text = message
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}