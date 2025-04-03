package com.example.hazlens.workers

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.annotation.WorkerThread
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.ColorMatrixColorFilter
import androidx.compose.ui.graphics.Paint
import com.example.hazlens.OUTPUT_PATH
import okio.FileNotFoundException
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import androidx.core.graphics.scale
import androidx.core.graphics.createBitmap
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import kotlin.random.Random
import androidx.core.graphics.set
import androidx.core.graphics.get


private const val TAG = "WorkerUtils"


@WorkerThread
fun filterBitmap(bitmap: Bitmap, filterType: Int): Bitmap {
    val res = when(filterType) {
        1 -> bitmapDecay(bitmap)
        2 -> mutatedBlur(bitmap)
        3 -> pixelRot(bitmap)
        else -> bitmap
    }
    return res
}

    fun mutatedBlur(bitmap: Bitmap):Bitmap {
    val input = bitmap.scale(bitmap.width / 20, bitmap.height / 20)
    return input.scale(bitmap.width, bitmap.height)
}

fun pixelRot(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height
    val greyBitmap = createBitmap(width, height)
    val canvas = Canvas(greyBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix()
    colorMatrix.setToSaturation(0f)
    val filter = ColorMatrixColorFilter(colorMatrix)
    paint.colorFilter = filter
    canvas.drawBitmap(bitmap, 0f, 0f, paint.asFrameworkPaint())
    return greyBitmap
}

fun bitmapDecay(bitmap: Bitmap): Bitmap {
    val width = bitmap.width
    val height = bitmap.height

    val noisyBitmap = createBitmap(width, height)
    val canvas = Canvas(noisyBitmap)
    val paint = Paint()
    canvas.drawBitmap(bitmap, 0f, 0f, paint.asFrameworkPaint()) // Draw the original bitmap first

    val random = Random.Default

    for (x in 0 until width) {
        for (y in 0 until height) {
            val pixel = bitmap[x, y]
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            val noise = (random.nextFloat() - 0.5f) * 2 * 255 // Noise range: -noiseLevel * 255 to +noiseLevel * 255

            val noisyRed = (red + noise).coerceIn(0f, 255f).toInt()
            val noisyGreen = (green + noise).coerceIn(0f, 255f).toInt()
            val noisyBlue = (blue + noise).coerceIn(0f, 255f).toInt()

            noisyBitmap[x, y] = Color.rgb(noisyRed, noisyGreen, noisyBlue)
        }
    }

    return noisyBitmap
}
/*
   The Filtered Image (from filterBitmap() ) which is Bitmap is still a bitmap, the android needs to save an uri in a file,
   we are saving it in the directory OUTPUT_PATH (constant defined in Constants.kt)
 */
@OptIn(ExperimentalUuidApi::class)
@Throws(FileNotFoundException::class)
fun writeBitMapToFile(applicationContext: Context, bitmap: Bitmap): Uri {

    // UUID takes care to uniquely name each output Image saved
    val name = String.format("haz-filter-output-%s.png", Uuid.random().toString())

    // Output Directory (we are using the OUTPUT_PATH constant as the name)
    val outputDir = File(applicationContext.filesDir, OUTPUT_PATH)

    // check if it does not exist create one
    if (!outputDir.exists()) {
        outputDir.mkdirs()
    }

    // create the output file from (name)
    val outputFile = File(outputDir, name)
    var out: FileOutputStream? = null
    try {
        //creating the outputStream object from outputFile (Image)
        out = FileOutputStream(outputFile)
        //compression of the bitmap and passing it to the outputStream
        bitmap.compress(android.graphics.Bitmap.CompressFormat.PNG, 0, out)
    } finally {
        out?.let {
            try {
                it.close()
            } catch (e: IOException) {
                Log.e(TAG, e.message.toString())
            }
        }
    }
    // return the uri
    return Uri.fromFile(outputFile)
}