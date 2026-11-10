# Panbu — contexto para Claude

App **Android** de bienestar emocional que acompaña al peluche físico **Panbu** ("Tu calma, siempre contigo").
Ayuda a crear momentos de calma, registrar cómo se siente la persona, cumplir metas pequeñas (menos celular de
noche, respiración, dormir mejor) y comprar el peluche. En la fase final, la app se conecta al peluche por
**Bluetooth LE**. Lo que la diferencia es la unión de un producto físico con una experiencia digital.

- **Fuente de verdad del producto:** `00info/` (mockup + guía PDF del cliente; **no editar**) y `docs/`.
- **Plan y estado:** `docs/01-plan-de-trabajo.md`. **Hecho: APK 0.1, 0.2 y 0.3. Sigue: APK 0.4** (cuentas,
  tienda por WhatsApp, mensajes positivos y sonidos). Qué trae cada APK: `docs/09-versiones.md`.
- **Decisiones tomadas (2026-11-09):** solo Android (sin iPhone). No hay ilustrador: **las ilustraciones del
  panda las creamos nosotros** en vector (la tienda usa fotos reales). **Habrá cuentas opcionales y sin
  sincronizar datos emocionales** (ADR-0004; proveedor propuesto: Firebase con correo). Se trabaja con VS Code +
  línea de comandos; Android Studio es opcional.
- **Prioridad: velocidad.** Seguir la "Vía rápida" del plan (APK 0.1 → 0.2 → 0.3 → 0.4): lo que no sale en el
  próximo APK espera. Los guardrails de salud y de datos reales no se saltan.
- **Idioma:** conversación, documentación y textos de la app en **español**. Código (clases, funciones,
  variables) en inglés; comentarios en español.

## Mapa de la documentación

| Necesitas… | Lee |
|---|---|
| Qué hacer ahora y qué falta decidir | `docs/01-plan-de-trabajo.md` |
| Cómo debe comportarse una pantalla o función | `docs/02-producto.md` |
| Dónde va el código y cómo se escribe | `docs/03-arquitectura.md` |
| Colores, tipografía, componentes, ilustraciones | `docs/04-diseno-visual.md` |
| Tablas y fórmulas de cada métrica | `docs/05-modelo-de-datos.md` |
| Qué está prohibido u obligatorio | `docs/06-guardrails.md` |
| Peluche y Bluetooth | `docs/07-bluetooth.md` |
| Por qué se eligió algo | `docs/decisiones/` |
| Preparar Windows + VS Code, clonar, compilar | `docs/08-manual-vscode.md` |
| Qué trae cada APK y qué se probó | `docs/09-versiones.md` |

## Stack

Kotlin · Jetpack Compose (Material 3, tema propio) · MVVM con `StateFlow` · Hilt · Room · Navigation Compose
con rutas `@Serializable` · `minSdk 26` / `targetSdk 36`. Versiones en `gradle/libs.versions.toml`.

## Estructura (paquete `com.panbu.app`)

```
navigation/      Rutas, NavHost, barra inferior (5 pestañas); AppViewModel: bienvenida o pestañas
ui/theme/        PanbuPalette, PanbuTheme (+ extraColors), PanbuTypography, PanbuShapes, PanbuSpacing
ui/components/   PanbuScreen, PanbuCard, GoalRow, HelpLineCard, SegmentedSelector, SettingsRow…
ui/components/charts/  CalmBarChart, DonutChart, MinutesAxis (Canvas, sin librerías)
feature/<x>/     XScreen + XContent + XViewModel (onboarding, home, stats, goals, calm, breathing, moment,
                 journal, shop, settings)
domain/model/    Modelos y reglas puras sin Android (Mood, Goal, CalmSession, StatsCalculator, RestReminder…)
domain/usecase/  ObserveTodayProgress, ObserveStats
data/local/      Room: PanbuDatabase (v2), Entities.kt, Daos.kt; esquemas en app/schemas/
data/preferences/ DataStore: nombre, bienvenida, horario de descanso, recordatorio
data/crypto/     Cifrado del diario (AES-GCM, Android Keystore)
data/repository/ Interfaz + implementación Room (MoodRepository es el ejemplo de referencia)
reminders/       Recordatorio de descanso (AlarmManager + receptores + notificación)
device/          PanbuDevice (interfaz del peluche) + NoPanbuDevice; BLE en la Fase 5
di/              Módulos de Hilt (Clock, base de datos, repositorios, peluche)
```

