# Panbu — Tu calma, siempre contigo

App Android de bienestar emocional que acompaña al peluche **Panbu**: momentos de calma, registro de cómo te
sientes, metas pequeñas (menos celular de noche, respirar, dormir mejor) y la tienda del peluche. En la fase
final se conecta al peluche por Bluetooth.

**Estado:** Fase 0 (base y diseño). Ver el [plan de trabajo](docs/01-plan-de-trabajo.md).

## Empezar

1. Instala [Android Studio](https://developer.android.com/studio). Trae el SDK de Android y el JDK que usa Gradle.
2. Abre esta carpeta con *File → Open* y espera a que termine el *Gradle Sync*.
3. Conecta un celular Android con "Depuración USB" activada y presiona ▶ (*Run 'app'*).

Desde la terminal (con `JAVA_HOME` apuntando al JDK de Android Studio):

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
