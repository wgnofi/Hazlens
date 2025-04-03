package com.example.hazlens.data

import android.net.Uri
import androidx.work.WorkInfo
import kotlinx.coroutines.flow.Flow

interface HazRepository {
    val outputWorkInfo: Flow<WorkInfo>
    fun applyFilter(filterType: Int, imageUri: Uri)
    fun cancelWork()
}