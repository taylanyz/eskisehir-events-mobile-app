package com.eskisehir.eventapp

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for Hilt dependency injection setup.
 * Initializes Hilt at app startup.
 */
@HiltAndroidApp
class EskisehirEventsApp : Application()
