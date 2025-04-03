package com.example.hazlens.ui

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.work.WorkInfo
import com.example.hazlens.HazlensApplication
import com.example.hazlens.KEY_IMAGE_URI
import com.example.hazlens.data.FilterOptionData
import com.example.hazlens.data.HazRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

sealed interface HazUiState {
    object Default: HazUiState
    object Loading: HazUiState
    data class Complete(val outputUri: String): HazUiState
}

class HazViewModel(val hazRepository: HazRepository): ViewModel() {

    internal val filterType = FilterOptionData.filterType

    val hzUiState: StateFlow<HazUiState> = hazRepository.outputWorkInfo.map {
        info ->
        val outputImageUri = info.outputData.getString(KEY_IMAGE_URI)
        when {
            info.state.isFinished && !outputImageUri.isNullOrEmpty() -> {
                HazUiState.Complete(outputImageUri)
            }

            info.state == WorkInfo.State.CANCELLED -> {
                HazUiState.Default
            }

            else -> HazUiState.Loading
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = HazUiState.Default
    )

    fun applyFilter(filterType: Int, imageUri: String) {
        hazRepository.applyFilter(filterType, Uri.parse(imageUri))
    }

    fun cancelWork() {
        hazRepository.cancelWork()
    }


    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val hazRepository =
                    (this[APPLICATION_KEY] as HazlensApplication).container.hazRepository
                HazViewModel(hazRepository)
            }
        }
    }
}