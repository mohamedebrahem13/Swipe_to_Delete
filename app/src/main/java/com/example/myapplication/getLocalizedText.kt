package com.example.myapplication

import android.content.Context
import android.content.res.Configuration
import androidx.annotation.StringRes
import java.util.Locale

fun getLocalizedText(context: Context, @StringRes resId: Int, locale: Locale): String {
    val config = Configuration(context.resources.configuration)
    config.setLocale(locale)
    val localizedContext = context.createConfigurationContext(config)
    return localizedContext.resources.getString(resId)
}