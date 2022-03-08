package me.bytebeats.android.webview.opt

import java.lang.reflect.Method

/**
 * Created by bytebeats on 2022/3/8 : 17:03
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

/**
 * obtain Method from specific Class, static or non-static
 * @param klass the specific class
 * @param name the method name
 * @param parameterTypes the method parameter types
 */
internal fun getMethod(klass: Class<*>, name: String, parameterTypes: Array<Class<*>>): Method? =
    try {
        klass.getDeclaredMethod(name, *parameterTypes)
    } catch (ignore: NoSuchMethodException) {
        val parent = klass.superclass
        if (parent == null) null
        else getMethod(parent, name, parameterTypes)
    }

/**
 * invoke non-static Method from specific object
 * @param obj the specific object
 * @param name the method name
 * @param parameterTypes the method parameter types
 */
fun <T> invokeMethod(
    obj: Any,
    name: String,
    parameterTypes: Array<Class<*>>,
    args: Array<Any>
): T? = if (parameterTypes.size != args.size) null
else getMethod(obj.javaClass, name, parameterTypes)?.run {
    isAccessible = true
    invoke(obj, *args) as? T
}

/**
 * invoke non-static Method from specific object
 * @param obj the specific object
 * @param name the method name
 */
fun <T> invokeMethod(
    obj: Any,
    name: String,
): T? = invokeMethod(obj, name, emptyArray(), emptyArray())

/**
 * invoke static Method from specific Class
 * @param klass the specific Class
 * @param name the method name
 * @param parameterTypes the method parameter types
 * @param args the method arguments
 */
fun <T> invokeStaticMethod(
    klass: Class<*>,
    name: String,
    parameterTypes: Array<Class<*>>,
    args: Array<Any>
): T? = if (parameterTypes.size != args.size) null
else getMethod(klass, name, parameterTypes)?.run {
    isAccessible = true
    invoke(klass, *args) as? T
}

fun <T> invokeStaticMethod(klass: Class<*>, name: String): T? =
    invokeStaticMethod(klass, name, emptyArray(), emptyArray())