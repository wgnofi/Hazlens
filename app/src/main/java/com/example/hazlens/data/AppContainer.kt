package com.example.hazlens.data

import android.content.Context

interface AppContainer {
    val hazRepository: HazRepository
}

class DefaultAppContainer(context: Context): AppContainer {
    override val hazRepository = WorkManagerHazRepository(context = context)
}