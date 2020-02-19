package com.example.jmemo

import android.content.ContextWrapper
import android.graphics.Bitmap
import android.net.Uri
import android.os.Environment
import com.bumptech.glide.load.engine.cache.DiskLruCacheFactory
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.lang.Exception
import java.net.URI

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