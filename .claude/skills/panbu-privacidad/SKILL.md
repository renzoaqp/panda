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
  exportar la clave). El IV se guarda junto al texto cifrado. Ya existe: `data/crypto/TextCipher.kt` (ADR-0006);
  reutilízalo, no crees otro cifrado. Bloqueo opcional con `androidx.biometric` (pendiente).
- **Estado de pantalla:** el texto del diario (o cualquier dato emocional escrito) **no** va a `SavedStateHandle`
  ni a `rememberSaveable`: Android guarda ese estado fuera de la app y sin cifrar.
- **Backup:** `android:allowBackup="false"` y `res/xml/data_extraction_rules.xml` (excluye todo de la nube en
  Android 12+) se mantienen (ADR-0002). La transferencia directa a un celular nuevo está permitida.
- **Borrar todo:** borra Room, DataStore y la clave del Keystore; luego vuelve al onboarding.
- **Exportar:** archivo JSON que elige la persona con el Storage Access Framework; nunca se envía a ningún lado
  automáticamente.
- **Tiempo de pantalla:** `PACKAGE_USAGE_STATS` es opcional; sin permiso, la tarjeta lo explica (no muestra 0).
- **Ayuda humana:** "¿Necesitas hablar con alguien?" en Panbu, en Ayuda y en la bienvenida, con el componente
  `HelpLineCard` (el número vive en `HelpLine.PHONE`). Perú: Línea 113 del Minsa, "opción de salud mental" (la
  ruta exacta del menú está pendiente de confirmar con el cliente). No inventes números.
- **Notificaciones:** hoy solo existe el recordatorio de descanso (1 al día, ADR-0005). Si agregas otra, el total
  no puede pasar de 2 al día, y nunca menciona la tienda ni el contenido del diario.

## Checklist
- [ ] No hay datos personales nuevos que salgan del celular
- [ ] No hay datos personales en los logs
- [ ] Los permisos coinciden con la tabla de `docs/06`
- [ ] Hay estado vacío y no hay números inventados
- [ ] Los textos no son clínicos, no culpan y el aviso sigue visible
- [ ] Se actualizó `docs/06` o `docs/05` si cambió qué se guarda o qué se pide
