package com.example.projectwork_1.view.fragments

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentValues
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.icu.util.Calendar
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.bumptech.glide.Glide
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.App
import com.example.projectwork_1.MyNotificationReceiver
import com.example.projectwork_1.utils.ApiConstantsApp
import com.example.projectwork_1.R
import com.example.projectwork_1.databinding.FragmentDetailsBinding
import com.example.projectwork_1.utils.DetailsNotifications
import com.example.projectwork_1.utils.NotificationConstants
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import com.google.android.material.transition.MaterialContainerTransform
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch


class DetailsFragment : Fragment() {

    private lateinit var detDesc: TextView
    private lateinit var detPost: AppCompatImageView
    private lateinit var detToolBar: Toolbar
    private lateinit var detFabShare: FloatingActionButton
    private lateinit var coordinatorLay: CoordinatorLayout
    private lateinit var detFabFav: FloatingActionButton
    private lateinit var detFabNotify: FloatingActionButton
    private lateinit var binding: FragmentDetailsBinding
    private lateinit var film: Film
    private var favDataBase = mutableListOf<Film>()
        set(value) {
            if (field == value) return
            field = value
        }
    private val compDisposable = CompositeDisposable()

    private val viewModel: SharedFilmsViewModel by activityViewModels()
    private val scope = CoroutineScope(Dispatchers.IO)
    lateinit var notificationManager: NotificationManager
    private lateinit var alarmManager: AlarmManager

    init {
        sharedElementEnterTransition = MaterialContainerTransform().apply {
            //это тот вьюгруп, где имеются два фрагмента, через которых и будет проходить анимация с общим элементом
            drawingViewId = R.id.fragment_container
            duration = 500
            //цвет фона
            scrimColor = Color.TRANSPARENT
            propagation = null
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        binding = FragmentDetailsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        compDisposable.add(
            viewModel.favFilmsFlowData
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { favDataBase = it.toMutableList() },
                    { e -> println("!!! Problem in DetailsFragment $e") })
        )

        postponeEnterTransition()

        notificationManager = App.instance.notificationManager
        alarmManager = App.instance.alarmManager

        detDesc = binding.detailsDescription
        detPost = binding.detailsPoster
        detToolBar = binding.detailsToolbar
        detFabShare = binding.detailsFab
        coordinatorLay = binding.coordinatorLay
        detFabFav = binding.detailsFabFav
        detFabNotify = binding.detailsFabNotify

        detActivity()
        startPostponedEnterTransition()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compDisposable.clear()
    }

