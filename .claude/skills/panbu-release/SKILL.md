---
name: panbu-release
description: Preparar una versión de Panbu para pruebas o para publicar en Google Play - versionado, firma, build de release, revisión de privacidad, ficha de Play Console, Seguridad de los datos, declaración de app de salud y prueba cerrada. Úsala al generar un APK/AAB para entregar, al subir a Play Console o al revisar si la app está lista para publicarse.
---

# Publicar Panbu

## 1. Versión
- Sube `versionCode` (siempre +1) y `versionName` (semver: `0.x.y` mientras no se publique) en
  `app/build.gradle.kts`.
- Anota los cambios para la persona usuaria, en español y sin términos técnicos.

## 2. Firma (nunca en el repositorio)
- El keystore y sus contraseñas viven fuera del repo (`keystore.properties` está en `.gitignore`). Lee las
  rutas y contraseñas desde `keystore.properties` o desde variables de entorno en `signingConfigs`.
- Usa Play App Signing. **Respalda la clave de subida**: si se pierde, hay que pedir a Google que la
  restablezca.
- Nunca imprimas contraseñas en logs ni las escribas en archivos versionados.

## 3. Build
```bash
./gradlew clean testDebugUnitTest lintRelease bundleRelease   # AAB para Play Store
./gradlew assembleRelease                                     # APK para pruebas directas
```
- Instala el build de release en un celular y recorre todas las pantallas (R8 puede romper la reflexión o la
  serialización).
- Confirma que no hay `DemoData` en release: el source set `debug` no debe filtrarse a `main`.

## 4. Revisión de guardrails
Recorre la "Checklist antes de publicar" de `docs/06-guardrails.md`. Si algo falla, no se publica.

## 5. Play Console
- **Ficha:** nombre "Panbu", descripción sin promesas médicas, capturas reales (sin datos inventados que
  parezcan resultados), ícono de 512 px y gráfico destacado de 1024×500.
- **Política de privacidad:** una URL pública que coincida con lo que hace la app (datos locales, sin terceros).
- **Seguridad de los datos:** declara lo que realmente se recoge. Con datos solo en el celular y sin envío, se
  declara que no se recopilan ni comparten datos, salvo que una fase posterior lo cambie (revísalo cada vez).
- **Declaración de apps de salud:** Panbu es una app de bienestar, no un dispositivo médico; complétala con
  sinceridad.
- **Clasificación de contenido:** cuestionario IARC.
- **Público objetivo:** si se incluyen menores de 13 años, aplican las reglas de la Política de Familias.
  Confírmalo con el cliente.

## 6. Pistas
1. Prueba interna (hasta 100 testers, inmediata).
2. Prueba cerrada: las cuentas personales nuevas necesitan **12 testers durante 14 días** antes de producción
   (verifica la regla vigente en Play Console).
3. Producción con despliegue gradual (por ejemplo, 20 % → 100 %), vigilando los fallos en Android Vitals.

## 7. Al terminar
Marca las tareas de la Fase 4 en `docs/01-plan-de-trabajo.md` y crea un tag de git `vX.Y.Z` (solo si la
persona usuaria lo pide).
