package com.ismaindra.alquranapp.ui.auth.register

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
import com.ismaindra.alquranapp.databinding.FragmentRegisterBinding
import com.ismaindra.alquranapp.ui.register.RegisterViewModel
import com.ismaindra.alquranapp.utils.AuthManager

class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!

    // ViewModel hanya butuh AuthManager
    private val viewModel: RegisterViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return RegisterViewModel(AuthManager(requireContext())) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe loading state
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // Observe hasil registrasi
        viewModel.registerResult.observe(viewLifecycleOwner) { result ->
            result.onSuccess { response ->
                Toast.makeText(
                    requireContext(),
                    response.message ?: "Registrasi berhasil! Silakan masuk.",
                    Toast.LENGTH_LONG
                ).show()

                // Kembali ke LoginFragment (pop back stack)
                findNavController().popBackStack()
            }

            result.onFailure { exception ->
                Toast.makeText(
                    requireContext(),
                    exception.message ?: "Registrasi gagal",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Tombol Daftar
        binding.btnRegister.setOnClickListener {
            val name = binding.etName.text.toString().trim()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            when {
                name.isEmpty() -> {
                    Toast.makeText(requireContext(), "Nama harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                email.isEmpty() -> {
                    Toast.makeText(requireContext(), "Email harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                password.isEmpty() -> {
                    Toast.makeText(requireContext(), "Password harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                confirmPassword.isEmpty() -> {
                    Toast.makeText(requireContext(), "Konfirmasi password harus diisi", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                password != confirmPassword -> {
                    Toast.makeText(requireContext(), "Password tidak cocok", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
            }

            viewModel.register(name, email, password, confirmPassword)
        }

        // Teks "Sudah punya akun? Masuk di sini"
        binding.tvLogin.setOnClickListener {
            findNavController().popBackStack() // kembali ke LoginFragment
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}