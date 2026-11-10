#!/usr/bin/env bash
# Ejecuta ./gradlew con un JDK 21, aunque el Java del sistema sea otro (Gradle 8.14 no funciona con Java 25).
# Uso:  ./scripts/gradlew.sh assembleDebug      (lo usan las tareas de VS Code en Linux)
set -euo pipefail

is_jdk21() { [ -x "$1/bin/java" ] && "$1/bin/java" -version 2>&1 | grep -q '"21\.'; }

if [ -z "${JAVA_HOME:-}" ] || ! is_jdk21 "$JAVA_HOME"; then
    JAVA_HOME=""
    for dir in "$HOME"/.jdks/jdk-21* /usr/lib/jvm/java-21-openjdk* /usr/lib/jvm/temurin-21*; do
        if is_jdk21 "$dir"; then JAVA_HOME="$dir"; break; fi
    done
    if [ -z "$JAVA_HOME" ]; then
        echo "No encontré un JDK 21. Instálalo como dice docs/03-arquitectura.md (Entorno de desarrollo)." >&2
        exit 1
    fi
    export JAVA_HOME
fi

cd "$(dirname "$0")/.."
exec ./gradlew "$@"
