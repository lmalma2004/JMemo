package com.jmemo.engine

import android.graphics.Bitmap
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.lang.Exception

fun saveBitmapToJpeg(bitmap: Bitmap, filePath: File, name: String){
    val file = File(filePath, name)
    try{
        val stream: OutputStream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.WEBP, 100, stream)
        stream.flush()
        stream.close()
    }catch (e: Exception){
        e.printStackTrace()
    }
}