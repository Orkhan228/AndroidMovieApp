package com.example.projectwork_1

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import com.example.projectwork_1.databinding.FragmentWatchLaterBinding


class WatchLaterFragment : Fragment() {

    private lateinit var rootWatchLater: FrameLayout
    private lateinit var binding: FragmentWatchLaterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentWatchLaterBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootWatchLater = binding.rootWatchLater
        AnimationHelper.performFragmentCircularRevealAnimation(rootWatchLater, requireActivity(), 3)
    }
}