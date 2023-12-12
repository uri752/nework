package ru.netology.nework.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.netology.nework.auth.AppAuth
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {
    private val appScope = CoroutineScope(Dispatchers.Default)

    @Inject
    lateinit var appAuth: AppAuth

    override fun onCreate() {
        super.onCreate()
        setupAuth()
    }

    private fun setupAuth() {
        appScope.launch {
        }
    }
}