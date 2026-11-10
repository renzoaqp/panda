# Modelo de datos

Todo se guarda **en el celular** ([ADR-0002](decisiones/0002-datos-locales-primero.md)). Base de datos Room
`panbu.db`; preferencias en DataStore.

## Tablas

### Ya en el esqueleto (versión 1 del esquema)

**`mood_entries`** — cada vez que la persona dice cómo se siente

| Columna | Tipo | Nota |
|---|---|---|
| `id` | Long (PK, autoincremental) | |
| `mood` | String | `CALM`, `ANXIOUS`, `SAD`, `STRESSED`, `HAPPY` |
| `note` | String? | Nota opcional |
| `created_at` | Long (epoch ms) | Indexada |

**`calm_sessions`** — tiempo dedicado a una actividad de calma

| Columna | Tipo | Nota |
|---|---|---|
| `id` | Long (PK) | |
| `type` | String | `BREATHING`, `MINDFULNESS`, `SOUNDS`, `MOMENT_WITH_PANBU`, `REST_ROUTINE` |
| `source` | String | `MANUAL` (app) o `DEVICE` (peluche, Fase 5) |
| `started_at` | Long | Indexada |
| `ended_at` | Long | |

**`goals`** — metas de la persona

| Columna | Tipo | Nota |
|---|---|---|
| `id` | Long (PK) | |
| `type` | String | `NIGHT_SCREEN_LIMIT`, `CALM_MOMENTS`, `MINDFULNESS`, `SLEEP`, `CUSTOM` |
| `title` | String | |
| `target` | Int | En la unidad indicada |
| `unit` | String | `MINUTES` o `TIMES` |
| `period` | String | `DAILY` o `WEEKLY` |
| `is_active` | Boolean | `false` = archivada (sus registros se conservan) |
| `created_at` | Long | |

### Planificadas

| Tabla | Fase | Campos principales |
|---|---|---|
| `goal_check_ins` | 1 | `goal_id`, `date`, `value` — avance manual de metas personalizadas |
| `sleep_logs` | 1 | `night_of` (fecha), `bedtime_at`, `wake_at`, `source` (`MANUAL`, `ROUTINE`, `HEALTH_CONNECT`) |
| `journal_entries` | 1 | `created_at`, `updated_at`, `mood?`, `text_encrypted`, `iv` — texto cifrado con AES-GCM y clave en Android Keystore |
| `night_screen_time` | 2 | `night_of`, `minutes`, `computed_at` — resumen diario (Android borra los eventos de uso a los pocos días) |

### Preferencias (DataStore)

Archivo `panbu_prefs` (DataStore Preferences), en `data/preferences/PreferencesRepository.kt`. Modelo:
`domain/model/UserPreferences.kt`. **Los nombres de las claves no se cambian después de publicar** (se perderían
las preferencias guardadas).

| Clave | Tipo | Por defecto | Uso | Estado |
|---|---|---|---|---|
| `userName` | String | (sin valor) | Saludo "Hola, Ana". Se guarda sin espacios al inicio ni al final y con máximo 30 caracteres; vacío = se borra la clave | ✅ APK 0.3 |
| `onboardingDone` | Boolean | `false` | `false` → la app muestra la bienvenida | ✅ APK 0.3 |
| `notificationsEnabled` | Boolean | `false` | Recordatorio para dejar el celular | ✅ APK 0.3 |
| `restStart` | Int (minutos desde las 00:00) | 1320 (22:00) | Inicio del horario de descanso | ✅ APK 0.3 |
| `restEnd` | Int (minutos desde las 00:00) | 360 (06:00) | Fin del horario de descanso | ✅ APK 0.3 |
| `reminderMinutesBefore` | Int | 30 | Anticipación del recordatorio: 15, 30 o 60 | ✅ APK 0.3 |
| `avatarUri`, `preferredSound`, `theme` (`SYSTEM`/`LIGHT`/`DARK`), `journalLockEnabled`, `pairedDeviceId` | — | — | Foto, sonido, tema, bloqueo del diario, peluche (Fase 5) | Pendiente |

"Borrar todos mis datos" (`data/LocalDataEraser.kt`) cancela el recordatorio, vacía todas las tablas de Room,
vuelve a crear las metas iniciales y borra todas las preferencias (lo que muestra otra vez la bienvenida).

### Tienda (Fase 3)

Catálogo en `assets/catalog.json` (producto, colores, accesorios, precios en céntimos, texto de envío). El
carrito vive en memoria y en DataStore. No se guardan datos de pago en el celular.

## Reglas de cálculo

Estas reglas son la "verdad" de cada número que ve la persona. Toda regla tiene un test unitario.

