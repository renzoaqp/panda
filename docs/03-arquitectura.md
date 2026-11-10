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
| Preferencias | DataStore Preferences ✅ APK 0.3 | Nombre, bienvenida, horario de descanso, recordatorio |
| Cuentas | Firebase Authentication — propuesto (APK 0.4) | Ver [ADR-0004](decisiones/0004-cuentas-de-usuario.md) |
| Recordatorios | AlarmManager con alarma inexacta ✅ APK 0.3 | Aviso diario para dejar el celular ([ADR-0005](decisiones/0005-recordatorios-con-alarmmanager.md)) |
| Tareas en segundo plano | WorkManager (Fase 2) | Resumen nocturno del tiempo de pantalla |
| Audio | Media3 ExoPlayer (APK 0.4) | Sonidos relajantes en bucle |
| Gráficos | Canvas de Compose ✅ APK 0.3 | Iguales al mockup, sin librerías extra |
| Cifrado | AES-256-GCM con clave en Android Keystore ✅ APK 0.3 | Diario privado ([ADR-0006](decisiones/0006-diario-cifrado.md)) |
| Pruebas | JUnit + corrutinas de prueba; Robolectric + Roborazzi para capturas | Reglas de cálculo y pantallas sin celular |
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
        │   │   ├── navigation/             Rutas, NavHost, barra inferior; AppViewModel decide
        │   │   │                           si se ve la bienvenida o las pestañas
        │   │   ├── ui/theme/               Colores, tipografía, formas, espaciado
        │   │   ├── ui/components/          Componentes reutilizables (PanbuCard, GoalRow, HelpLineCard,
        │   │   │   │                       SegmentedSelector, SettingsRow, TimePickerDialog…)
        │   │   │   └── charts/             Gráficos con Canvas: CalmBarChart, DonutChart, MinutesAxis
        │   │   ├── feature/                Una carpeta por pantalla: XScreen + XContent + XViewModel
        │   │   │   ├── onboarding/         Bienvenida (5 pasos) con el aviso y la línea de ayuda
        │   │   │   ├── home/               Inicio
        │   │   │   ├── stats/              Estadísticas (Semana / Mes / Total)
        │   │   │   ├── goals/              Metas
        │   │   │   ├── calm/               Panbu, tu espacio de calma
        │   │   │   ├── journal/            Mi diario emocional: lista y editor
        │   │   │   ├── breathing/          Respira con Panbu
        │   │   │   ├── moment/             Un momento con Panbu (temporizador)
        │   │   │   ├── shop/               Tienda
        │   │   │   └── settings/           Ajustes
        │   │   ├── domain/
        │   │   │   ├── model/              Modelos y reglas puras (Mood, Goal, CalmSession, StatsCalculator,
        │   │   │   │                       RestReminder, UserPreferences…)
        │   │   │   └── usecase/            Combinan repositorios: ObserveTodayProgress, ObserveStats
        │   │   ├── data/
        │   │   │   ├── crypto/             Cifrado del diario (AES-GCM, clave en Android Keystore)
        │   │   │   ├── local/              Room: base de datos, entidades, DAOs
        │   │   │   ├── preferences/        DataStore: nombre, bienvenida, horario de descanso, recordatorio
        │   │   │   ├── repository/         Repositorios (interfaz + implementación)
        │   │   │   ├── LocalDataEraser.kt  "Borrar todos mis datos"
        │   │   │   └── screentime/         Tiempo de pantalla nocturno (Fase 2)
        │   │   ├── reminders/              Recordatorio de descanso: AlarmManager, receptores y notificación
        │   │   ├── device/                 Peluche Panbu: interfaz + implementación vacía (BLE en Fase 5)
        │   │   └── di/                     Módulos de Hilt
        │   └── res/                        Textos (strings.xml), íconos, temas
        ├── debug/                          (Fase 0) DemoData: datos de ejemplo SOLO para debug
        └── test/java/com/panbu/app/        Tests unitarios
