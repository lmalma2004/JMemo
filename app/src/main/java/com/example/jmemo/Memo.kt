package com.example.jmemo

import android.graphics.Bitmap
import android.net.Uri
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Image(
    var image: ByteArray = byteArrayOf(),
    var urlOfImage: String = ""
) : RealmObject(){
}

open class Memo(
    @PrimaryKey var id: Long = 0,
    var title: String = "",
    var date: Long = 0,
    var body: String = "",
    var images: RealmList<Image> = RealmList<Image>()
    //var images: ArrayList<Bitmap> = arrayListOf()
) : RealmObject(){
}