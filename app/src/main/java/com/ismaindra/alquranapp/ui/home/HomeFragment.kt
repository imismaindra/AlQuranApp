package com.ismaindra.alquranapp.ui.detail


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ismaindra.alquranapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Setup listeners untuk quick access cards
        setupQuickAccess()
    }

    private fun setupQuickAccess() {
        // TODO: Implement quick access functionality
        // Misalnya navigasi ke Juz 30 atau halaman terakhir dibaca
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}