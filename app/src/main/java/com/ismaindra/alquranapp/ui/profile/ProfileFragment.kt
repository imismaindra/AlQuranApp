package com.ismaindra.alquranapp.ui.profile

import android.os.Bundle
import android.util.Log
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
        viewModel.loadUserData()

        viewModel.userName.observe(viewLifecycleOwner) { name ->
            binding.tvName.text = name ?: "-"
            Log.d("ProfileFragment", "User name loaded: $name")
        }

        viewModel.userEmail.observe(viewLifecycleOwner) { email ->
            binding.tvEmail.text = email ?: "-"
            binding.tvInfoEmail.text = "Email: ${email ?: "-"}"
            Log.d("ProfileFragment", "User email loaded: $email")
        }

        binding.btnLogout.setOnClickListener {
            viewModel.logout()

            Toast.makeText(
                requireContext(),
                "Berhasil logout",
                Toast.LENGTH_SHORT
            ).show()
            try {
                findNavController().navigate(
                    R.id.loginFragment,
                    null,
                    navOptions {
                        popUpTo(R.id.nav_graph) { // Clear semua back stack
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                )
            } catch (e: Exception) {
                Log.e("ProfileFragment", "Navigation error: ${e.message}", e)

                // Fallback: Restart activity
                activity?.let { act ->
                    val intent = act.intent
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    intent.addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    act.finish()
                    startActivity(intent)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}