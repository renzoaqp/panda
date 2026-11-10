package com.panbu.app

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/** Punto de entrada de la app. Hilt arma aquí el grafo de dependencias. */
@HiltAndroidApp
class PanbuApplication : Application()
