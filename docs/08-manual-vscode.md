# Manual: trabajar en Panbu con VS Code (Windows y Linux)

Guía única para trabajar en Panbu **sin Android Studio**: preparar la PC, compilar, ver la app (en un celular o en
el emulador) y el trabajo del día a día. Los pasos 1–10 son para **Windows**. Para **Linux** ver el
[anexo](#anexo--linux-fedora). La preparación se hace una sola vez y toma unos 30 minutos, casi todo en descargas.

## Referencia rápida

**Tareas de VS Code** (*Terminal → Run Task…*; funcionan igual en Windows y en Linux):

| Tarea | Qué hace |
|---|---|
| **Panbu: compilar APK** (`Ctrl+Shift+B`) | Genera `app/build/outputs/apk/debug/app-debug.apk` |
| **Panbu: abrir emulador** | Abre el celular virtual "Panbu" (antes hay que crearlo una vez, paso 7C) |
| **Panbu: instalar y abrir** | Compila, instala y abre Panbu en el emulador o en el celular conectado |
| Panbu: instalar en el celular | Solo instala (celular por USB o emulador) |
| Panbu: abrir la app | Solo abre Panbu si ya está instalada |
| Panbu: tests | Corre los tests automáticos (deben pasar todos) |
| Panbu: capturas de pantalla | Genera un PNG de cada pantalla en `app/build/outputs/roborazzi/` |

**Scripts** (se ejecutan una sola vez, al preparar la PC):

| Script | Sistema | Qué hace | Paso |
|---|---|---|---|
| `scripts\setup-windows.ps1` | Windows | Instala el JDK 21 y el Android SDK y configura las variables | 5 |
| `scripts\setup-emulator-windows.ps1` | Windows | Instala el emulador y crea el celular virtual "Panbu" | 7C |
| `scripts/setup-emulator.sh` | Linux | Igual que el anterior, en Linux | Anexo |
| `scripts/gradlew.sh` | Linux | Compila usando el JDK 21 (lo usan las tareas; no hace falta llamarlo a mano) | Anexo |

Para ejecutar un `.ps1` en Windows: `powershell -ExecutionPolicy Bypass -File scripts\<nombre>.ps1`, en la terminal
de VS Code y desde la carpeta del proyecto.

## Cómo leer este manual (y toda la documentación)

Los documentos son archivos Markdown (`.md`): texto con formato (títulos, tablas, listas).

- **Antes de clonar:** léelo en GitHub, que lo muestra formateado sin instalar nada:
  <https://github.com/renzoaqp/panda/blob/main/docs/08-manual-vscode.md>
- **En VS Code, ya con el proyecto abierto:** los `.md` se abren formateados automáticamente (lo configura
  `.vscode/settings.json`). Si alguno se ve como texto plano con `#`, `|` y `**`:
  - `Ctrl+Shift+V` → abre la vista formateada;
  - `Ctrl+K` y luego `V` → texto y vista formateada lado a lado (útil para editar).
- Para **editar** un `.md`: clic derecho en la pestaña → *Reopen Editor With…* → *Text Editor*.

No hace falta ninguna extensión para ver Markdown: VS Code ya lo trae. La extensión recomendada *Markdown All in
One* (paso 4) solo agrega comodidades para editar, como el índice automático y atajos.

## Qué vas a instalar

| Programa | Para qué | Cómo |
|---|---|---|
| Git | Descargar el proyecto y guardar cambios | Paso 1 |
| VS Code | Editar el código | Paso 1 |
| Extensiones de VS Code | Colores para Kotlin y XML, y Claude Code | Paso 4 (VS Code las propone solo) |
| JDK 21 | Lo necesita Gradle para compilar | Paso 5 (script) |
| Android SDK | Las herramientas de Android para compilar e instalar | Paso 5 (script) |

---

## Paso 1 — Instalar Git y VS Code

Abre **PowerShell** (menú Inicio → escribe "PowerShell") y ejecuta:

```powershell
winget install --id Git.Git -e
winget install --id Microsoft.VisualStudioCode -e
```

Si `winget` no existe, instala "App Installer" desde Microsoft Store o descarga los instaladores de
<https://git-scm.com> y <https://code.visualstudio.com>. **Cierra PowerShell cuando termine.**

## Paso 2 — Configurar Git (una sola vez)

Abre PowerShell de nuevo:

```powershell
git config --global user.name "Tu Nombre"
git config --global user.email "tu-correo@ejemplo.com"
git config --global core.longpaths true
```

La última línea evita el error *"Filename too long"*, porque Android crea rutas muy largas.

## Paso 3 — Clonar el proyecto con VS Code

1. Abre VS Code.
2. `Ctrl+Shift+P` → escribe **Git: Clone** → Enter.
3. Pega `https://github.com/renzoaqp/panda.git` → Enter.
4. Elige una carpeta **corta y sin espacios**, por ejemplo `C:\dev` (crea la carpeta si no existe). El proyecto
   queda en `C:\dev\panda`.
   - ❌ No uses `Documentos`, `Escritorio` ni nada dentro de **OneDrive**: las rutas son largas y OneDrive
     sincroniza miles de archivos de compilación.
5. Si el repositorio es privado, VS Code pide iniciar sesión en GitHub: acepta y autoriza.
6. Al terminar, presiona **Abrir**.
7. Si pregunta *"¿Confías en los autores de los archivos de esta carpeta?"*, responde **Sí, confío**. Si no lo
   haces, las tareas de compilación no funcionan.

**¿No aparece "Git: Clone"?** VS Code no encuentra Git: vuelve al paso 1 y, después de instalar Git, cierra
**todas** las ventanas de VS Code y ábrelo de nuevo.

**Clonar desde PowerShell** (alternativa, da el mismo resultado):

```powershell
mkdir C:\dev -Force
cd C:\dev
git clone https://github.com/renzoaqp/panda.git
code panda
```

## Paso 4 — Extensiones de VS Code

Al abrir el proyecto, VS Code muestra abajo a la derecha: *"Este espacio de trabajo tiene extensiones
recomendadas"* → **Instalar todo**. Si no lo ves: `Ctrl+Shift+X` → escribe `@recommended`.

| Extensión | Para qué | |
|---|---|---|
| **Kotlin** (`mathiasfrohlich.Kotlin`) | Colores y formato del código Kotlin | Recomendada |
| **XML** (`redhat.vscode-xml`) | Archivos de Android (`AndroidManifest.xml`, `strings.xml`, íconos) | Recomendada |
| **EditorConfig** (`EditorConfig.EditorConfig`) | Aplica el formato del proyecto (sangría, finales de línea) | Recomendada |
| **Markdown All in One** (`yzhang.markdown-all-in-one`) | Comodidades para editar la documentación `.md` (verla no necesita extensión) | Recomendada |
| **Claude Code** (`anthropic.claude-code`) | Trabajar con Claude dentro de VS Code | Recomendada |
| Language Support for Java (`redhat.java`), Extension Pack for Java, Gradle for Java | — | ❌ **No instalar** |

**¿Por qué no las de Java?** No entienden proyectos Android y además ofrecen "actualizar Gradle", lo que rompe la
compilación. Si ya las tienes instaladas, el proyecto las neutraliza (`.vscode/settings.json`), pero si VS Code
te pregunta por actualizar Gradle, responde siempre **No**.

> VS Code no tiene autocompletado completo para Kotlin en Android como Android Studio. Los errores se ven al
> compilar (paso 6), y Claude Code compila y corrige.

## Paso 5 — Instalar el JDK y el Android SDK (script automático)

En VS Code: menú **Terminal → New Terminal** (se abre PowerShell abajo). Ejecuta:

```powershell
powershell -ExecutionPolicy Bypass -File scripts\setup-windows.ps1
```

El script:
- instala el **JDK 21** (Temurin) con `winget`;
- descarga el **Android SDK** en `%LOCALAPPDATA%\Android\Sdk` y verifica que la descarga no esté alterada
  (checksum);
- instala `adb`, Android 36, las build-tools y el driver USB de Google;
- configura `JAVA_HOME`, `ANDROID_HOME` y `adb` para tu usuario.

**Si se queda en "Iniciando instalación de paquete…":** no está colgado. Windows está pidiendo permiso de
administrador para instalar el JDK, y esa ventana suele quedar **escondida detrás de VS Code**. Búscala en la barra
de tareas (un ícono de escudo que parpadea) o con `Alt+Tab`, y responde **Sí**. Si no aparece después de 5 minutos,
cancela con `Ctrl+C`, instala el JDK 21 a mano desde <https://adoptium.net> (Windows x64, `.msi`) y vuelve a
ejecutar el script: detecta que el JDK ya está y sigue con el resto.

**Al terminar, cierra VS Code por completo y vuelve a abrirlo** para que tome las variables nuevas.

Comprobación (en la terminal de VS Code):

```powershell
java -version        # debe decir 21
adb version          # debe mostrar la versión de Android Debug Bridge
```

## Paso 6 — Compilar el APK

`Ctrl+Shift+B` (o *Terminal → Run Task… → Panbu: compilar APK*).

- La primera vez tarda **5–10 minutos**: descarga Gradle y las librerías. Las siguientes, menos de un minuto.
- **Resultado esperado:** la terminal de VS Code muestra muchas líneas `> Task :app:...` y termina con
  **`BUILD SUCCESSFUL in …`**.
- El APK queda en `app\build\outputs\apk\debug\app-debug.apk` (unos 11 MB). Para encontrarlo:
  - en el explorador de VS Code (`Ctrl+Shift+E`): `app` → `build` → `outputs` → `apk` → `debug`. Si no ves la
    carpeta `build`, haz **Pull** (las versiones viejas de la configuración la ocultaban) o
    `Ctrl+Shift+P` → **Reload Window**;
  - o abre la carpeta en el Explorador de Windows con: `explorer app\build\outputs\apk\debug`
- El APK **no se guarda en Git**: cada PC lo genera al compilar.
- Si termina con `BUILD FAILED` o *"terminated with exit code: 1"*, sube un poco en la terminal hasta
  **`What went wrong:`** y busca ese mensaje en la tabla de problemas del final.

## Paso 7 — Instalar la app en un celular

**Opción A — por cable (recomendada para desarrollar):**
1. En el celular: *Ajustes → Acerca del teléfono* → toca **7 veces** "Número de compilación" (activa las Opciones
   de desarrollador).
2. *Ajustes → Sistema → Opciones de desarrollador* → activa **Depuración USB**.
3. Conecta el celular con un cable **de datos** (algunos cables solo cargan).
4. En el celular aparece *"¿Permitir depuración USB?"* → marca "Permitir siempre" → **Aceptar**.
5. En VS Code: *Terminal → Run Task… → Panbu: instalar en el celular*. La app aparece como **Panbu**.

**Opción B — sin cable:**
1. Envía `app-debug.apk` al celular: por WhatsApp a ti mismo (como **Documento**), por Google Drive, por correo o
   copiándolo por cable a la carpeta Descargas.
2. En el celular, toca el archivo. Android dice que no puede instalar apps de esa fuente → **Configuración** →
   activa **Permitir de esta fuente** → vuelve → **Instalar**.
3. Si Play Protect avisa "app desconocida", toca **Más detalles → Instalar de todas formas**. Es normal en un APK
   de prueba que no viene de Play Store.
4. Para instalar una versión nueva, repite lo mismo: se instala encima y conserva los datos.

**Opción C — emulador (un celular virtual en la PC, sin celular real):**
1. Crea el celular virtual una sola vez (descarga unos 1,5 GB):
   - Windows: `powershell -ExecutionPolicy Bypass -File scripts\setup-emulator-windows.ps1`
   - Linux: `./scripts/setup-emulator.sh`
2. *Terminal → Run Task… → **Panbu: abrir emulador***. Se abre una ventana con un celular. La primera vez tarda
   1–3 minutos en arrancar.
3. Cuando muestre la pantalla de inicio de Android: *Run Task… → **Panbu: instalar y abrir***. Compila, instala y
   abre Panbu dentro del emulador.
4. Se usa con el mouse como si fuera el dedo. El teclado de la PC también funciona.

El emulador usa unos 2–3 GB de RAM: cierra programas pesados si la PC se pone lenta. En Windows necesita la
virtualización activa: si dice *"requires hardware acceleration"*, ve a "Activar o desactivar las características
de Windows", marca **Plataforma del hipervisor de Windows** y reinicia. El Bluetooth del peluche (Fase 5) solo se
puede probar en un celular real.

### Qué revisar para saber si funciona (APK 0.1)

| Prueba | Resultado esperado |
|---|---|
| Abrir la app | Ícono del panda con el nombre "Panbu"; fondo crema; no se cierra sola |
| Inicio → tocar un estado ("Tranquilo", "Ansioso"…) | El chip queda marcado y abajo dice "Hoy: Tranquilo. Gracias por contármelo." |
| **Cerrar la app por completo** (desde recientes) y abrirla otra vez | El estado elegido **sigue marcado**. Esto prueba que se guarda en la base de datos |
| Tocar las 5 pestañas de abajo | Cambia de pantalla y la pestaña activa se marca en lavanda |
| Engranaje arriba a la derecha en Inicio | Va a Ajustes |
| Pestaña Panbu → "Conoce a tu Panbu" | Abre "Agregar compra" sin la barra de abajo; la flecha ← o el gesto de volver regresa |
| Pantallas con "Se construye en la fase…" | Es lo esperado en esta versión |

Si la app se cierra sola, anota qué tocaste. Con el celular conectado por cable, `adb logcat -d > error.txt` guarda
el registro del error para revisarlo.

## Paso 8 — Ver las pantallas sin celular

*Terminal → Run Task… → Panbu: capturas de pantalla*. Genera imágenes PNG de cada pantalla en
`app\build\outputs\roborazzi\`. Sirve para revisar el diseño sin instalar nada.

## Paso 9 — El día a día con Git en VS Code

| Quiero… | En VS Code |
|---|---|
| Traer los cambios nuevos | Barra inferior → botón de sincronizar (🔄), o `Ctrl+Shift+G` → `…` → **Pull** |
| Ver qué cambié | `Ctrl+Shift+G` (lista de archivos modificados) |
| Guardar mis cambios (commit) | `Ctrl+Shift+G` → escribe un mensaje → **Commit** |
| Subirlos a GitHub (push) | Después del commit → **Sync Changes** / **Push** |

Haz **Pull antes de empezar a trabajar**, sobre todo si también se trabaja desde la PC con Linux.

## Paso 10 — Claude Code en VS Code

Con la extensión **Claude Code** instalada, abre su panel desde la barra lateral. Al trabajar en este proyecto,
Claude lee automáticamente `CLAUDE.md` (contexto y reglas) y las skills de `.claude/skills/`. Pídele las tareas
del plan (`docs/01-plan-de-trabajo.md`).

---

## Anexo — Linux (Fedora)

En Linux el proyecto se usa igual (mismas tareas de VS Code), con estas diferencias:

| | Linux |
|---|---|
| JDK 21 | En `~/.jdks/jdk-21.*` (Temurin). El Java del sistema es el 25, que **no sirve**; `scripts/gradlew.sh` busca el 21 solo |
| Android SDK | En `~/Android/Sdk`; `local.properties` tiene `sdk.dir` (no va a Git) |
| Compilar | `Ctrl+Shift+B`, o en la terminal: `./scripts/gradlew.sh assembleDebug` (**no** `./gradlew` directo: usaría Java 25 y falla con `25.0.4.1`) |
| Emulador | Crear una vez con `./scripts/setup-emulator.sh`; luego la tarea **Panbu: abrir emulador**. Requiere `/dev/kvm` (Fedora lo trae) |
| Terminal | La de VS Code puede ser PowerShell o bash; los comandos `./scripts/...` funcionan en las dos |

Preparar Linux desde cero: instalar el JDK 21 (por ejemplo, Temurin en `~/.jdks`) y el Android SDK
command-line tools en `~/Android/Sdk` con `platform-tools`, `platforms;android-36` y `build-tools;35.0.0`
(ver [docs/03 › Entorno de desarrollo](03-arquitectura.md#entorno-de-desarrollo)).

## Problemas comunes

| Mensaje o síntoma | Solución |
|---|---|
| Un `.md` se ve como texto plano (con `#`, `\|`, `**`) | `Ctrl+Shift+V`, o `Ctrl+K` y luego `V` para verlo lado a lado |
| En `Ctrl+Shift+P` **no aparece "Git: Clone"** (solo "Git: Show Git Output") | VS Code no encuentra Git. Instálalo (`winget install --id Git.Git -e`), **cierra todas las ventanas de VS Code** y vuelve a abrirlo. Alternativa sin VS Code: ver "Clonar desde PowerShell" abajo |
| VS Code ofrece **"Upgrade Gradle"** / actualizar el wrapper | Responde **No**. Panbu usa Gradle 8.14.3 |
| `Verification of Gradle distribution failed` | `gradle\wrapper\gradle-wrapper.properties` debe decir `gradle-8.14.3-bin.zip`. Si dice otra versión, alguien la cambió: restáuralo con Git (`Ctrl+Shift+G` → clic derecho en el archivo → *Discard Changes*) |
| `What went wrong: 25.0.4.1` (o cualquier número 25.x) | Gradle está usando Java 25. En Windows: vuelve a ejecutar el script del paso 5 y reinicia VS Code. En Linux: compila con `Ctrl+Shift+B` o con `./scripts/gradlew.sh assembleDebug` (no con `./gradlew` directo) |
| `JAVA_HOME is not set` o `Unsupported class file major version` | Cierra y abre VS Code. Si sigue, ejecuta de nuevo el script del paso 5. `java -version` debe decir 21 |
| `SDK location not found` | Falta `ANDROID_HOME`: ejecuta el script del paso 5 y reinicia VS Code |
| `...running scripts is disabled on this system` | Usa el comando del paso 5 tal cual (incluye `-ExecutionPolicy Bypass`) |
| `Filename too long` | `git config --global core.longpaths true` y usa una carpeta corta, como `C:\dev\panda` |
| `adb devices` no muestra el celular | Cable de datos, aceptar el diálogo en el celular, otro puerto USB. Si no es un Pixel, instala el driver USB del fabricante |
| `adb devices` dice `unauthorized` | Desbloquea el celular y acepta el diálogo "¿Permitir depuración USB?" |
| La compilación es muy lenta o se queda sin memoria | Cierra programas pesados; la primera compilación siempre es la más lenta |
