package com.jmemo.engine.database

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration

class RealmJMemo : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        //val config = RealmConfiguration.Builder()
        //    .deleteRealmIfMigrationNeeded()
        //    .build()
    }
}