```

Carpetas que se agregarán más adelante: `data/account/` y `feature/account/` (cuentas, APK 0.4), `device/ble/` (Fase 5), `src/androidTest/` (Fase 4).

## Convenciones

- **Idioma:** código (clases, funciones, variables) en inglés; comentarios, documentación y textos de la app en
  español. Todo texto visible va en `res/values/strings.xml`.
- **Paquetes por funcionalidad** (`feature/x`), no por tipo de clase.
- **Pantallas:** `XScreen` obtiene el ViewModel con `hiltViewModel()` y llama a `XContent(uiState, callbacks)`,
  que no tiene estado y tiene `@Preview`.
- **Capturas de pantalla (Roborazzi):** cada `XContent` tiene su captura en `ScreensScreenshotTest`. Si la pantalla
  tiene una animación infinita, `XContent` recibe un parámetro para apagarla (si no, el test nunca termina).
- **Datos sensibles:** el texto del diario no va a `SavedStateHandle` ni a `rememberSaveable` (Android guarda ese
  estado fuera de la app y sin cifrar; ver ADR-0006). Tampoco a los logs.
- **Estado:** `data class XUiState` inmutable; el ViewModel lo expone como `StateFlow` con
  `stateIn(viewModelScope, WhileSubscribed(5_000), XUiState())`.
- **Fechas:** se guarda `Instant` (epoch millis en Room). Los "días" se calculan con `LocalDate` y el `Clock`
  inyectado (en los tests se usa `Clock.fixed`). Ver las reglas de cálculo en
  [05-modelo-de-datos.md](05-modelo-de-datos.md).
- **Enums en la base de datos:** se guardan por su `name`. Nunca se renombra un valor ya publicado.
- **Colores y medidas:** solo desde el tema (`MaterialTheme`, `PanbuTheme.extraColors`, `PanbuSpacing`).
- **Dependencias:** todas en `gradle/libs.versions.toml`.

## Entorno de desarrollo

**No hace falta Android Studio.** Se trabaja con VS Code (o cualquier editor) y la línea de comandos. Es la
opción recomendada para este equipo: consume mucha menos memoria y permite que Claude Code compile, corra los tests y
vea las pantallas.

| Herramienta | Para qué |
|---|---|
| JDK 21 | Ejecutar Gradle 8.14 (no funciona con el JDK 25) |
| Android SDK command-line tools en `~/Android/Sdk`, con `platform-tools`, `platforms;android-36` y `build-tools;36.0.0` | Compilar |
| `adb` (viene en `platform-tools`) | Instalar en el celular y leer los logs |
| `scrcpy` (opcional) | Ver y controlar la pantalla del celular desde la PC |
| VS Code + extensión de Kotlin | Editar |

Variables de entorno: `ANDROID_HOME=$HOME/Android/Sdk` y `JAVA_HOME=<ruta del JDK 21>`. También se puede poner
`sdk.dir` en `local.properties`, que no va al repositorio.

| Con Android Studio | Sin Android Studio |
|---|---|
| `@Preview` de Compose | Tests de captura de pantalla con Roborazzi: `./gradlew recordRoborazziDebug` genera un PNG de cada pantalla en `app/build/outputs/roborazzi/` |
| Autocompletado y refactors completos para Android | La extensión de Kotlin de VS Code es más limitada en Android; algunos errores solo aparecen al compilar |
| Depurador visual, Layout Inspector, Database Inspector | `adb logcat` y logs. Android Studio se puede instalar después solo para casos difíciles |
| Administrador de emuladores | Emulador por línea de comandos: `scripts/setup-emulator.sh` (Linux) o `scripts/setup-emulator-windows.ps1` crean el celular virtual "Panbu"; la tarea "Panbu: abrir emulador" lo abre. Para el Bluetooth del peluche hace falta un celular real |

### VS Code (Linux y Windows)

- `.vscode/tasks.json`: *Terminal → Run Task…* tiene "compilar APK" (Ctrl+Shift+B), "instalar en el celular",
  "tests" y "capturas de pantalla". Funcionan en los dos sistemas.
- `.vscode/settings.json` desactiva la importación de Gradle de la extensión de Java de Red Hat. Esa extensión no
  soporta Android y, con su Java 25, propone "actualizar" el wrapper a Gradle 9.x, lo que rompe la compilación.
  **Si VS Code ofrece actualizar Gradle, responde que no.**
- `.gitattributes` fija los finales de línea (LF en todo; CRLF en `.bat` y `.ps1`), así que no hace falta tocar
  `core.autocrlf`.

### Windows

**Paso 0 — tener Git y VS Code** (una sola vez, en PowerShell):
```powershell
winget install --id Git.Git -e
winget install --id Microsoft.VisualStudioCode -e
git config --global core.longpaths true   # las compilaciones de Android crean rutas muy largas
```

**Paso 1 — clonar el repositorio con VS Code**
1. Abre VS Code → `Ctrl+Shift+P` → escribe **Git: Clone** → pega `https://github.com/renzoaqp/panda.git`.
2. Elige una carpeta **corta y sin espacios**, por ejemplo `C:\dev` (queda `C:\dev\panda`). Las rutas largas
   como `C:\Users\Nombre\OneDrive\Documentos\...` causan errores de Gradle en Windows, y OneDrive además
   sincroniza las carpetas `build`.