| Métrica | Regla |
|---|---|
| **Día** | De medianoche a medianoche, en la zona horaria del celular |
| **Noche** | El horario de descanso (por defecto 22:00–06:00). Puede cruzar la medianoche; se asigna a la fecha en que empieza |
| **Semana** | De lunes a domingo |
| **Mes** | Mes calendario (del 1 al último día) |
| **Total** | Desde el día del primer registro (sesión de calma o estado de ánimo, lo que sea más antiguo) hasta hoy. Sin registros, solo hoy |
| **A qué día pertenece un registro** | Al día local en que **empieza** (una sesión de 23:50 a 00:10 cuenta para el día anterior) |
| **Tiempo contigo / tiempo de calma** | Suma de la duración de las `calm_sessions` del periodo. Las sesiones de menos de 30 s no cuentan |
| **Comparación (%)** | `(actual − anterior) / anterior × 100`, redondeado al entero más cercano. "Anterior" es **el mismo tramo** del periodo anterior: en Semana, del lunes pasado al mismo día de la semana que hoy; en Mes, del día 1 del mes pasado al mismo número de día (o a su último día si es más corto: el 31 de marzo se compara con el 1–28 de febrero). No se muestra en Total, ni si actual = 0 o anterior = 0 |
| **"↑ 45 min que ayer"** | `tiempo de hoy − tiempo de ayer`, en minutos. No se muestra si ayer fue 0 |
| **Barras de Estadísticas** | Semana: 7 barras (un día cada una). Mes: semanas de lunes a domingo recortadas al mes (noviembre de 2026: 1, 2–8, 9–15, 16–22, 23–29, 30). Total: un mes por barra, como máximo los últimos 12 |
| **Eje del gráfico** | De 0 a un múltiplo de un paso "redondo" (1, 2, 5, 10, 15, 30 min, 1, 2, 3, 4, 6, 8, 12, 24 h), el más pequeño que deja como máximo 4 divisiones. 3 h 30 min → 0–4 h de 1 en 1 |
| **Distribución de ánimo** | Registros de cada estado / total del periodo. Los porcentajes se ajustan para sumar 100 (método del mayor residuo: cada estado recibe la parte entera y los puntos que faltan van a los residuos más grandes; si empatan, al de más registros y luego al más reciente). 3 estados con 1 registro cada uno → 34 % (el más reciente), 33 %, 33 % |
| **Estado más frecuente** | El de mayor cantidad; si hay empate, el elegido más recientemente |
| **Tiempo de pantalla nocturno** | Suma de intervalos con la pantalla encendida (`SCREEN_INTERACTIVE` → `SCREEN_NON_INTERACTIVE`, API 28+) dentro de la noche. En API 26–27: suma del tiempo de apps en primer plano (aproximado) |
| **Meta "Momentos de calma"** | Cantidad de sesiones de hoy que cuentan (≥ 30 s), de cualquier tipo |
| **Meta "Respiración y mindfulness"** | Cantidad de sesiones de hoy que cuentan, de tipo `BREATHING` o `MINDFULNESS` |
| **Metas sin datos** | Sueño, celular de noche y personalizadas no muestran progreso hasta que existan sus datos (nunca un 0 % inventado) |
| **Metas iniciales** | Si la tabla `goals` está vacía (sin contar archivadas), se crean las de `DefaultGoals`; su nombre visible sale de `strings.xml` según el tipo |
| **Un momento con Panbu** | Se guarda como `MOMENT_WITH_PANBU`; se corta solo a las 2 h para que un temporizador olvidado no infle los datos |
| **Salir a mitad de una actividad** | La sesión se guarda igual hasta ese momento (si dura ≥ 30 s) |
| **Progreso de meta (%)** | `actual × 100 / objetivo`, entero, **redondeado hacia abajo**, entre 0 y 100 (7 h 10 min de 8 h = 89 %) |
| **Meta cumplida** | Metas normales: `actual ≥ objetivo`. Meta de límite (`NIGHT_SCREEN_LIMIT`): `actual ≤ objetivo` al terminar la noche |
| **Sueño** | `wake_at − bedtime_at` del `sleep_log` de esa noche |
| **Hora del recordatorio de descanso** | `restStart − reminderMinutesBefore` (si da negativo, es del día anterior: 00:30 − 60 min = 23:30). Si esa hora de hoy ya pasó (o es justo ahora), el próximo es mañana. Llega con un margen de hasta 10 min (ADR-0005) |

## Migraciones

- **Antes de la primera publicación** se puede modificar la versión 1 del esquema (desinstalar y volver a
  instalar la app en los celulares de prueba).
- **Después de publicar:** cada cambio sube la versión y lleva una `Migration` con su test. Nunca se usa
  `fallbackToDestructiveMigration` en release, porque borraría el diario y el historial de la persona.
- Los esquemas exportados (`app/schemas/`) van al repositorio.
