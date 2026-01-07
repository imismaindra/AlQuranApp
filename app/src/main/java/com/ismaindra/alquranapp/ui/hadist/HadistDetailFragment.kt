package com.ismaindra.alquranapp.ui.hadist

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ismaindra.alquranapp.data.model.Hadist
import com.ismaindra.alquranapp.databinding.FragmentHadistDetailBinding

class HadistDetailFragment : Fragment() {

    private var _binding: FragmentHadistDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHadistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve data from arguments
        val hadist = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("hadist_data", Hadist::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("hadist_data")
        }

        hadist?.let { displayData(it) }

        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun displayData(hadist: Hadist) {
        binding.apply {
            tvJenis.text = hadist.jenis.nama
            tvNomor.text = "Hadist No. ${hadist.no}"
            tvJudul.text = hadist.judul
            tvArab.text = hadist.arab
            tvIndo.text = hadist.indo
            
            // Set toolbar title dynamically if needed, or keep generic "Detail Hadist"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
