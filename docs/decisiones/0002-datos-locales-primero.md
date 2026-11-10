# ADR-0002: Datos guardados en el celular (local primero)

- **Estado:** Aceptada. Modificada por [ADR-0004](0004-cuentas-de-usuario.md): habrá cuentas, pero los datos
  emocionales siguen en el celular.
- **Fecha:** 2026-11-09

## Contexto
Panbu guarda información emocional sensible: estados de ánimo, un diario, horas de sueño y uso del celular. La
guía del cliente pide proteger esa información, recoger solo lo necesario y que la persona controle qué se
guarda.

## Decisión
- Los datos emocionales se guardan en el celular: Room (`panbu.db`) y DataStore. Lo único que va a la nube es la
  cuenta (correo, nombre y foto opcional), según el ADR-0004.
- `android:allowBackup="false"`: los datos no se suben al backup de Google. En Android 12+ la transferencia
  directa de un celular a otro (por cable o Wi-Fi directo) sigue siendo posible, y se acepta porque es local y
  la inicia la persona. Desde el APK 0.3, `res/xml/data_extraction_rules.xml` excluye todo de la copia en la
  nube de forma explícita (en Android 12+ `allowBackup` está obsoleto) y deja permitida la transferencia directa.
- El texto del diario se cifra con AES-GCM y una clave guardada en Android Keystore (Fase 1).
- Ajustes ofrece exportar los datos y borrarlo todo.

## Consecuencias
- Máxima privacidad y una política de privacidad simple; no hay costos de servidor.
- Si la persona cambia de celular sin usar la transferencia directa, pierde su historial salvo que lo haya
  exportado. Se mitiga con la opción de exportar.
- Las cuentas ya están decididas (ADR-0004). Sincronizar o respaldar los datos emocionales en la nube requiere
  otro ADR, consentimiento explícito y cifrado de extremo a extremo.

## Alternativas consideradas
- **Firebase (Auth + Firestore):** facilita la sincronización, pero saca datos emocionales del celular y agrega
  un tercero. Se descarta mientras no haga falta.
- **SQLCipher para toda la base de datos:** es más completo, pero agrega una librería nativa y aumenta el tamaño
  de la app. Por ahora basta con cifrar el campo del diario; se puede revisar más adelante.
