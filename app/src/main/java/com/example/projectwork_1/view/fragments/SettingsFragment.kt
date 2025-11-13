package com.example.projectwork_1.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO
import androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_YES
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.projectwork_1.R
import com.example.projectwork_1.databinding.FragmentSettingsBinding
import com.example.projectwork_1.utils.AnimationHelper
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {
    private lateinit var binding: FragmentSettingsBinding
    private val viewModel: SharedFilmsViewModel by activityViewModels()
    private lateinit var rootView: FrameLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentSettingsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rootView = binding.settingsFragmentRoot
        AnimationHelper.performFragmentCircularRevealAnimation(rootView, requireActivity(), 5)

        //подписываемся на наши обозреваемы данные, именно на наш список, который хранит категории, тут viewLifecycleOwner -
        //нужен, чтобы наблюдатель автоматически снимался, когда фрагмент уничтожается, предотвращая утечки памяти и Observer<String> -
        //это лямбда, которая вызывается каждый раз, когда значение categoryPropertyLiveData меняется
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.categoryPropertyFlow.collect {
                    //Тут мы проверяем, какая категория была добавлена, то кнопку, которая соответствует добавленной категории необходимо выбрать
                    when (it) {
                        CATEGORY_POPULAR -> binding.radioPopular.isChecked = true
                        CATEGORY_TOP_RATED -> binding.radioTopRated.isChecked = true
                        CATEGORY_SOON -> binding.radioSoon.isChecked = true
                        CATEGORY_NOW_PLAYING -> binding.radioNowPlaying.isChecked = true
                    }
                }
            }
        }

        //подписываемся на наш наблюдаемый список, и при каждом изменении списка, выполняется код в лямбде
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.themeFlowData.collect { theme ->
                    when (theme) {
                        THEME_DARK -> binding.radioDark.isChecked = true
                        THEME_LIGHT -> binding.radioLight.isChecked = true
                    }
                }
            }
        }

        //ставим слушатель, на выбор кнопок и в соответствии с этой кнопкой, передаем категорию
        binding.radioGroup.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_popular -> viewModel.putCategoryProperty(CATEGORY_POPULAR)
                R.id.radio_top_rated -> viewModel.putCategoryProperty(CATEGORY_TOP_RATED)
                R.id.radio_soon -> viewModel.putCategoryProperty(CATEGORY_SOON)
                R.id.radio_now_playing -> viewModel.putCategoryProperty(CATEGORY_NOW_PLAYING)
            }
        }

        //ставим слушатель на выбор кнопок, в соответствии с кнопкой, выполняем код
        binding.radioGroupTheme.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_light -> {
                    viewModel.setTheme(THEME_LIGHT)
                    //класс AppCompatDelegate позволяет обновить тему приложения без перезапуска активити
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_NO)
                }

                R.id.radio_dark -> {
                    viewModel.setTheme(THEME_DARK)
                    AppCompatDelegate.setDefaultNightMode(MODE_NIGHT_YES)
                }
            }
        }
    }

    companion object {
        private const val CATEGORY_POPULAR = "popular"
        private const val CATEGORY_TOP_RATED = "top_rated"
        private const val CATEGORY_SOON = "upcoming"
        private const val CATEGORY_NOW_PLAYING = "now_playing"
        private const val THEME_LIGHT = "light"
        private const val THEME_DARK = "dark"
    }
}