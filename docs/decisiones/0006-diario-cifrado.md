# ADR-0006: Diario cifrado con AES-GCM y clave en Android Keystore

- **Estado:** Aceptada
- **Fecha:** 2026-11-10

## Contexto

"Mi diario emocional" guarda lo más privado que la persona escribe. Los guardrails piden:

- Regla 4: el diario **nunca se analiza**, ni en el celular ni fuera de él.
- Regla 11: los datos emocionales se quedan en el celular.
- Regla 13: el diario se **cifra** (AES-GCM con clave en Android Keystore) y el backup en la nube está apagado.
- Regla 15: nunca escribir el texto del diario en los logs.

Además, ya hay APK de prueba (0.1 y 0.2) instalados con datos reales, así que agregar la tabla no puede borrar ni
romper la base existente.

## Decisión

1. **Cifrado:** cada entrada se cifra con **AES-256-GCM** antes de guardarse. La clave (`panbu_journal_v1`) se crea
   dentro de **Android Keystore** y nunca sale de él. Cada entrada tiene su propio IV, que se guarda junto al
   texto cifrado. Código: `data/crypto/TextCipher.kt` (`KeystoreTextCipher`).
2. **Tabla `journal_entries`** en Room (versión 2 del esquema) con `text_encrypted` y `iv` como BLOB. La fecha y el
   estado de ánimo opcional van sin cifrar (sirven para ordenar y mostrar la lista, y no dicen lo que se escribió).
3. **Migración automática 1 → 2** (`AutoMigration`), porque solo se agrega una tabla. Room la genera comparando los
   esquemas exportados en `app/schemas/`.
4. **Entradas ilegibles:** si una entrada no se puede descifrar (por ejemplo, porque Android perdió la clave), se
   muestra "No se pudo leer esta entrada." y se puede borrar. La app no se cierra.
5. **El texto que se está escribiendo no va al `SavedStateHandle`:** Android guarda ese estado fuera de la app y
   sin cifrar. Si Android cierra la app en segundo plano a mitad de una entrada, se pierde lo que no se guardó; para
   no perderlo por accidente, salir con cambios pide confirmación ("¿Salir sin guardar?").
6. **Borrar todos mis datos** vacía la tabla **y borra la clave** del Keystore.
7. El estado de ánimo del diario **no se suma a Estadísticas**: ahí solo cuenta lo que se elige en Inicio.

## Consecuencias

- Aunque alguien copiara el archivo `panbu.db` (con un celular con root, por ejemplo), no podría leer el diario
  sin la clave, que está protegida por el hardware del celular. Se verificó en el emulador: el texto no aparece en
  los archivos de la base.
- El diario no va a la copia de seguridad en la nube (`allowBackup="false"` y `data_extraction_rules.xml`). En la
  **transferencia directa a un celular nuevo**, que el ADR-0002 permite, la base viaja pero la clave del Keystore
  no: en el celular nuevo esas entradas se ven como "No se pudo leer esta entrada." y se pueden borrar. Los demás
  datos (ánimo, sesiones, metas) sí se leen. **Pregunta abierta para el responsable del proyecto:** si se prefiere
  que el diario no viaje en absoluto, hay que excluir la base de la transferencia (y con ella todos los datos).
- Si en el futuro se ofrece exportar o respaldar el diario, hará falta un ADR nuevo (y el consentimiento de la
  persona).
- **Pendiente:** bloqueo opcional con huella o PIN (`androidx.biometric`, permiso `USE_BIOMETRIC`, ya en la tabla de
  permisos). Se puede agregar sin cambiar el cifrado.
- **Pendiente antes de publicar:** test de la migración con `MigrationTestHelper` (Fase 4). La migración ya se probó a
  mano: el APK 0.3 se instaló sobre el 0.2 en el emulador y conservó los estados de ánimo y las sesiones.

## Alternativas consideradas

- **SQLCipher (cifrar toda la base):** suma una librería nativa grande y una contraseña que habría que guardar en
  algún lado (otra vez el Keystore). Para proteger solo el texto del diario, cifrar ese campo es más simple.
- **`androidx.security:security-crypto` (EncryptedSharedPreferences/EncryptedFile):** Google la marcó como obsoleta
  en 2025; además no sirve para filas de Room.
- **Clave que exige autenticación (`setUserAuthenticationRequired`)** para cada lectura: obligaría a desbloquear con
  huella cada vez y se invalida si la persona cambia su bloqueo de pantalla (perdería el diario). El bloqueo con
  huella se hará en la interfaz, sin atar la clave a él.
