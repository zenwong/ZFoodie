package com.zen.zfoodie

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule

class App : Application() {
	override fun onCreate() {
		super.onCreate()
		Iconify.with(FontAwesomeModule())
	}
}