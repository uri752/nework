package ru.netology.nework.activity.wall

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.SeekBar
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nework.R
import ru.netology.nework.activity.MainFragment
import ru.netology.nework.activity.MainFragment.Companion.textArg
import ru.netology.nework.activity.post.PostFragment
import ru.netology.nework.adapter.WallAdapter
import ru.netology.nework.adapter.OnClick
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.FragmentWallBinding
import ru.netology.nework.dto.Wall
import ru.netology.nework.viewmodel.*
import javax.inject.Inject

@AndroidEntryPoint
@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WallFragment : Fragment() {

    @Inject
    lateinit var appAuth: AppAuth
    private var playTrack = -1L
    private val viewModelPost: PostViewModel by viewModels()
    private val authViewModel: AuthViewModel by viewModels()
    private val wallViewModel: WallViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val binding = FragmentWallBinding.inflate(inflater, container, false)
        val adapter = WallAdapter(object : OnClick<Wall> {


            override fun onRemove(wall: Wall) {
                wallViewModel.removeWallPostDao(wall.id)
                viewModelPost.removePostById(wall.id)
            }

            override fun onLike(wall: Wall) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    if (!wall.likeOwnerIds.contains(authViewModel.data.value?.id)) {
                        viewModelPost.likeById(wall.id)
                        wallViewModel.likeByIdWall(wall.id)
                    } else {
                        viewModelPost.unlikeById(wall.id)
                        wallViewModel.unlikeByIdWall(wall.id)
                    }
                }
            }

            override fun onPlayMusic(wall: Wall, seekBar: SeekBar) {
                if (wall.id != playTrack) {
                    playTrack = wall.id
                    val url = wall.attachment?.url
                    MainFragment.observer.apply {
                        mediaPlayer?.reset()
                        mediaPlayer?.setDataSource(url)
                    }.onPlay(seekBar)
                } else {
                    MainFragment.observer.mediaPlayer?.start()
                }
            }

            override fun onPlayVideo(wall: Wall) {
            }

            override fun onClick(data: Wall) {

            }

            override fun onPause() {
                MainFragment.observer.onPause()
            }

            override fun onShare(wall: Wall) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    val intent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, wall.content)
                        type = "text/plain"
                    }
                    val shareIntent =
                        Intent.createChooser(intent, getString(R.string.share_content))
                    startActivity(shareIntent)
                }
            }

            override fun onImage(wall: Wall) {
                if (!authViewModel.authorized) {
                    findNavController().navigate(R.id.logoutFragment,
                        Bundle().apply {
                            textArg = PostFragment.SIGN_IN
                        })
                } else {
                    val url = wall.attachment?.url
                    findNavController().navigate(
                        R.id.action_authorFragment2_to_imageFragment3,
                        Bundle().apply {
                            textArg = url
                        })
                }
            }
        }, appAuth)
        binding.rcView.layoutManager = LinearLayoutManager(activity)
        binding.rcView.adapter = adapter

        wallViewModel.wallData.observe(viewLifecycleOwner) {
            if (it.isEmpty()) {
                binding.wallEmpty.isVisible = true
                binding.wallEmpty.text = getString(R.string.post_not_found)
            }
            adapter.submitList(it)
        }

        authViewModel.data.observe(viewLifecycleOwner) {

        }

        wallViewModel.state.observe(viewLifecycleOwner) { state ->
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
        }

        return binding.root
    }

}
