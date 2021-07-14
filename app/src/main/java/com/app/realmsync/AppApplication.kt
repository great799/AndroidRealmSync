package com.app.realmsync

import android.app.Application
import io.realm.Realm
import io.realm.mongodb.App
import io.realm.mongodb.AppConfiguration

const val USER_ID = "aman"
class AppApplication : Application() {
    private val appId = "applicationandroidrealm-qjvay" // Enter your own App Id here
    private lateinit var app: App


    companion object {
        private var instance: AppApplication? = null
        fun getInstance(): AppApplication {
            return instance!!
        }
    }

    fun getRealmApp(): App{
        return app
    }


    override fun onCreate() {
        super.onCreate()
        Realm.init(this)
        instance = this
        app = App(AppConfiguration.Builder(appId).build())
    }
}