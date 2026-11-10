# Panbu — Tu calma, siempre contigo

App Android de bienestar emocional que acompaña al peluche **Panbu**: momentos de calma, registro de cómo te
sientes, metas pequeñas (menos celular de noche, respirar, dormir mejor) y la tienda del peluche. En la fase
final se conecta al peluche por Bluetooth.

**Estado:** Fase 0 (base y diseño). Ver el [plan de trabajo](docs/01-plan-de-trabajo.md).

## Empezar

No hace falta Android Studio: basta VS Code y la terminal. Se necesitan el JDK 21 y el Android SDK
(command-line tools). El paso a paso está en
[docs/03-arquitectura.md › Entorno de desarrollo](docs/03-arquitectura.md#entorno-de-desarrollo).

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
