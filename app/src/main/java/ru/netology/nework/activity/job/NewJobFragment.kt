package ru.netology.nework.activity.job

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi
import ru.netology.nework.R
import ru.netology.nework.databinding.FragmentNewJobBinding
import ru.netology.nework.utils.AndroidUtils
import ru.netology.nework.viewmodel.JobViewModel

@OptIn(ExperimentalCoroutinesApi::class)
@AndroidEntryPoint
class NewJobFragment : Fragment() {

    private val viewModelJob: JobViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewJobBinding.inflate(inflater)

        binding.saveJob.setOnClickListener {
            val nameJob = binding.name.text.toString()
            val linkJob = binding.link.text.toString()
            if (nameJob.isNotBlank() || linkJob.isNotBlank()) {
                this@NewJobFragment.viewModelJob.saveJob(nameJob, linkJob)
                AndroidUtils.hideKeyboard(requireView())
                findNavController().navigateUp()
            } else {
                Snackbar.make(binding.root, getString(R.string.name_or_link_is_empty), Snackbar.LENGTH_LONG)
                    .setAction(getString(R.string.ok)) {}
                    .show()
            }
        }

        return binding.root
    }

}