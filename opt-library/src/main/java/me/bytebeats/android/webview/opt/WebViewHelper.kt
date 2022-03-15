package me.bytebeats.android.webview.opt

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build

/**
 * Created by bytebeats on 2022/3/8 : 17:26
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */

/**
 * 使用复用池对WebView做优化，是一个优化点，但并不能从根本上解决问题，
 * 复用池也就是仅仅是对WebView的渲染着了优化，所以并不能从根本解决这个性能问题，而且这种方式不好控制，容易造成内存泄漏，
 * 为了解决问题而引入其他问题，这是得不偿失的，
 * 经过大量阅读网上的文章和阅读WebView的源码，发现WebView首次加载慢的原因就是，
 * 在启动过过程中，要启动chrome相关的服务，而这些过程是相当耗时的，
 * 所以我们的优化点就是，是否可以在应用启动时，提前启动chrome的服务？答案是可以的，
 * 在这之后我看了滴滴的Booster的源码，他们优化方案也是通过提前启动chrome的服务
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
