package ru.netology.nework.activity.post

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import ru.netology.nework.R
import ru.netology.nework.activity.MainFragment
import ru.netology.nework.activity.MainFragment.Companion.textArg
import ru.netology.nework.adapter.PostAdapter
import ru.netology.nework.adapter.OnClick
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.FragmentPostBinding
import ru.netology.nework.dto.Post
import ru.netology.nework.viewmodel.*
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class PostFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth

    private val viewModelPost: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val viewModelJob: JobViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val wallViewModel: WallViewModel by viewModels()

    companion object {
        const val SIGN_IN = "SIGN_IN"
        const val SIGN_OUT = "SIGN_OUT"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentPostBinding.inflate(inflater)
        val adapter = PostAdapter(object : OnClick<Post> {
            override fun onClick(post: Post) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = SIGN_IN
                        })
                } else {
                    MainFragment.observer.mediaPlayer?.release()
                    MainFragment.observer.mediaPlayer = null
                    wallViewModel.loadWallById(post.authorId)
                    userViewModel.loadUserData(post.authorId)
                    viewModelJob.loadJobById(post.authorId)
                    findNavController().navigate(R.id.authorFragment2)
                }
            }

            override fun onRemove(post: Post) {
                viewModelPost.removePostById(post.id)
            }

            override fun onLike(post: Post) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = SIGN_IN
                        })
                } else {
                    if (!post.likeOwnerIds.contains(authViewModel.data.value?.id)) {
                        viewModelPost.likeById(post.id)
                    } else {
                        viewModelPost.unlikeById(post.id)
                    }
                }
            }

            override fun onPlayMusic(post: Post, seekBar: SeekBar) {
                if (post.id != MainFragment.playId) {
                    MainFragment.playId = post.id
                        val url = post.attachment?.url
                        MainFragment.observer.apply {
                            mediaPlayer?.reset()
                            mediaPlayer?.setDataSource(url)
                        }.onPlay(seekBar)
                    } else {
                        MainFragment.observer.mediaPlayer?.start()
                    }
            }

            override fun onPlayVideo(post: Post) {

            }

            override fun onPause() {
                MainFragment.observer.onPause()
            }

            override fun onShare(post: Post) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = SIGN_IN
                        })
                } else {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, post.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.share_content))
                    startActivity(shareIntent)
                }
            }

            override fun onImage(post: Post) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = SIGN_IN
                        })
                } else {
                    val url = post.attachment?.url
                    findNavController().navigate(
                        R.id.action_mainFragment_to_imageFragment3,
                        Bundle().apply {
                            textArg = url
                        })
                }
            }

        }, appAuth)

        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.listPosts.smoothScrollToPosition(0)
                }
            }
        })

        binding.listPosts.layoutManager = LinearLayoutManager(requireContext())
        binding.listPosts.adapter = adapter
        viewModelPost.state.observe(viewLifecycleOwner) { state ->
            binding.progress.isVisible = state.loading

            if (state.loadError) {
                Snackbar.make(binding.root, R.string.error, Snackbar.LENGTH_LONG)
                    .setAction(R.string.retry_loading) {
                        viewModelPost.loadPosts()
                    }
                    .show()
            }
            if (state.likeError) {
                Snackbar.make(
                    binding.root, "\n" +
                            getString(R.string.you_can_t), Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.ok) {}
                    .show()
            }
            if (state.removeError) {
                Snackbar.make(
                    binding.root,
                    getString(R.string.failed_to_connect),
                    Snackbar.LENGTH_LONG
                )
                    .setAction(R.string.ok) {}
                    .show()
            }
            if (state.saveError) {
                Snackbar.make(binding.root, R.string.failed_to_connect, Snackbar.LENGTH_LONG)
                    .setAction(R.string.ok) {}
                    .show()
            }

            binding.swiperefresh.isRefreshing = state.refreshing
        }

        authViewModel.state.observe(viewLifecycleOwner) {
            binding.addPost.isVisible = authViewModel.authorized
        }

        lifecycleScope.launchWhenCreated {
            viewModelPost.data.collectLatest(adapter::submitData)
        }


        lifecycleScope.launchWhenCreated {
            adapter.loadStateFlow.collectLatest {
                binding.swiperefresh.isRefreshing = it.refresh is LoadState.Loading
                        || it.append is LoadState.Loading
                        || it.prepend is LoadState.Loading
            }
        }

        binding.swiperefresh.setOnRefreshListener {
            adapter.refresh()
        }

        binding.addPost.setOnClickListener {
            findNavController().navigate(R.id.newPostFragment)
        }

        return binding.root
    }


}