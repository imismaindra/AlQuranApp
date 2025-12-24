package com.ismaindra.alquranapp.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ismaindra.alquranapp.databinding.FragmentProfileBinding
import com.ismaindra.alquranapp.utils.AuthManager
import com.ismaindra.alquranapp.R
import androidx.navigation.NavOptions
import androidx.navigation.navOptions

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ProfileViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ProfileViewModel(AuthManager(requireContext())) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Tampilkan nama user
        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.tvName.text = name ?: "Nama Tidak Tersedia"
        }

        // Tampilkan email user
        viewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.tvEmail.text = email ?: "email@contoh.com"
            binding.tvInfoEmail.text = "Email: ${email ?: "-"}"
        }

        // Tombol Logout
        binding.btnLogout.setOnClickListener {
            viewModel.logout()

            Toast.makeText(requireContext(), "Berhasil keluar", Toast.LENGTH_SHORT).show()

            findNavController().navigate(
                R.id.homeFragment,
                null,
                navOptions {
                    popUpTo(R.id.homeFragment) {
                        inclusive = false
                    }
                    launchSingleTop = true
                }
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}