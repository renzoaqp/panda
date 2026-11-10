# Panbu — Tu calma, siempre contigo

App Android de bienestar emocional que acompaña al peluche **Panbu**: momentos de calma, registro de cómo te
sientes, metas pequeñas (menos celular de noche, respirar, dormir mejor) y la tienda del peluche. En la fase
final se conecta al peluche por Bluetooth.

**Estado:** APK 0.3 de prueba (bienvenida, estadísticas reales, diario cifrado, recordatorio de descanso y
ajustes). Qué trae cada versión: [docs/09-versiones.md](docs/09-versiones.md). Qué sigue: el
[plan de trabajo](docs/01-plan-de-trabajo.md).

## Empezar

No hace falta Android Studio: basta VS Code y la terminal. Se necesitan el JDK 21 y el Android SDK
(command-line tools). El paso a paso está en
[docs/03-arquitectura.md › Entorno de desarrollo](docs/03-arquitectura.md#entorno-de-desarrollo).

**Windows: sigue el [manual de VS Code](docs/08-manual-vscode.md)** (paso a paso, con solución de problemas). En
resumen:
1. Instala Git y VS Code (`winget install Git.Git` y `winget install Microsoft.VisualStudioCode`).
2. En VS Code: `Ctrl+Shift+P` → **Git: Clone** → `https://github.com/renzoaqp/panda.git` → carpeta corta, como
   `C:\dev`, → **Abrir** → "Confiar".
3. En la terminal de VS Code: `powershell -ExecutionPolicy Bypass -File scripts\setup-windows.ps1`.
4. Reinicia VS Code y compila con `Ctrl+Shift+B`. Si VS Code ofrece "actualizar Gradle", responde que no.

Con `JAVA_HOME` (JDK 21) y `ANDROID_HOME` configurados, y un celular conectado con "Depuración USB":

```bash
./gradlew assembleDebug        # compila
./gradlew installDebug         # instala en el celular
./gradlew testDebugUnitTest    # tests
```

## Documentación

Toda la documentación está en [`docs/`](docs/README.md): producto, arquitectura, diseño, datos, guardrails,
Bluetooth y decisiones (ADR). El material original del cliente está en [`00info/`](00info/).

Las reglas para trabajar con Claude Code están en [`CLAUDE.md`](CLAUDE.md) y [`.claude/skills/`](.claude/skills/).

> Panbu acompaña a practicar hábitos de bienestar, pero no sustituye la atención de un profesional de la salud.
