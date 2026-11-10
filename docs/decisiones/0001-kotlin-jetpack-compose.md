# ADR-0001: App nativa en Kotlin con Jetpack Compose

- **Estado:** Aceptada (se revisa si se decide hacer también la versión de iPhone, decisión pendiente #1)
- **Fecha:** 2026-11-09

## Contexto
Panbu es para Android. Tiene dos funciones que dependen mucho del sistema: el tiempo de pantalla nocturno
(`UsageStatsManager`, que solo existe en Android) y la conexión Bluetooth LE con el peluche (Fase 5). La
interfaz tiene que verse igual al mockup.

## Decisión
App nativa en **Kotlin** con **Jetpack Compose** (Material 3 con tema propio), MVVM, Hilt y Room.

## Consecuencias
- Acceso directo y estable a las APIs de BLE, uso de apps, notificaciones y Health Connect.
- Es la tecnología recomendada por Google, con la mejor documentación y soporte en Android Studio.
- Si después se quiere iPhone, habría que compartir la lógica con **Kotlin Multiplatform** (dominio y datos) y
  hacer la interfaz de iOS, o reescribir en otra tecnología.

## Alternativas consideradas
- **Flutter:** una sola base de código para Android e iOS, pero BLE y el uso de apps dependen de plugins de
  terceros, que es justo donde más problemas suele haber. Sería la opción si se decide salir en iPhone pronto.
- **React Native:** mismas desventajas que Flutter para BLE y, además, un rendimiento menor en animaciones.
- **Solo un mockup (Figma):** no sirve para validar las funciones reales; se prefirió un prototipo programado
  que se convierte en la app.
