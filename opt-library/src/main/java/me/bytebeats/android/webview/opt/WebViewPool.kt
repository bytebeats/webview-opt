package me.bytebeats.android.webview.opt

import android.content.Context
import android.content.MutableContextWrapper
import android.os.Build
import android.view.ViewGroup
import android.webkit.WebView
import java.util.concurrent.LinkedBlockingQueue

/**
 * Created by bytebeats on 2022/3/8 : 16:51
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 *
 * To preload Chrome Service and recycle WebView.
 */

sealed interface WebViewPool {
    fun prepare(context: Context): WebView
    fun acquire(context: Context): WebView
    fun recycle(webView: WebView?)
    fun resume(webView: WebView?) {
        webView?.onResume()
        webView?.resumeTimers()
    }

    fun pause(webView: WebView?) {
        webView?.onPause()
        webView?.pauseTimers()
    }

    companion object {
        fun recycle(webView: WebView?, q: LinkedBlockingQueue<WebView>) {
            webView?.loadDataWithBaseURL(null, "", "text/html", "utf-8", null)
            webView?.clearHistory()
            webView?.parent?.let {
                (it as ViewGroup).removeView(webView)
            }
            webView?.destroy()
            if (webView?.context is MutableContextWrapper) {
                val base = webView.context as MutableContextWrapper
                base.baseContext = base.applicationContext
                q.offer(webView)
            }
        }
    }

    object POOL : WebViewPool {

        private val mWebViews = LinkedBlockingQueue<WebView>()

        override fun prepare(context: Context): WebView {
            synchronized(mWebViews) {
                if (mWebViews.isEmpty()) {
                    val instance = WebView(MutableContextWrapper(context.applicationContext))
                    mWebViews.offer(instance)
                }
                return mWebViews.poll()!!
            }
        }

        override fun acquire(context: Context): WebView {
            var instance = mWebViews.poll()
            if (instance == null) {
                instance = prepare(context)
            }
            val wrapper = instance.context as MutableContextWrapper
            // Android Lollipop 5.0 & 5.1
            wrapper.baseContext =
                if (Build.VERSION.SDK_INT in Build.VERSION_CODES.LOLLIPOP until Build.VERSION_CODES.M)
                    context.applicationContext
                else context
            return instance
        }

        override fun recycle(webView: WebView?) {
            Companion.recycle(webView, mWebViews)
        }
    }
}
