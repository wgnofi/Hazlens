package com.example.hazlens

import android.app.Application
import com.example.hazlens.data.AppContainer
import com.example.hazlens.data.DefaultAppContainer

class HazlensApplication: Application() {
    lateinit var container: AppContainer
    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)
    }
}