    fun detActivity() {

        film = arguments?.getParcelable<Film>("film")!!
        val filmTitle = film?.title

        if (film == null) {
            detPost.setImageResource(R.drawable.baseline_error_24)
            detDesc.text = "There was an error occurred!"
            detToolBar.title = "Error occurred!"
        } else {
            Glide.with(this)
                .load(ApiConstantsApp.IMAGES_URL + "w780" + film.poster)
                .centerCrop()
                .into(detPost)
            detDesc.text = film.description
            detToolBar.title = film.title
            //делаем транзишнНейм одинаковым
            detPost.transitionName = "poster_$filmTitle"
        }

        detFabShare.setOnClickListener {
            val intent = Intent(Intent.ACTION_SEND)
            intent.putExtra(
                Intent.EXTRA_TEXT,
                "Глянь этот фильм: ${film?.title} \n \n ${film?.description}"
            )
            intent.type = "text/plain"
            startActivity(Intent.createChooser(intent, "Поделиться:"))
        }

        detFabFav.setImageResource(
            if (film!!.isInFavorites) R.drawable.baseline_favorite_24
            else R.drawable.baseline_favorite_border_24
        )

        detFabFav.setOnClickListener {

            if (!film.isInFavorites) {
                film.isInFavorites = true
                viewModel.addToFavorites(film)
                detFabFav.setImageResource(R.drawable.baseline_favorite_24)
                Toast.makeText(requireContext(), "Добавлено в Избранное", Toast.LENGTH_SHORT).show()
            } else {
                film.isInFavorites = false
                viewModel.removeFromFavorites(film)
                detFabFav.setImageResource(R.drawable.baseline_favorite_border_24)
                Toast.makeText(requireContext(), "Удалено в Избранное", Toast.LENGTH_SHORT).show()
            }
        }

        binding.detailsFabDownloadWp.setOnClickListener {
            performAsyncLoadOfPoster()
        }

        detFabNotify.setOnClickListener {

            val intentBroadcast = Intent(requireContext(), MyNotificationReceiver::class.java)
            intentBroadcast.action = NotificationConstants.ALARM_NOTIFICATION_ACTION


            val datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Выберите дату:")
                .build()

            datePicker.show(parentFragmentManager, "DATE_PICKER")

            datePicker.addOnPositiveButtonClickListener { dateMillis ->
                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(CLOCK_24H)
                    .setHour(12)
                    .setMinute(0)
                    .setTitleText("Выберите время")
                    .build()

                timePicker.show(parentFragmentManager, "TIME_PICKER")

                timePicker.addOnPositiveButtonClickListener {
                    val calendar = Calendar.getInstance()
                    calendar.timeInMillis = dateMillis
                    calendar.set(Calendar.HOUR_OF_DAY, timePicker.hour)
                    calendar.set(Calendar.MINUTE, timePicker.minute)
                    calendar.set(Calendar.SECOND, 0)

                    val triggerTime = calendar.timeInMillis

                    if (!film.isInWatchLater) {

                        viewModel.addToWatchLater(film, triggerTime)

                        intentBroadcast.putExtra(NotificationConstants.EXTRA_FILM_ID, film.id)
                        intentBroadcast.putExtra(NotificationConstants.EXTRA_FILM_TITLE, film.title)

                        val pendingIntentAlarmBroadcast = PendingIntent.getBroadcast(
                            requireContext(),
                            film.id,
                            intentBroadcast,
                            PendingIntent.FLAG_UPDATE_CURRENT,
                        )

                        alarmManager.set(
                            AlarmManager.RTC_WAKEUP,
                            triggerTime,
                            pendingIntentAlarmBroadcast
                        )

                        Toast.makeText(requireContext(), "Напоминание установлено",
                            Toast.LENGTH_SHORT).show()
                    }
                    else {
                        Toast.makeText(requireContext(), "Вы уже установили напоминание на этот фильм",
                            Toast.LENGTH_SHORT).show()
                    }

                }

            }
        }
    }

    private fun checkPermission(): Boolean {
        val result = ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return  result == PackageManager.PERMISSION_GRANTED
    }

    private fun requirePermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            1
        )
    }

    private fun saveToGallery(bitmap: Bitmap) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val contentValues = ContentValues().apply {
                put(MediaStore.Images.Media.TITLE, film.title.handleSingleQuote())
                put(MediaStore.Images.Media.DISPLAY_NAME, film.title.handleSingleQuote())
                put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
                put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
                put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/FilmsSearchApp")
            }

            val contentResolver = requireActivity().contentResolver
            val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            val outputStream = contentResolver.openOutputStream(uri!!)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream!!)
            outputStream.close()
        }
        else {
            @Suppress("DEPRECATION")
            MediaStore.Images.Media.insertImage(
                requireActivity().contentResolver,
                bitmap,
                film.title.handleSingleQuote(),
                film.description.handleSingleQuote())
        }
    }

    private fun performAsyncLoadOfPoster() {
        if (!checkPermission()) {
            requirePermission()
            return
        }

        MainScope().launch {
            binding.detailsProgressBar.isVisible = true
            try {
                val deferred = scope.async {
                    viewModel.loadWallpaper(ApiConstantsApp.IMAGES_URL + "original" + film.poster)
                }
                saveToGallery(deferred.await())
                Snackbar.make(binding.root, R.string.downloaded_to_gallery, Snackbar.LENGTH_LONG)
                    .setAction(R.string.open) {
                        val intent = Intent()
                        intent.action = Intent.ACTION_VIEW
                        intent.type = "image/*"
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }.show()
            } catch (e: Exception) {
                e.printStackTrace()

                Snackbar.make(
                    binding.root,
                    "Error loading image: ${e.message}",
                    Snackbar.LENGTH_SHORT
                ).show()
            } finally {
                binding.detailsProgressBar.isVisible = false
            }
        }
    }

    private fun String.handleSingleQuote(): String {
        return this.replace("'", "")
    }
}