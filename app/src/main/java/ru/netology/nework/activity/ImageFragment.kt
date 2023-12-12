package ru.netology.nework.activity

import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import ru.netology.nework.activity.MainFragment.Companion.textArg
import ru.netology.nework.databinding.FragmentImageBinding

class ImageFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val binding = FragmentImageBinding.inflate(inflater, container, false)

        arguments?.textArg?.let {
            val uri: Uri = Uri.parse(it)
            Glide.with(binding.image)
                .load(uri)
                .timeout(10_000)
                .into(binding.image)
        }
        return binding.root
    }

}