package com.example.hazlens.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.hazlens.OUTPUT_PATH
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

private const val TAG = "CleanupWorker"

class CleanupWorker(ctx: Context, params: WorkerParameters): CoroutineWorker(ctx, params) {
    override suspend fun doWork(): Result {
        // Run in IO thread ( good for file based operations )
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)
                if (outputDir.exists()) {
                    val entries = outputDir.listFiles()
                    if (entries != null) {
                        for (entry in entries) {
                            val name = entry.name
                            if (name.isNotEmpty() && name.endsWith(".png")) {
                                val deleted = entry.delete()
                                Log.i(TAG, "Deleted $name - $deleted")
                            }
                        }
                    }
                }
                Result.success()
            } catch (e: Exception) {
                Log.e(
                    TAG,
                    "Error in CleaningWorker",
                    e
                )
                Result.failure()
            }
        }
    }
}