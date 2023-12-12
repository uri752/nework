package ru.netology.nework.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.view.isVisible
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nework.R
import ru.netology.nework.auth.AppAuth
import ru.netology.nework.databinding.CardJobBinding
import ru.netology.nework.dto.Job
import ru.netology.nework.viewmodel.UserViewModel
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter

interface OnClickMenu {
    fun onClick(job: Job)
}

class JobAdapter(
    private val auth: AppAuth,
    private val userViewModel: UserViewModel,
    private val onClickMenu: OnClickMenu,
) : ListAdapter<Job, JobViewHolder>(JobDiffCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        val binding = CardJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return JobViewHolder(binding, auth, userViewModel, onClickMenu)
    }

    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val job = getItem(position) ?: return
        holder.bind(job)
    }

}

class JobViewHolder(
    private val binding: CardJobBinding,
    private val auth: AppAuth,
    private val userViewModel: UserViewModel,
    private val onClickMenu: OnClickMenu,
) : RecyclerView.ViewHolder(binding.root) {
    private var startJobTime: LocalDateTime = LocalDateTime.now()
    private var endJobTime: LocalDateTime = LocalDateTime.now()
    fun bind(job: Job) {
        binding.apply {
            val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy   HH:mm:ss")
            try {
                 startJobTime = OffsetDateTime.parse(job.start).toLocalDateTime()
                 endJobTime = OffsetDateTime.parse(job.finish).toLocalDateTime()

            } catch (_: Exception) {}
            startTime.text = startJobTime.format(formatter)
            finishTime.text = endJobTime.format(formatter)
            name.text = job.name
            position.text = job.position
            menu.isVisible = auth.getAuthId() == userViewModel.user.value?.idUser
            link.text = job.link
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.option_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onClickMenu.onClick(job)
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

class JobDiffCallback : DiffUtil.ItemCallback<Job>() {
    override fun areItemsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Job, newItem: Job): Boolean {
        return oldItem == newItem
    }

}