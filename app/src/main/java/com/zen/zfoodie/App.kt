package com.zen.zfoodie

import android.app.Application
import com.joanzapata.iconify.Iconify
import com.joanzapata.iconify.fonts.FontAwesomeModule
import com.yayandroid.locationmanager.LocationManager

class App : Application() {

	override fun onCreate() {
		super.onCreate()
		Iconify.with(FontAwesomeModule())
		LocationManager.enableLog(true)

	}


}