package com.ismaindra.alquranapp.ui.login

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
import androidx.navigation.navOptions
import com.ismaindra.alquranapp.databinding.FragmentLoginBinding
import com.ismaindra.alquranapp.utils.AuthManager
import com.ismaindra.alquranapp.R
import android.util.Log

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val viewModel: LoginViewModel by viewModels {
        object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return LoginViewModel(AuthManager(requireContext())) as T
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Observe loading
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            binding.btnLogin.isEnabled = !isLoading
        }

        // Observe hasil login
        viewModel.loginResult.observe(viewLifecycleOwner) { result ->
            Log.d("LoginFragment", "loginResult observed: success=${result.isSuccess}")

            result.onSuccess { authResponse ->
                Log.d("LoginFragment", "Login success! Message: ${authResponse.message}")

                Toast.makeText(
                    requireContext(),
                    authResponse.message ?: "Login berhasil!",
                    Toast.LENGTH_SHORT
                ).show()

                // Navigate ke profile setelah login sukses
                try {
                    findNavController().navigate(
                        R.id.profileFragment,
                        null,
                        navOptions {
                            popUpTo(R.id.loginFragment) {
                                inclusive = true
                            }
                            launchSingleTop = true
                        }
                    )
                    Log.d("LoginFragment", "Navigation executed")
                } catch (e: Exception) {
                    Log.e("LoginFragment", "Navigation error: ${e.message}", e)
                    // Fallback navigation tanpa options
                    try {
                        findNavController().navigate(R.id.profileFragment)
                    } catch (e2: Exception) {
                        Log.e("LoginFragment", "Fallback navigation also failed: ${e2.message}", e2)
                    }
                }
            }

            result.onFailure { exception ->
                Log.e("LoginFragment", "Login failed: ${exception.message}")
                Toast.makeText(
                    requireContext(),
                    exception.message ?: "Login gagal",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        // Tombol Login
        binding.btnLogin.setOnClickListener {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            when {
                email.isEmpty() -> {
                    binding.etEmail.error = "Email harus diisi"
                    Toast.makeText(requireContext(), "Email harus diisi", Toast.LENGTH_SHORT).show()
                }
                password.isEmpty() -> {
                    binding.etPassword.error = "Password harus diisi"
                    Toast.makeText(requireContext(), "Password harus diisi", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    Log.d("LoginFragment", "Attempting login with email: $email")
                    viewModel.login(email, password)
                }
            }
        }

        // Ke halaman Register
        binding.tvRegister.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}