package com.example.uimirror

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.uimirror.database.models.Person
import org.opencv.android.Utils
import org.opencv.core.CvType
import org.opencv.core.Mat
import java.io.ByteArrayOutputStream

fun matToByteArray(mat: Mat): ByteArray {
    val bmp = Bitmap.createBitmap(mat.cols(), mat.rows(), Bitmap.Config.ARGB_8888)
    Utils.matToBitmap(mat, bmp)
    val stream = ByteArrayOutputStream()
    bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

fun byteArrayToMat(data: ByteArray): Mat {
    val bmp = BitmapFactory.decodeByteArray(data, 0, data.size)
    val mat = Mat(bmp.height, bmp.width, CvType.CV_8UC4)
    Utils.bitmapToMat(bmp, mat)

    // Convert 4-channel Mat (BGRA) to 3-channel Mat (BGR)
    val matBGR = Mat()
    org.opencv.imgproc.Imgproc.cvtColor(mat, matBGR, org.opencv.imgproc.Imgproc.COLOR_BGRA2BGR)
    return matBGR
}
