package com.jmemo.engine.database

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

open class Memo(
    @PrimaryKey var id: Long = 0,
    var title: String = "",
    var lastDate: Long = 0,
    var initDate: Long = 0,
    var body: String = "",
    var youTubeVideoId: String = "",
    var images: RealmList<String> = RealmList<String>() //image는 경로 또는 URL
) : RealmObject(){
}