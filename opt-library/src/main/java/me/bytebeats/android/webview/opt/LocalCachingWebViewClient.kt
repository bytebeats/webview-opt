package me.bytebeats.android.webview.opt

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.*

/**
 * Created by bytebeats on 2022/3/8 : 16:57
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
 */
open class LocalCachingWebViewClient : WebViewClient() {
    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? = super.shouldInterceptRequest(view, request)

    override fun shouldInterceptRequest(view: WebView?, url: String?): WebResourceResponse? {
        return if (view != null && !url.isNullOrEmpty()) {
            try {
                val lowerUrl = url.lowercase(Locale.ROOT)
                val data =
                    Glide.with(view).asBitmap().diskCacheStrategy(DiskCacheStrategy.ALL).load(url)
                        .submit().get()
//        if (lowerUrl.endsWith(IMAGE_SUFFIX_GIF)) {
//          val data = Glide.with(view).asGif().diskCacheStrategy(DiskCacheStrategy.ALL).load(url).submit().get()
//
//          val inputStream = convertToInputStream(data, Bitmap.CompressFormat.JPEG)
//          WebResourceResponse(IMAGE_MIMETYPE_GIF, Charsets.UTF_8.name(), inputStream)
//
//        } else
                if (lowerUrl.endsWith(IMAGE_SUFFIX_WEBP)) {

                    val inputStream = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                        convertToInputStream(data, Bitmap.CompressFormat.WEBP_LOSSY)
                    } else {
                        convertToInputStream(data, Bitmap.CompressFormat.WEBP)
                    }
                    WebResourceResponse(IMAGE_MIMETYPE_WEBP, Charsets.UTF_8.name(), inputStream)
                } else if (lowerUrl.endsWith(IMAGE_SUFFIX_PNG)) {
                    val inputStream = convertToInputStream(data, Bitmap.CompressFormat.PNG)
                    WebResourceResponse(IMAGE_MIMETYPE_PNG, Charsets.UTF_8.name(), inputStream)
                } else if (lowerUrl.endsWith(IMAGE_SUFFIX_JPG) || lowerUrl.endsWith(
                        IMAGE_SUFFIX_JPEG
                    )
                ) {
                    val inputStream = convertToInputStream(data, Bitmap.CompressFormat.JPEG)
                    WebResourceResponse(IMAGE_MIMETYPE_JPG, Charsets.UTF_8.name(), inputStream)
                } else {
                    super.shouldInterceptRequest(view, url)
                }
            } catch (ignore: Exception) {
                super.shouldInterceptRequest(view, url)
            }
        } else super.shouldInterceptRequest(view, url)
    }

    companion object {
        internal const val IMAGE_SUFFIX_JPG = ".jpg"
        internal const val IMAGE_SUFFIX_JPEG = ".jpeg"
        internal const val IMAGE_SUFFIX_PNG = ".png"
        internal const val IMAGE_SUFFIX_WEBP = ".webp"
        internal const val IMAGE_SUFFIX_GIF = ".gif"

        internal const val IMAGE_MIMETYPE_JPG = "image/jpg"
        internal const val IMAGE_MIMETYPE_PNG = "image/png"
        internal const val IMAGE_MIMETYPE_WEBP = "image/webp"
        internal const val IMAGE_MIMETYPE_GIF = "image/gif"

        internal fun convertToInputStream(
            bitmap: Bitmap,
            format: Bitmap.CompressFormat
        ): InputStream {
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(format, 90, outputStream)
            val data = outputStream.toByteArray()
            return ByteArrayInputStream(data)
        }

        internal fun convertToInputStream(
            drawable: Drawable,
            format: Bitmap.CompressFormat
        ): InputStream {
            val outputStream = ByteArrayOutputStream()
            val bitmap = (drawable as BitmapDrawable).bitmap
            bitmap.compress(format, 90, outputStream)
            val data = outputStream.toByteArray()
            return ByteArrayInputStream(data)
        }
    }
}
