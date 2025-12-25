package com.example.projectwork_1.view.fragments

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.icu.util.Calendar
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.domain_room_api.entity.Film
import com.example.projectwork_1.App
import com.example.projectwork_1.MyNotificationReceiver
import com.example.projectwork_1.databinding.FragmentWatchLaterBinding
import com.example.projectwork_1.entity.WatchLaterNotification
import com.example.projectwork_1.utils.AnimationHelper
import com.example.projectwork_1.utils.NotificationConstants
import com.example.projectwork_1.view.activities.MainActivity
import com.example.projectwork_1.view.rv_adapters.FilmListAdapter
import com.example.projectwork_1.view.rv_adapters.FilmListItemDecor
import com.example.projectwork_1.view.rv_adapters.WatchLaterFilmsAdapter
import com.example.projectwork_1.viewmodel.SharedFilmsViewModel
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat.CLOCK_24H
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlin.getValue

class WatchLaterFragment : Fragment() {

    private lateinit var rootWatchLater: FrameLayout
    private lateinit var binding: FragmentWatchLaterBinding
    private lateinit var adapter: WatchLaterFilmsAdapter
    private val viewModel: SharedFilmsViewModel by activityViewModels()
    private var watchLaterFilmsDB = mutableListOf<WatchLaterNotification>()
        set(value) {
            if (field == value) return
            field = value
            adapter.addItems(field)
        }
    private val compDisposable = CompositeDisposable()
    private lateinit var alarmManager: AlarmManager


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

        val watchLaterRecycler = binding.watchLaterRecyclerView
        alarmManager = App.instance.alarmManager

        val intentBroadcast = Intent(requireContext(), MyNotificationReceiver::class.java)
        intentBroadcast.action = NotificationConstants.ALARM_NOTIFICATION_ACTION

        adapter = WatchLaterFilmsAdapter(object : WatchLaterFilmsAdapter.OnWatchLaterClickListener {
            override fun click(
                film: Film,
                posterView: ImageView,
            ) {
                (requireActivity() as MainActivity).launchDetFragment(film, posterView)
            }

            override fun onEditClick(watchLaterNotification: WatchLaterNotification) {
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

                        alarmManager.cancel(createAlarmPendingIntent(watchLaterNotification))

                        val newTriggerTime = calendar.timeInMillis

                        val updated = watchLaterNotification.copy(
                            triggerTime = newTriggerTime,
                            alarmRequestCode = System.currentTimeMillis().toInt()
                        )

                        viewModel.updateWatchLater(updated)

                        alarmManager.set(AlarmManager.RTC_WAKEUP, newTriggerTime, createAlarmPendingIntent(watchLaterNotification))

                        Toast.makeText(requireContext(), "Напоминие изменено", Toast.LENGTH_SHORT).show()
                    }
                }

            }

            override fun onDeleteClick(watchLaterNotification: WatchLaterNotification) {
                val pendingDelete = PendingIntent.getBroadcast(requireContext(),
                    watchLaterNotification.alarmRequestCode,
                    intentBroadcast,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

                alarmManager.cancel(pendingDelete)

                viewModel.removeFromWatchLater(watchLaterNotification.film.id)
            }

        })

        compDisposable.add(
            viewModel.watchLaterFilmsFlowData
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    {watchLaterFilmsDB = it.toMutableList()},
                    {e -> println("!!! Problem in FavoritesFragment $e")}
                )
        )

        adapter.addItems(watchLaterFilmsDB)

        watchLaterRecycler.adapter = adapter
        watchLaterRecycler.layoutManager = LinearLayoutManager(requireContext())
        watchLaterRecycler.addItemDecoration(FilmListItemDecor(8))

        rootWatchLater = binding.rootWatchLater
        AnimationHelper.performFragmentCircularRevealAnimation(rootWatchLater, requireActivity(), 3)
    }

    private fun createAlarmPendingIntent(notificationWatchLater: WatchLaterNotification): PendingIntent {
        val intent = Intent(requireContext(), MyNotificationReceiver::class.java).apply {
            action = NotificationConstants.ALARM_NOTIFICATION_ACTION
            putExtra(NotificationConstants.EXTRA_FILM_ID, notificationWatchLater.film.id)
            putExtra(NotificationConstants.EXTRA_FILM_TITLE, notificationWatchLater.film.title)
        }

        return PendingIntent.getBroadcast(
            requireContext(),
            notificationWatchLater.alarmRequestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        compDisposable.clear()
    }
}