package com.example.hazlens.workers

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import androidx.core.net.toUri
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.example.hazlens.KEY_IMAGE_URI
import com.example.hazlens.KEY_TYPE_LEVEL
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private const val TAG = "FilterWorker"

class FilterWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        val resourceUri = inputData.getString(KEY_IMAGE_URI)
        val filterType = inputData.getInt(KEY_TYPE_LEVEL, 1)
        return withContext(Dispatchers.IO) {
            return@withContext try {
                require(!resourceUri.isNullOrBlank()) {
                    val errorMessage = "Empty URI"
                    Log.e(TAG, errorMessage)
                    errorMessage
                }

                // resolver object
                val resolver = applicationContext.contentResolver

                // take the png (Uri) open its input stream and decode to bitmap
                val picture = BitmapFactory.decodeStream(
                    resolver.openInputStream(resourceUri.toUri())
                )
                // the whole filtration process based on user option
                val output = filterBitmap(picture, filterType)

                // our function to take the returned bitmap and convert into a file url
                val outputUri = writeBitMapToFile(applicationContext, output)

                // data object to be returned as the part of Result.success() for this worker
                val outputData = workDataOf(KEY_IMAGE_URI to outputUri.toString())
                // as you see
                Result.success(outputData)
            } catch (throwable: Throwable) {
                Log.e(
                    TAG,
                    "Error on filtration process",
                    throwable
                )
                Result.failure()
            }
        }
    }
}