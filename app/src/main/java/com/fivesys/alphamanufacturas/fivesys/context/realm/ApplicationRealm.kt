package com.fivesys.alphamanufacturas.fivesys.context.realm

import android.app.Application
import android.os.Build
import android.support.annotation.RequiresApi
import io.realm.Realm
import io.realm.RealmConfiguration

class ApplicationRealm : Application() {

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onCreate() {
        super.onCreate()

        Realm.init(applicationContext)

        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build()
        Realm.setDefaultConfiguration(config)
    }
}