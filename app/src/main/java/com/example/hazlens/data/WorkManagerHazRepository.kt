package com.example.hazlens.data

import android.content.Context
import android.net.Uri
import androidx.lifecycle.asFlow
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import com.example.hazlens.IMAGE_MANIPULATION_WORK_NAME
import com.example.hazlens.KEY_IMAGE_URI
import com.example.hazlens.KEY_TYPE_LEVEL
import com.example.hazlens.TAG_OUTPUT
import com.example.hazlens.workers.CleanupWorker
import com.example.hazlens.workers.FilterWorker
import com.example.hazlens.workers.SaveImageToFileWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull

class WorkManagerHazRepository(context: Context): HazRepository {

    /*
    WorkManager API-
        WorkManager - The one who does the job (we have this below)
        WorkRequest - The request definer with constraints
        Worker - The work definer
     */

    //work Manager object
    private val workManager = WorkManager.getInstance(context)

    // We can get the work status by TAG_OUTPUT constant called at the saving file stage
    override val outputWorkInfo: Flow<WorkInfo> = workManager.getWorkInfosByTagLiveData(TAG_OUTPUT)
        .asFlow()
        .mapNotNull {
            if (it.isNotEmpty()) it.first() else null
        }

    /*
     this is the where the whole worker process happens, the combination of
        - Cleaning up temp files
        - Applying the filter ( by filter type from UI event)
        - Saving it to a permanent file
     */
    override fun applyFilter(filterType: Int, imageUri: Uri) {

        // constraints on which conditions the work should happen
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .build()

        /*
         the beginning of the unique work named under the constant IMAGE_MANIPULATION_WORK_NAME
         and we start with CleanupWorker
         */
        var continuation = workManager.beginUniqueWork(
            IMAGE_MANIPULATION_WORK_NAME,
            ExistingWorkPolicy.REPLACE,
            OneTimeWorkRequest.from(CleanupWorker::class.java)
        )

        // next the filter applier ( ye you know what it is)
        val filterBuilder = OneTimeWorkRequestBuilder<FilterWorker>()
        /*
         this is where we put the KEY_IMAGE and KEY_LEVEL as a Data object to the worker before building the filterWorker
         and the same is retrieved as inputData.getString() and inputData.getInt() in the worker classes except cleaner
         */
        filterBuilder.setInputData(giveDataForWorkRequest(filterType, imageUri))
        filterBuilder.setConstraints(constraints)
        continuation = continuation.then(filterBuilder.build())

        /*
        The TAG_OUTPUT is used above to get the status of the save work
         */
        val save = OneTimeWorkRequestBuilder<SaveImageToFileWorker>()
            .addTag(TAG_OUTPUT)
            .build()
        continuation = continuation.then(save)
        continuation.enqueue()
    }

    override fun cancelWork() {
        workManager.cancelUniqueWork(IMAGE_MANIPULATION_WORK_NAME)
    }
}
private fun giveDataForWorkRequest(filterType: Int, imageUri: Uri): Data {
    val builder = Data.Builder()
    // this is where you build and put those
    builder
        .putString(KEY_IMAGE_URI, imageUri.toString())
        .putInt(KEY_TYPE_LEVEL, filterType)

    //the data object
    return builder.build()
}
