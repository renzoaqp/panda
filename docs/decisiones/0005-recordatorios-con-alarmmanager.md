# ADR-0005: Recordatorio de descanso con AlarmManager (alarma inexacta)

- **Estado:** Aceptada
- **Fecha:** 2026-11-10

## Contexto

El APK 0.3 trae el "Recordatorio para dejar el celular" (docs/02-producto.md §6): una notificación al día, unos
minutos antes del horario de descanso (por defecto 21:30 para un descanso que empieza a las 22:00).

Restricciones:

- Tiene que llegar **a una hora del día** concreta, no "en algún momento".
- Unos minutos de diferencia no importan; no hace falta una alarma exacta.
- Guardrail 6 (docs/06-guardrails.md): solo los permisos de la tabla. `SCHEDULE_EXACT_ALARM` y `USE_EXACT_ALARM`
  están descartados de forma explícita.
- Guardrail 4: como máximo 2 notificaciones al día, sin presión, y nunca con la tienda.
- Android borra las alarmas al reiniciar el celular.

## Decisión

Usar **`AlarmManager.setWindow(RTC_WAKEUP, hora, 10 min)`**, una alarma inexacta con una ventana de 10 minutos,
más un `BroadcastReceiver` que muestra la notificación y programa la del día siguiente. Otro receptor vuelve a
programarla al reiniciar el celular, al actualizar la app y al cambiar la hora o la zona horaria.

Archivos: `reminders/ReminderScheduler.kt`, `reminders/ReminderReceivers.kt`, `reminders/ReminderNotifier.kt` y la
regla de la hora en `domain/model/RestReminder.kt` (con tests).

## Consecuencias

- **Sin librerías nuevas.** Solo hacen falta dos permisos que ya estaban en la tabla: `POST_NOTIFICATIONS` (se
  pide al activar el recordatorio en Ajustes) y `RECEIVE_BOOT_COMPLETED` (normal, no se pide).
- El aviso llega entre la hora elegida y 10 minutos después. Con el celular en reposo profundo (Doze) puede
  atrasarse un poco más; a esa hora el celular suele estar en uso, que es justo cuando sirve el aviso.
- El recordatorio está **apagado por defecto**: la persona lo activa en Ajustes. Así el permiso se pide en
  contexto, nunca al abrir la app.
- Cada vez que cambia el horario, los minutos de anticipación o el interruptor, Ajustes llama a
  `ReminderScheduler.sync()`, que programa o cancela. "Borrar todos mis datos" también lo cancela.
- Si se agrega otra notificación (por ejemplo, mensajes positivos), hay que respetar el máximo de 2 al día entre
  todas.

## Alternativas consideradas

- **WorkManager con una tarea periódica de 24 h:** está pensado para trabajo diferible, no para una hora del
  día; se va corriendo con el tiempo y agrega permisos al manifest (`WAKE_LOCK`, `ACCESS_NETWORK_STATE`,
  `FOREGROUND_SERVICE`) que no están en la tabla. Se usará en la Fase 2 para el resumen nocturno del tiempo de
  pantalla, que sí es diferible.
- **Alarma exacta (`setExactAndAllowWhileIdle`):** necesita `SCHEDULE_EXACT_ALARM`, que Android 14 ya no concede
  por defecto y que Google Play limita a apps de reloj o calendario. No hace falta tanta precisión.
- **`@AndroidEntryPoint` en los receptores:** en Kotlin no se puede llamar a `super.onReceive` (es abstracto) y
  Hilt lo necesita para inyectar. Se usa un `@EntryPoint` (`ReminderEntryPoint`).
