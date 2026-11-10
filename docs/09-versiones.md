# Versiones de Panbu

Qué trae cada APK, para el cliente y para quien lo prueba. Cómo probar cada versión: [manual, paso 7](08-manual-vscode.md#paso-7--instalar-la-app-en-un-celular).
Qué viene después: [plan de trabajo › Vía rápida](01-plan-de-trabajo.md#vía-rápida-prioridad-actual-un-apk-cuanto-antes).

> Todas las versiones son **de prueba** (APK de depuración). Ninguna está publicada en Google Play.

---

## APK 0.3 — 2026-11-10 · versión 0.3.0 (código 3)

**Se puede instalar encima del 0.2:** se conservan los estados de ánimo y los momentos de calma (la base de datos
pasa sola a la versión 2).

### Nuevo
- **Bienvenida** en 5 pasos: qué es Panbu, cómo acompaña, tu privacidad, tu nombre (opcional) y el aviso de que
  Panbu no sustituye a un profesional, con la línea de ayuda.
- **Estadísticas reales**: Semana, Mes y Total. Tiempo de calma con gráfico de barras y comparación con el mismo
  tramo del periodo anterior; dona "¿Cómo te sientes?" con el porcentaje de cada estado.
- **Mi diario emocional**: escribir, editar y borrar entradas, con un estado de ánimo opcional. El texto se guarda
  **cifrado** y nunca se analiza.
- **Recordatorio para dejar el celular**: un aviso al día, 15, 30 o 60 minutos antes del horario de descanso. Se
  activa en Ajustes; ahí se pide el permiso de notificaciones, con una explicación previa.
- **Ajustes reales**: tu nombre, horario de descanso, recordatorio, ayuda y **Borrar todos mis datos**.
- **"¿Necesitas hablar con alguien?"**: Línea 113 del Minsa, en la pestaña Panbu, en Ajustes y en la bienvenida.
- Inicio saluda por tu nombre.

### Por dentro
- 70 tests automáticos (eran 32): reglas de estadísticas, eje del gráfico, hora del recordatorio y diario cifrado.
- Decisiones nuevas: [ADR-0005](decisiones/0005-recordatorios-con-alarmmanager.md) (recordatorio con AlarmManager)
  y [ADR-0006](decisiones/0006-diario-cifrado.md) (diario cifrado).
- Permisos nuevos (los dos ya estaban en la tabla de [guardrails](06-guardrails.md#permisos)):
  `POST_NOTIFICATIONS` y `RECEIVE_BOOT_COMPLETED`.

### Probado en el emulador (Android 15)
Bienvenida completa; estadísticas con los datos del 0.2; recordatorio activado y recibido (llegó a las 21:31:58
para un aviso de las 21:30); reprogramación al reiniciar y al cambiar la hora; diario cifrado (el texto no aparece
en los archivos de la base de datos); "Borrar todos mis datos". Sin cierres inesperados.

### Pendiente del cliente
- Confirmar la ruta del menú de la Línea 113 ("opción 5" u "opción 3 → 5"; las fuentes no coinciden).

---

## APK 0.2 — 2026-11-10 · demo funcional

- Ilustración propia del panda abrazando un corazón.
- **Respira con Panbu**: 3 ritmos (suave 4-6, caja 4-4-4-4 y 4-7-8) y 1, 3 o 5 minutos; el panda crece al inhalar
  y se achica al exhalar.
- **"Necesito un momento de calma"**: un toque y empieza 1 minuto de respiración.
- **Un momento con Panbu**: temporizador que suma al "Tiempo contigo".
- **Inicio con datos reales**: tiempo de calma de hoy y comparación con ayer.
- **Metas iniciales** que avanzan solas: 3 momentos de calma y 2 respiraciones al día.
- 32 tests automáticos.

## APK 0.1 — 2026-11-09 · esqueleto

- Tema de Panbu (crema y lavanda), las 5 pestañas y la barra inferior.
- Registro del estado de ánimo en Inicio, guardado en el celular.
- Compilado en Linux y en Windows; probado en el emulador.
