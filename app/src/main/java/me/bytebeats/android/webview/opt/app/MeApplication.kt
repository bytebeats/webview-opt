package me.bytebeats.android.webview.opt.app

import android.app.Application
import me.bytebeats.android.webview.opt.preloadWebViewService

/**
 * Created by bytebeats on 2022/3/8 : 17:40
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
class MeApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        preloadWebViewService(this)
    }
}