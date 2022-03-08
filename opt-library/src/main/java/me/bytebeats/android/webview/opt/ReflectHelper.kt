package me.bytebeats.android.webview.opt

import java.lang.reflect.Method

/**
 * Created by bytebeats on 2022/3/8 : 17:03
 * E-mail: happychinapc@gmail.com
 * Quote: Peasant. Educated. Worker
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