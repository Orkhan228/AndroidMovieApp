package com.example.projectwork_1.view.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import com.example.projectwork_1.databinding.FragmentCollectionsBinding
import com.example.projectwork_1.utils.AnimationHelper


class CollectionsFragment : Fragment() {

    private lateinit var rootViewCollect: FrameLayout
    private lateinit var binding: FragmentCollectionsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentCollectionsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootViewCollect = binding.rootCollections
        AnimationHelper.performFragmentCircularRevealAnimation(rootViewCollect, requireActivity(), 4)
    }

}