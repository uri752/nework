package ru.netology.nework.activity.users

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.activity.MainFragment.Companion.textArg
import ru.netology.nework.activity.post.PostFragment
import ru.netology.nework.adapter.OnUserClick
import ru.netology.nework.adapter.UserAdapter
import ru.netology.nework.databinding.FragmentUsersBinding
import ru.netology.nework.dto.User
import ru.netology.nework.viewmodel.*

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class UsersFragment : Fragment() {
    private val userViewModel: UserViewModel by viewModels()
    private val wallViewModel: WallViewModel by viewModels()
    private val viewModelJob: JobViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentUsersBinding.inflate(inflater)
        val adapter = UserAdapter(object : OnUserClick {
            override fun onClick(user: User) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    wallViewModel.loadWallById(user.idUser)
                    userViewModel.loadUserData(user.idUser)
                    viewModelJob.loadJobById(user.idUser)
                    findNavController().navigate(R.id.authorFragment2)
                }
            }

        })

        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter

        userViewModel.loadUsers()

        userViewModel.userData.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }

        userViewModel.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            binding.swiperefresh.isRefreshing = state.loading

            if (state.loadError) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        userViewModel.loadUsers()
                    }
                    .show()
            }
        }

        binding.swiperefresh.setOnRefreshListener {
            userViewModel.loadUsers()
        }

        return binding.root
    }

}
