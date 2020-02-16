package com.example.jmemo

import android.graphics.Bitmap
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Memo(
    @PrimaryKey var id: Long = 0,
    var title: String = "",
    var date: Long = 0,
    var body: String = ""
    //var images: ArrayList<Bitmap> = arrayListOf()
) : RealmObject(){

}