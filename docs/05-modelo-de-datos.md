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

### Preferencias (DataStore, Fase 1)

`userName`, `avatarUri`, `notificationsEnabled`, `restStart` (22:00), `restEnd` (06:00),
`reminderMinutesBefore` (30), `preferredSound`, `theme` (`SYSTEM`/`LIGHT`/`DARK`), `onboardingDone`,
`journalLockEnabled`, `pairedDeviceId` (Fase 5).

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
| **Tiempo contigo / tiempo de calma** | Suma de la duración de las `calm_sessions` del periodo. Las sesiones de menos de 30 s no cuentan |
| **Comparación (%)** | `(actual − anterior) / anterior × 100`, redondeado. Si `anterior = 0`, no se muestra |
| **"↑ 45 min que ayer"** | `tiempo de hoy − tiempo de ayer`, en minutos. No se muestra si ayer fue 0 |
| **Distribución de ánimo** | Registros de cada estado / total del periodo. Los porcentajes se ajustan para sumar 100 (método del mayor residuo) |
| **Estado más frecuente** | El de mayor cantidad; si hay empate, el más reciente |
| **Tiempo de pantalla nocturno** | Suma de intervalos con la pantalla encendida (`SCREEN_INTERACTIVE` → `SCREEN_NON_INTERACTIVE`, API 28+) dentro de la noche. En API 26–27: suma del tiempo de apps en primer plano (aproximado) |
| **Progreso de meta (%)** | `actual × 100 / objetivo`, entero, **redondeado hacia abajo**, entre 0 y 100 (7 h 10 min de 8 h = 89 %) |
| **Meta cumplida** | Metas normales: `actual ≥ objetivo`. Meta de límite (`NIGHT_SCREEN_LIMIT`): `actual ≤ objetivo` al terminar la noche |
| **Sueño** | `wake_at − bedtime_at` del `sleep_log` de esa noche |

## Migraciones

- **Antes de la primera publicación** se puede modificar la versión 1 del esquema (desinstalar y volver a
  instalar la app en los celulares de prueba).
- **Después de publicar:** cada cambio sube la versión y lleva una `Migration` con su test. Nunca se usa
  `fallbackToDestructiveMigration` en release, porque borraría el diario y el historial de la persona.
- Los esquemas exportados (`app/schemas/`) van al repositorio.
