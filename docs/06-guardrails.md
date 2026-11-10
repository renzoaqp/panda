# Guardrails — reglas obligatorias

Estas reglas valen para todo el que trabaje en Panbu, sea persona o IA. Si una tarea choca con alguna,
**se detiene y se consulta**; no se busca una vuelta. Cambiar una regla requiere un ADR.

## A. Salud y bienestar

1. **Panbu acompaña, no trata.** Nunca diagnosticar, ni prometer curar, prevenir o detener la ansiedad u otros
   problemas. No usar lenguaje clínico ("terapia", "tratamiento", "cura", "síntomas") en la app ni en la ficha
   de Play Store.
2. **Aviso visible** en el onboarding, en la pestaña Panbu y en Ayuda: "Panbu te acompaña a practicar hábitos de
   bienestar, pero no sustituye la atención de un profesional de la salud."
3. **Siempre hay una salida a ayuda humana:** "¿Necesitas hablar con alguien?", en la pestaña Panbu y en Ayuda,
   con la línea de ayuda del país. Para Perú: Línea 113, opción 5 (salud mental, MINSA). **Verificar que siga
   vigente antes de publicar.**
4. **No se analiza el contenido del diario** (ni en el celular ni fuera de él). Es privado.
5. **Sin presión:** no hay rachas que se pierden, ni rojo de error, ni mensajes de culpa. Superar un límite se
   muestra en rosa suave y con un texto amable.
6. **Notificaciones con medida:** máximo 2 al día; todas se pueden desactivar; nunca envían mensajes alarmantes.
7. **Sin patrones engañosos:** la tienda no aparece en los flujos de calma, ni en notificaciones, ni en momentos
   de ánimo bajo. "Necesito un momento de calma" nunca lleva a la tienda.

## B. Datos reales

8. **Ningún número ni gráfico muestra datos inventados en la versión publicada.** Los datos de ejemplo
   (`DemoData`) viven solo en `app/src/debug/`.
9. **Sin datos → estado vacío** que explica qué hacer; nunca ceros engañosos ni porcentajes calculados sobre
   nada.
10. **Cada regla de cálculo** de [05-modelo-de-datos.md](05-modelo-de-datos.md#reglas-de-cálculo) tiene un test
    unitario.

## C. Privacidad

11. **Local primero:** los datos emocionales (ánimo, diario, sesiones, sueño, uso del celular) se quedan en el
    celular. Enviar cualquier dato fuera del celular requiere un ADR **y** el consentimiento explícito de la
    persona.
12. **Sin SDKs de terceros que vean datos personales:** nada de analítica, publicidad ni "growth". Un reporte de
    errores solo con ADR y sin datos personales.
13. **Diario cifrado** (AES-GCM con clave en Android Keystore) y **backup en la nube desactivado**
    (`android:allowBackup="false"`) hasta tener una exportación cifrada.
14. **Control de la persona:** desde Ajustes se puede ver qué se guarda, exportar los datos y borrarlo todo.
15. **Los logs no llevan datos personales:** nunca registrar el texto del diario, los estados de ánimo, las horas
    de sueño ni el uso del celular.
16. **Permisos mínimos,** pedidos en el momento de uso y con una pantalla previa que explica para qué. Cada
    permiso debe estar en la tabla de abajo **antes** de agregarse al `AndroidManifest.xml`.

## D. Técnica

17. **No hay secretos en el repositorio:** keystores, contraseñas y claves de API van fuera (variables de entorno o
    `local.properties`, que está en `.gitignore`).
18. **`applicationId` (`com.panbu.app`) no se cambia** después de la primera publicación.
19. **Migraciones de Room obligatorias** después de publicar; nunca `fallbackToDestructiveMigration` en release.
20. **Los valores de enum guardados en la base de datos no se renombran.**
21. **No hay texto visible fuera de `strings.xml`,** ni colores o medidas fuera del tema.
22. **Las dependencias nuevas se justifican** (en el PR o con un ADR si son importantes) y van en
    `libs.versions.toml`.
23. **Antes de dar una tarea por terminada:** compila, los tests pasan, se probó en un celular y la documentación
    está al día.

## Permisos

| Permiso | Para qué | Fase | Cómo se pide |
|---|---|---|---|
| *(ninguno)* | El esqueleto actual no necesita permisos | 0 | — |
| `POST_NOTIFICATIONS` | Recordatorios (Android 13+) | 1 | Al activar los recordatorios |
| `RECEIVE_BOOT_COMPLETED` | Volver a programar los recordatorios tras reiniciar | 1 | No se pide (normal) |
| `USE_BIOMETRIC` | Bloquear el diario con huella | 1 | No se pide (normal) |
| `FOREGROUND_SERVICE`, `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Sonidos con la pantalla apagada | 1 | No se pide (normal) |
| `PACKAGE_USAGE_STATS` | Tiempo de pantalla nocturno | 2 | Pantalla explicativa → Ajustes del sistema. Opcional |
| `INTERNET` | Solo si la tienda carga imágenes o precios remotos | 3 | Evitarlo si es posible |
| `BLUETOOTH_SCAN` (`neverForLocation`), `BLUETOOTH_CONNECT` | Conectar el peluche (Android 12+) | 5 | Al emparejar |

`SCHEDULE_EXACT_ALARM` / `USE_EXACT_ALARM` **no** se usan: un recordatorio con unos minutos de diferencia está
bien.

## Checklist antes de publicar

- [ ] No hay `DemoData` ni textos de prueba en el APK/AAB de release
- [ ] El aviso de "no sustituye a un profesional" se ve en el onboarding, en Panbu y en Ayuda
- [ ] La línea de ayuda está verificada y vigente
- [ ] Los permisos del manifest coinciden con la tabla de arriba
- [ ] `allowBackup="false"` y el diario está cifrado
- [ ] Exportar y borrar datos funcionan
- [ ] Los logs de release no tienen datos personales (revisar con `adb logcat`)
- [ ] La política de privacidad publicada coincide con lo que hace la app
- [ ] La sección "Seguridad de los datos" de Play Console coincide con la política
- [ ] Ningún texto promete resultados médicos
