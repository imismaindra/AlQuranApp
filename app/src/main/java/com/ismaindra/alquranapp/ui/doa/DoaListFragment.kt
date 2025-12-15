package com.ismaindra.alquranapp.ui.doa

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.ismaindra.alquranapp.data.api.RetrofitClient
import com.ismaindra.alquranapp.data.model.Doa
import com.ismaindra.alquranapp.databinding.FragmentDoaListBinding
import com.ismaindra.alquranapp.databinding.LayoutDoaDetailBinding
import com.ismaindra.alquranapp.ui.home.HomeViewModel

class DoaListFragment : Fragment() {

    private lateinit var binding: FragmentDoaListBinding
//    private val viewModel: DoaViewModel by viewModels()
private val viewModel: DoaViewModel by lazy {
    val apiService = RetrofitClient.apiService
    DoaViewModel(apiService)
}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDoaListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.rvDoa.layoutManager = LinearLayoutManager(requireContext())

        viewModel.listDoa.observe(viewLifecycleOwner) { list ->
            binding.rvDoa.adapter = DoaAdapter(list) { doa ->
                showDetail(doa)
            }
        }

        viewModel.loadDoa()
    }

    private fun showDetail(doa: Doa) {
        val dialog = BottomSheetDialog(requireContext())

        val detailBinding = LayoutDoaDetailBinding.inflate(layoutInflater)
        detailBinding.tvJudul.text = doa.judul
        detailBinding.tvArab.text = doa.arab
        detailBinding.tvIndo.text = doa.indo

        dialog.setContentView(detailBinding.root)
        dialog.show()
    }
}