3. Si el repo es privado, VS Code pide iniciar sesión en GitHub: acepta.
4. Cuando termine, presiona **Abrir**. Si pregunta *"¿Confías en los autores de los archivos?"*, responde **Sí**
   (sin eso, las tareas de compilación no funcionan).

**Paso 2 — instalar las herramientas:** en VS Code abre *Terminal → New Terminal* (PowerShell) y ejecuta:
`powershell -ExecutionPolicy Bypass -File scripts\setup-windows.ps1`
El script instala el JDK 21 (con `winget`) y el Android SDK en `%LOCALAPPDATA%\Android\Sdk` (verificando el
checksum), y configura `JAVA_HOME`, `ANDROID_HOME` y `adb` en el `PATH` del usuario.

**Paso 3 — compilar:** cierra y vuelve a abrir VS Code; luego `Ctrl+Shift+B` (o `.\gradlew.bat assembleDebug`). La
primera vez tarda unos minutos porque descarga Gradle y las librerías. El APK queda en
`app\build\outputs\apk\debug\app-debug.apk`.

**Paso 4 — celular:** activa "Depuración USB" y conéctalo. Si `adb devices` no lo muestra, instala el driver USB del
fabricante (el script ya instala el de Google para los Pixel). Luego, la tarea "Panbu: instalar en el celular".

**Para traer los cambios nuevos:** en VS Code, barra lateral de Git (`Ctrl+Shift+G`) → `…` → **Pull**, o el botón de
sincronizar en la barra inferior.

### Linux (Fedora)

JDK 21 en `~/.jdks/` (Temurin) y SDK en `~/Android/Sdk` (`local.properties` tiene `sdk.dir`).

**No hay que configurar nada más:** el Java del sistema es el 25 (con él Gradle falla con
`What went wrong: 25.0.4.1`), pero `scripts/gradlew.sh` busca solo un JDK 21 y llama a `./gradlew`.

- En VS Code: `Ctrl+Shift+B` (las tareas de Linux ya usan `scripts/gradlew.sh`).
- En la terminal (bash o PowerShell): `./scripts/gradlew.sh assembleDebug`.

## Comandos

```bash
export ANDROID_HOME=$HOME/Android/Sdk
export JAVA_HOME=/ruta/al/jdk-21

./gradlew assembleDebug          # Compila el APK de depuración
./gradlew installDebug           # Instala en el celular conectado por USB
./gradlew testDebugUnitTest      # Tests unitarios
./gradlew lintDebug              # Análisis estático
```

## Versiones

Las versiones de `gradle/libs.versions.toml` son un conjunto compatible conocido (AGP 8.11 · Kotlin 2.1.21 ·
KSP 2.1.21-2.0.1 · Compose BOM 2025.06.01 · Gradle 8.14.3). Para actualizarlas, trabaja en una rama aparte:
cambia las versiones en el catálogo (respetando la compatibilidad entre AGP, Gradle, Kotlin y KSP), compila,
corre los tests y prueba en un celular antes de unir los cambios.
DataStore ya se usa (APK 0.3). Las librerías de fases siguientes (WorkManager, Media3, Biometric) ya están
declaradas en el catálogo, pero se agregan a `app/build.gradle.kts` recién cuando se usan.
