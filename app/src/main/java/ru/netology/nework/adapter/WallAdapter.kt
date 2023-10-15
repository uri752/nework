package ru.netology.nework.adapter

import android.animation.ObjectAnimator
import android.animation.PropertyValuesHolder
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.BounceInterpolator
import android.widget.ImageView
import android.widget.MediaController
import android.widget.PopupMenu
import androidx.annotation.DrawableRes
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.WallUserBinding
import ru.netology.nework.dto.TypeAttachment
import ru.netology.nework.dto.Wall
import ru.netology.nework.utils.Count
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

class WallAdapter(
    private val onListener: OnClick<Wall>,
    private val auth: AppAuth
) : ListAdapter<Wall, WallViewHolder>(WallDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallViewHolder {
        val binding = WallUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WallViewHolder(binding, onListener, auth)
    }

    override fun onBindViewHolder(holder: WallViewHolder, position: Int) {
        val wall = getItem(position) ?: return
        holder.bind(wall)
    }
}

fun ImageView.loadAvatar(
    url: String,
    @DrawableRes placeholder: Int = R.drawable.ic_loading_100dp,
    @DrawableRes fallBack: Int = R.drawable.ic_error_100,
    timeOutMs: Int = 10_000
) {
    Glide.with(this)
        .load(url)
        .placeholder(placeholder)
        .error(fallBack)
        .timeout(timeOutMs)
        .circleCrop()
        .into(this)
}

class WallViewHolder(
    private val binding: WallUserBinding,
    private val onListener: OnClick<Wall>,
    private val auth: AppAuth
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(wall: Wall) {
        binding.apply {

            val publishedTime = OffsetDateTime.parse(wall.published).toLocalDateTime()
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy   HH:mm:ss")
            publish.text = publishedTime.format(formatter)
            content.text = wall.content
            if (wall.attachment == null) {
                image.visibility = View.GONE
                videoGroup.visibility = View.GONE
                audioGroup.visibility = View.GONE
            } else {
                when (wall.attachment?.type) {
                    TypeAttachment.IMAGE -> {
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        image.visibility = View.VISIBLE
                        val url = wall.attachment?.url
                        Glide.with(image)
                            .load(url)
                            .timeout(10_000)
                            .into(image)
                    }
                    TypeAttachment.VIDEO -> {
                        image.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                        videoGroup.visibility = View.VISIBLE
                        videoView.setVideoURI(
                            Uri.parse(wall.attachment?.url)
                        )
                        videoView.seekTo(10)
                    }
                    TypeAttachment.AUDIO -> {
                        videoGroup.visibility = View.GONE
                        image.visibility = View.VISIBLE
                        audioGroup.visibility = View.VISIBLE
                        image.setImageResource(R.drawable.music_logo)
                        seekBar.max = 0
                    }
                    else -> {
                        image.visibility = View.GONE
                        videoGroup.visibility = View.GONE
                        audioGroup.visibility = View.GONE
                    }
                }

            }

            like.isChecked = wall.likeOwnerIds.contains(auth.getAuthId())
            like.text = Count.logic(wall.likeOwnerIds.size)

            fabPlay.setOnClickListener {
                fabPlay.visibility = View.GONE
                videoView.apply {
                    setMediaController(MediaController(context))
                    setVideoURI(
                        Uri.parse(wall.attachment?.url)
                    )
                    setOnPreparedListener {
                        start()
                    }
                    setOnCompletionListener {
                        stopPlayback()
                    }
                }
            }

            fabPlayAudio.setOnClickListener {
                if (fabPlayAudio.isChecked) {
                    onListener.onPlayMusic(wall, seekBar)
                } else {
                    onListener.onPause()
                }
            }

            like.setOnClickListener {
                if (auth.getAuthId() != 0L) {
                    val scaleX = PropertyValuesHolder.ofFloat(View.SCALE_X, 1F, 1.5F, 1F)
                    val scaleY = PropertyValuesHolder.ofFloat(View.SCALE_Y, 1F, 1.5F, 1F)
                    ObjectAnimator.ofPropertyValuesHolder(it, scaleX, scaleY).apply {
                        duration = 1000
                        repeatCount = 1
                        interpolator = BounceInterpolator()
                    }.start()
                }
                onListener.onLike(wall)
            }

            share.setOnClickListener {
                onListener.onShare(wall)
            }

            image.setOnClickListener {
                onListener.onImage(wall)
            }

            menu.isVisible = wall.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onListener.onRemove(wall)
                                true
                            }

                            else -> false
                        }

                    }
                }.show()
            }
        }
    }
}

class WallDiffCallback : DiffUtil.ItemCallback<Wall>() {
    override fun areItemsTheSame(oldItem: Wall, newItem: Wall): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Wall, newItem: Wall): Boolean {
        return oldItem == newItem
    }

}