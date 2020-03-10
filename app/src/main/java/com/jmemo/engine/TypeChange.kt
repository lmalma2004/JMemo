package com.jmemo.engine

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.io.InputStream

fun inputStreamToByteArray(inputStream: InputStream): ByteArray{
    var resBytes = byteArrayOf()
    var bos = ByteArrayOutputStream()
    var buffer = ByteArray(1024)
    var read = -1
    try{
        read = inputStream.read(buffer)
        while(read != -1){
            bos.write(buffer, 0, read)
            read = inputStream.read(buffer)
        }
        resBytes = bos.toByteArray()
        bos.close()
    }
    catch (e: IOException){
        e.printStackTrace()
    }
    return resBytes
}

fun byteArrayToInputStream(byteArray: ByteArray): InputStream{
    return ByteArrayInputStream(byteArray)
}

fun bitmapToByteArray(bitmap: Bitmap): ByteArray{
    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    return bos.toByteArray()
}

fun byteArrayToBitmap(byteArray: ByteArray): Bitmap{
    return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
}