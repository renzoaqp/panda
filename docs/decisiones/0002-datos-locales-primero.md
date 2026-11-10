# ADR-0002: Datos guardados en el celular (local primero)

- **Estado:** Aceptada
- **Fecha:** 2026-11-09

## Contexto
Panbu guarda información emocional sensible: estados de ánimo, un diario, horas de sueño y uso del celular. La
guía del cliente pide proteger esa información, recoger solo lo necesario y que la persona controle qué se
guarda. Todavía no está decidido si habrá cuentas de usuario.

## Decisión
- Todo se guarda en el celular: Room (`panbu.db`) y DataStore. No hay servidor ni cuentas por ahora.
- `android:allowBackup="false"`: los datos no se suben al backup de Google. En Android 12+ la transferencia
  directa de un celular a otro (por cable o Wi-Fi directo) sigue siendo posible, y se acepta porque es local y
  la inicia la persona.
- El texto del diario se cifra con AES-GCM y una clave guardada en Android Keystore (Fase 1).
- Ajustes ofrece exportar los datos y borrarlo todo.

## Consecuencias
- Máxima privacidad y una política de privacidad simple; no hay costos de servidor.
- Si la persona cambia de celular sin usar la transferencia directa, pierde su historial salvo que lo haya
  exportado. Se mitiga con la opción de exportar.
- Si en el futuro se agregan cuentas o sincronización, se necesita un ADR nuevo, consentimiento explícito y
  cifrado de extremo a extremo para el diario.

## Alternativas consideradas
- **Firebase (Auth + Firestore):** facilita la sincronización, pero saca datos emocionales del celular y agrega
  un tercero. Se descarta mientras no haga falta.
- **SQLCipher para toda la base de datos:** es más completo, pero agrega una librería nativa y aumenta el tamaño
  de la app. Por ahora basta con cifrar el campo del diario; se puede revisar más adelante.
