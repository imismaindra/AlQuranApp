package com.ismaindra.alquranapp.ui.hadist

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.ismaindra.alquranapp.R
import com.ismaindra.alquranapp.databinding.FragmentHadistBinding

class HadistFragment : Fragment() {

    private var _binding: FragmentHadistBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: HadistViewModel
    private lateinit var adapter: HadistAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHadistBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        viewModel = ViewModelProvider(this)[HadistViewModel::class.java]
        
        setupRecyclerView()
        setupObservers()
        setupListeners()
        
        // Initial Load
        viewModel.getAllHadist()
    }

    private fun setupRecyclerView() {
        adapter = HadistAdapter(emptyList()) { hadist ->
            val bundle = Bundle().apply {
                putParcelable("hadist_data", hadist)
            }
            findNavController().navigate(R.id.action_hadistFragment_to_hadistDetailFragment, bundle)
        }
        binding.rvHadist.layoutManager = LinearLayoutManager(requireContext())
        binding.rvHadist.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.getAllHadist()
        }
    }

    private fun setupObservers() {
        viewModel.hadistList.observe(viewLifecycleOwner) { list ->
            binding.swipeRefresh.isRefreshing = false
            if (list.isNotEmpty()) {
                adapter = HadistAdapter(list) { hadist ->
                    val bundle = Bundle().apply {
                        putParcelable("hadist_data", hadist)
                    }
                    findNavController().navigate(R.id.action_hadistFragment_to_hadistDetailFragment, bundle)
                }
                binding.rvHadist.adapter = adapter
                binding.tvError.visibility = View.GONE
            } else {
                 binding.tvError.visibility = View.VISIBLE
                 binding.tvError.text = "Data kosong"
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // Only show progress bar if not swiping
            if (!binding.swipeRefresh.isRefreshing) {
                binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { error ->
            binding.swipeRefresh.isRefreshing = false
            if (error != null) {
                binding.tvError.visibility = View.VISIBLE
                binding.tvError.text = error
                Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}