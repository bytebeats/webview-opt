package me.bytebeats.android.webview.opt

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build

/**
 * Created by bytebeats on 2022/3/8 : 17:26
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

internal const val WEBVIEW_FACTORY_CLASS_NAME = "android.webkit.WebViewFactory"
internal const val WEBVIEW_FACTORY_METHOD_NAME = "getProvider"

/**
 * preload Webview Chromium Service when main thread is idle
 * @param application app's Application
 */
fun preloadWebViewService(application: Application) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        application.mainLooper.queue.addIdleHandler {
            startChromiumEngine()
            true
        }
    }
}

/**
 * add SuppressLint to remove lint warning "Accessing internal APIs via reflection is not supported and may not work on all devices or in the future"
 */
@SuppressLint("PrivateApi")
internal fun startChromiumEngine() {
    try {
        val klass = Class.forName(WEBVIEW_FACTORY_CLASS_NAME)
        invokeStaticMethod<Any>(klass, WEBVIEW_FACTORY_METHOD_NAME)?.run {
            invokeMethod<Any>(this, "startYourEngines", arrayOf(Boolean::class.java), arrayOf(true))
        }
    } catch (ignore: Exception) {
        // do nothing here
    }
}