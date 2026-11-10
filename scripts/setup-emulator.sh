#!/usr/bin/env bash
# Linux: instala el emulador de Android y crea el celular virtual "Panbu" (Android 15, tipo Pixel 6).
# Uso:  ./scripts/setup-emulator.sh      Después: tarea de VS Code "Panbu: abrir emulador".
# Necesita el Android SDK en ~/Android/Sdk (docs/03-arquitectura.md) y unos 2 GB de disco.
set -euo pipefail

SDK="${ANDROID_HOME:-$HOME/Android/Sdk}"
IMAGE="system-images;android-35;google_apis;x86_64"
AVD_NAME="Panbu"

is_jdk21() { [ -x "$1/bin/java" ] && "$1/bin/java" -version 2>&1 | grep -q '"21\.'; }
if [ -z "${JAVA_HOME:-}" ] || ! is_jdk21 "$JAVA_HOME"; then
    for dir in "$HOME"/.jdks/jdk-21* /usr/lib/jvm/java-21-openjdk* /usr/lib/jvm/temurin-21*; do
        if is_jdk21 "$dir"; then export JAVA_HOME="$dir"; break; fi
    done
fi
export ANDROID_HOME="$SDK"

echo "== Descargando el emulador y la imagen de Android 15 (unos 1,5 GB) =="
"$SDK/cmdline-tools/latest/bin/sdkmanager" --sdk_root="$SDK" "emulator" "platform-tools" "$IMAGE"

echo "== Creando el celular virtual '$AVD_NAME' =="
echo no | "$SDK/cmdline-tools/latest/bin/avdmanager" create avd -n "$AVD_NAME" -k "$IMAGE" -d pixel_6 --force

# 2 GB de RAM (suficiente para Panbu) y teclado de la PC habilitado dentro del emulador.
CFG="$HOME/.android/avd/$AVD_NAME.avd/config.ini"
for kv in "hw.ramSize=2048" "hw.keyboard=yes"; do
    key="${kv%%=*}"
    if grep -q "^$key=" "$CFG"; then sed -i "s/^$key=.*/$kv/" "$CFG"; else echo "$kv" >> "$CFG"; fi
done

if [ ! -w /dev/kvm ]; then
    echo "Aviso: no hay acceso a /dev/kvm (aceleración por hardware); el emulador funcionará muy lento."
fi
echo "Listo. En VS Code: Terminal → Run Task… → 'Panbu: abrir emulador'."
