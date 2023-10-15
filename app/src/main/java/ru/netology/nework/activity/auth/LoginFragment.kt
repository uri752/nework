package ru.netology.nework.activity.auth

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentLoginBinding
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.viewmodel.AuthViewModel
import ru.netology.nework.viewmodel.PostViewModel

@AndroidEntryPoint
class LoginFragment() : Fragment() {

    lateinit var binding: FragmentLoginBinding

    private val viewModelPost: PostViewModel by activityViewModels()
    private val authViewModel: AuthViewModel by activityViewModels()
    @OptIn(ExperimentalCoroutinesApi::class)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        binding.buttonLogin.setOnClickListener {
            val login = binding.login.text.toString()
            if (!login.isNullOrBlank()) {
                val pass = binding.password.text.toString()
                AndroidUtils.hideKeyboard(requireView())
                binding.buttonLogin.let { button ->
                    button.isClickable = false
                    button.text = getString(R.string.Wait_authorization)
                }
                binding.loading.isVisible = true
                binding.login.isEnabled = false
                binding.password.isEnabled = false
                authViewModel.authorization(login, pass)
            } else {
                Toast.makeText(context, R.string.Login_is_Null, Toast.LENGTH_LONG).show()
            }
        }

        authViewModel.data.observe(viewLifecycleOwner) {
            if (authViewModel.authorized) {
             //   viewModelPost.loadPosts()
                findNavController().navigateUp()   //navigate(R.id.fragment_main)
            }
        }

        authViewModel.state.observe(viewLifecycleOwner) { state ->
            if (state.errorCode) {
                with(binding) {
                    buttonLogin.text = "Login"
                    buttonLogin.isClickable = true
                    loading.isVisible = false
                    login.isEnabled = true
                    password.isEnabled = true
                    login.setText("")
                    password.setText("")
                }
                Toast.makeText(context, R.string.Login_not_found, Toast.LENGTH_LONG).show()
            }
        }
        return binding.root
    }

}