Ejemplo completo del patrón de punta a punta: `feature/home/` (HomeScreen → HomeViewModel → MoodRepository →
MoodDao).

## Comandos

```bash
./gradlew assembleDebug        # compilar
./gradlew testDebugUnitTest    # tests unitarios
./gradlew lintDebug            # lint
./gradlew installDebug         # instalar en el celular conectado
```

Gradle 8.14 necesita **JDK 17 o 21** (con JDK 25 falla) y el Android SDK en `ANDROID_HOME` (`~/Android/Sdk`).
En Linux usa `./scripts/gradlew.sh <tarea>`, que busca solo el JDK 21 (el Java del sistema es el 25).
Detalle en `docs/03-arquitectura.md` › Entorno de desarrollo. Si no hay un SDK o un JDK compatible, dilo
explícitamente: **no afirmes que algo compila si no se compiló**. Sin Android Studio no hay `@Preview`; para ver
una pantalla se usan los tests de captura (Roborazzi) y se mira el PNG. **Una animación infinita
(`rememberInfiniteTransition`) cuelga el test de captura**: el contenido debe tener un parámetro para apagarla
(ejemplo: `MomentContent(animatePulse = false)`). Para probar en el emulador: `adb` + `uiautomator dump`.

## Guardrails (no negociables; detalle y razones en docs/06-guardrails.md)

1. **Acompaña, no trata.** Nada de diagnósticos, promesas de curar o prevenir, ni lenguaje clínico. El aviso
   "no sustituye a un profesional" debe verse en el onboarding, en la pestaña Panbu y en Ayuda.
2. **Siempre hay una salida a ayuda humana** (línea de ayuda en Panbu y en Ayuda). El diario nunca se analiza.
3. **Sin presión ni manipulación:** sin rachas que castigan, sin rojo de error, sin culpa, máximo 2
   notificaciones al día. La tienda nunca aparece en los flujos de calma ni en notificaciones.
4. **Datos reales:** ningún gráfico ni número inventado en release. `DemoData` solo en `app/src/debug/`. Sin
   datos → estado vacío. Toda regla de cálculo de `docs/05` tiene un test.
5. **Privacidad local:** los datos emocionales no salen del celular sin ADR + consentimiento. A la nube solo va
   la cuenta (correo, nombre y foto opcional, ADR-0004), y se puede borrar desde la app y desde la web. Las
   funciones de calma nunca piden iniciar sesión. Sin SDKs de analítica ni publicidad. `allowBackup="false"`.
   El diario se cifra. Nunca registrar datos personales en logs.
6. **Permisos:** solo los de la tabla de `docs/06-guardrails.md`; se agregan a la tabla antes que al manifest y
   se piden en contexto.
7. **Técnica:** sin secretos en el repo; `applicationId` fijo; los enums guardados no se renombran; migraciones
   de Room obligatorias después de publicar (nunca `fallbackToDestructiveMigration`); textos en `strings.xml`;
   colores y medidas solo desde el tema; dependencias nuevas en `libs.versions.toml` y justificadas.

Si una petición choca con un guardrail: **detente, explica cuál y propón una alternativa**. No lo resuelvas en
silencio.

## Cómo trabajar en este repo

- Antes de implementar, ubica la tarea en el plan y su comportamiento en `docs/02`. Si no está definido, propón
  una definición y escríbela en `docs/02` (o pregunta) antes de programar.
- Al terminar: compila, corre los tests, marca la casilla del plan y actualiza los docs que cambiaron.
- Para decisiones difíciles de revertir (librería, permiso, almacenamiento, pagos, backend), escribe un ADR en
  `docs/decisiones/`.
- Las pantallas siguen el patrón `XScreen` (con estado) → `XContent` (sin estado, con `@Preview`).
- No inventes los textos legales, la línea de ayuda, los precios ni las promesas del producto: deben venir del
  cliente o estar marcados como pendientes.

## Skills del proyecto (`.claude/skills/`)

| Skill | Úsala cuando… |
|---|---|
| `panbu-ui` | Creas o cambias pantallas, componentes, colores, textos visibles |
| `panbu-feature` | Implementas una tarea del plan que toca varias capas (UI + datos) |
| `panbu-privacidad` | Tocas datos emocionales, permisos, notificaciones, red, backups o textos de salud |
| `panbu-ble` | Trabajas en la conexión con el peluche (Fase 5) |
| `panbu-release` | Preparas una versión para probar o publicar en Google Play |
