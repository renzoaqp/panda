---
name: panbu-privacidad
description: Guardrails de privacidad, salud y ética de Panbu. Úsala SIEMPRE que el trabajo toque datos emocionales (estado de ánimo, diario, sesiones de calma, sueño, tiempo de pantalla), permisos de Android, notificaciones, analítica, red o servidores, backups, exportar o borrar datos, la tienda cerca de flujos de calma, o cualquier texto sobre ansiedad, salud mental o bienestar.
---

# Privacidad y bienestar en Panbu

Reglas completas y sus razones: `docs/06-guardrails.md`. Si algo choca con estas reglas, **detente, explica
cuál y propone una alternativa**. Para cambiar una regla hace falta un ADR aprobado por el responsable del
proyecto.

## Preguntas que debes responder antes de programar
1. **¿Qué dato se guarda o se muestra?** ¿Hace falta de verdad? (recoger solo lo necesario)
2. **¿Sale del celular?** Si es así: no sin un ADR + consentimiento explícito. Sin analítica, publicidad ni
   SDKs de terceros que vean datos personales.
3. **¿Termina en logs?** Nunca registres el texto del diario, los estados de ánimo, el sueño ni el uso del
   celular, tampoco en `Log.d`.
4. **¿Necesita un permiso?** Solo si está en la tabla de permisos de `docs/06`. Se pide en contexto, con una
   pantalla previa que explica para qué, y la app funciona si la persona dice que no.
5. **¿Se muestra un número?** Que salga de datos reales. Si no hay datos, estado vacío. `DemoData` solo en
   `app/src/debug/`.
6. **¿Hay texto de salud?** Sin diagnósticos, promesas ni términos clínicos. El aviso "no sustituye a un
   profesional" se mantiene visible donde corresponde.
7. **¿Presiona o manipula?** Sin culpa, sin rachas que castigan, sin rojo de error, máximo 2 notificaciones al
   día, y la tienda nunca aparece en flujos de calma ni de ánimo bajo.

## Patrones obligatorios
- **Diario:** el texto se cifra con AES-GCM y una clave en Android Keystore (`KeyGenParameterSpec`, sin
  exportar la clave). El IV se guarda junto al texto cifrado. Bloqueo opcional con `androidx.biometric`.
- **Backup:** `android:allowBackup="false"` se mantiene (ADR-0002).
- **Borrar todo:** borra Room, DataStore y la clave del Keystore; luego vuelve al onboarding.
- **Exportar:** archivo JSON que elige la persona con el Storage Access Framework; nunca se envía a ningún lado
  automáticamente.
- **Tiempo de pantalla:** `PACKAGE_USAGE_STATS` es opcional; sin permiso, la tarjeta lo explica (no muestra 0).
- **Ayuda humana:** "¿Necesitas hablar con alguien?" en Panbu y en Ayuda. La línea de ayuda debe estar
  verificada (Perú: Línea 113, opción 5); no inventes números.

## Checklist
- [ ] No hay datos personales nuevos que salgan del celular
- [ ] No hay datos personales en los logs
- [ ] Los permisos coinciden con la tabla de `docs/06`
- [ ] Hay estado vacío y no hay números inventados
- [ ] Los textos no son clínicos, no culpan y el aviso sigue visible
- [ ] Se actualizó `docs/06` o `docs/05` si cambió qué se guarda o qué se pide
