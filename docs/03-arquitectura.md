# Arquitectura

## Tecnología

| Capa | Herramienta | Por qué |
|---|---|---|
| Lenguaje | Kotlin | Lenguaje oficial de Android ([ADR-0001](decisiones/0001-kotlin-jetpack-compose.md)) |
| Interfaz | Jetpack Compose + Material 3 con tema propio | Permite reproducir el mockup con exactitud |
| Arquitectura | MVVM: ViewModel + `StateFlow` | Estándar de Android, fácil de probar |
| Inyección de dependencias | Hilt | Estándar de Android; permite cambiar implementaciones (p. ej., el peluche) |
| Base de datos | Room (SQLite) | Datos en el celular ([ADR-0002](decisiones/0002-datos-locales-primero.md)) |
| Navegación | Navigation Compose con rutas tipadas (`@Serializable`) | Seguridad de tipos |
| Preferencias | DataStore (Fase 1) | Ajustes de la persona |
| Tareas programadas | WorkManager / AlarmManager (Fase 1–2) | Recordatorios y resumen nocturno |
| Audio | Media3 ExoPlayer (Fase 1) | Sonidos relajantes en bucle |
| Gráficos | Canvas de Compose (Fase 2) | Iguales al mockup, sin librerías extra |
| Bluetooth | API BLE de Android + CompanionDeviceManager (Fase 5) | Ver [07-bluetooth.md](07-bluetooth.md) |

**SDK:** `minSdk 26` (Android 8.0, ~97 % de celulares) · `targetSdk 36` · `compileSdk 36`.

## Capas y flujo de datos

```
┌──────────────────────────┐
│ UI (Compose)             │  XScreen (con estado) → XContent (sin estado, con @Preview)
└────────────┬─────────────┘
             │ uiState (StateFlow) ↑   eventos onX() ↓
┌────────────▼─────────────┐
│ ViewModel (@HiltViewModel)│  combina repositorios, arma XUiState
└────────────┬─────────────┘
             │
┌────────────▼─────────────┐
│ Repositorio (interfaz)   │  MoodRepository, … (implementación RoomXRepository)
└────────────┬─────────────┘
             │
┌────────────▼──────────────────────────────────────────────┐
│ Fuentes: Room · DataStore · UsageStatsManager · PanbuDevice│
└───────────────────────────────────────────────────────────┘
```

- La UI **nunca** habla directo con Room, DataStore ni Bluetooth.
- Los modelos de `domain/model` son Kotlin puro (sin Android), así se prueban con tests unitarios simples.
- Las entidades de Room (`data/local`) son distintas de los modelos de dominio y se convierten con
  `toDomain()` / `toEntity()`.

## Estructura de carpetas

```
panda/
├── CLAUDE.md                     Contexto y reglas para Claude Code
├── README.md
├── 00info/                       Material original del cliente (no editar)
├── docs/                         Documentación del proyecto
├── .claude/skills/               Skills del proyecto para Claude Code
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradlew, gradlew.bat          Gradle wrapper (8.14.3)
├── gradle/
│   ├── libs.versions.toml        Versiones de todas las dependencias
│   └── wrapper/
└── app/
    ├── build.gradle.kts
    ├── proguard-rules.pro
    ├── schemas/                  Esquemas de Room (se generan al compilar; van al repo)
    └── src/
        ├── main/
        │   ├── AndroidManifest.xml
        │   ├── java/com/panbu/app/
        │   │   ├── PanbuApplication.kt     Punto de entrada (Hilt)
        │   │   ├── MainActivity.kt
        │   │   ├── navigation/             Rutas, NavHost, barra inferior
        │   │   ├── ui/theme/               Colores, tipografía, formas, espaciado
        │   │   ├── ui/components/          Componentes reutilizables (PanbuCard, PanbuProgressBar…)
        │   │   ├── feature/
        │   │   │   ├── home/               Inicio
        │   │   │   ├── stats/              Estadísticas
        │   │   │   ├── goals/              Metas
        │   │   │   ├── calm/               Panbu, tu espacio de calma (respirar, diario, sonidos…)
        │   │   │   ├── shop/               Tienda
        │   │   │   └── settings/           Ajustes
        │   │   ├── domain/model/           Modelos puros (Mood, Goal, CalmSession…)
        │   │   ├── data/
        │   │   │   ├── local/              Room: base de datos, entidades, DAOs
        │   │   │   ├── repository/         Repositorios (interfaz + implementación)
        │   │   │   └── screentime/         Tiempo de pantalla nocturno (Fase 2)
        │   │   ├── device/                 Peluche Panbu: interfaz + implementación vacía (BLE en Fase 5)
        │   │   └── di/                     Módulos de Hilt
        │   └── res/                        Textos (strings.xml), íconos, temas
        ├── debug/                          (Fase 0) DemoData: datos de ejemplo SOLO para debug
        └── test/java/com/panbu/app/        Tests unitarios
```

Carpetas que se agregarán más adelante: `notifications/` (Fase 1), `data/preferences/` (DataStore, Fase 1),
`device/ble/` (Fase 5), `src/androidTest/` (Fase 4).

## Convenciones

- **Idioma:** código (clases, funciones, variables) en inglés; comentarios, documentación y textos de la app en
  español. Todo texto visible va en `res/values/strings.xml`.
- **Paquetes por funcionalidad** (`feature/x`), no por tipo de clase.
- **Pantallas:** `XScreen` obtiene el ViewModel con `hiltViewModel()` y llama a `XContent(uiState, callbacks)`,
  que no tiene estado y tiene `@Preview`.
- **Estado:** `data class XUiState` inmutable; el ViewModel lo expone como `StateFlow` con
  `stateIn(viewModelScope, WhileSubscribed(5_000), XUiState())`.
- **Fechas:** se guarda `Instant` (epoch millis en Room). Los "días" se calculan con `LocalDate` y el `Clock`
  inyectado (en los tests se usa `Clock.fixed`). Ver las reglas de cálculo en
  [05-modelo-de-datos.md](05-modelo-de-datos.md).
- **Enums en la base de datos:** se guardan por su `name`. Nunca se renombra un valor ya publicado.
- **Colores y medidas:** solo desde el tema (`MaterialTheme`, `PanbuTheme.extraColors`, `PanbuSpacing`).
- **Dependencias:** todas en `gradle/libs.versions.toml`.

## Comandos

Gradle 8.14 necesita **JDK 17 o 21**. Android Studio trae uno (JBR 21); el Java 25 del sistema no sirve
para Gradle 8.14.

```bash
# Desde la terminal (Linux), usando el JDK de Android Studio:
export JAVA_HOME=/ruta/a/android-studio/jbr

./gradlew assembleDebug          # Compila el APK de depuración
./gradlew installDebug           # Instala en el celular conectado por USB
./gradlew testDebugUnitTest      # Tests unitarios
./gradlew lintDebug              # Análisis estático
```

## Versiones

Las versiones de `gradle/libs.versions.toml` son un conjunto compatible conocido (AGP 8.11 · Kotlin 2.1.21 ·
KSP 2.1.21-2.0.1 · Compose BOM 2025.06.01 · Gradle 8.14.3). Para actualizarlas, usa el *AGP Upgrade Assistant*
de Android Studio en una rama aparte, compila, corre los tests y prueba en un celular antes de unir los cambios.
Las librerías que se usarán en fases siguientes (DataStore, WorkManager, Media3, Biometric) ya están declaradas
en el catálogo, pero se agregan a `app/build.gradle.kts` recién cuando se usan.
