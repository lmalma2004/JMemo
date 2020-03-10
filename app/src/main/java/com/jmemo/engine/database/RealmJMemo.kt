package com.jmemo.engine.database

import android.app.Application
import io.realm.Realm

class RealmJMemo : Application(){
    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
    }
}