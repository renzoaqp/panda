---
name: panbu-feature
description: Pasos para implementar una funcionalidad completa en Panbu siguiendo la arquitectura del proyecto (modelo de dominio, entidad y DAO de Room, repositorio, Hilt, ViewModel, pantalla, navegación, tests y docs). Úsala al implementar cualquier tarea del plan de trabajo que toque más de una capa, o al crear una entidad, tabla, DAO, repositorio, ViewModel o ruta nueva.
---

# Implementar una funcionalidad en Panbu

Referencia viva del patrón: `feature/home/` → `HomeViewModel` → `data/repository/MoodRepository.kt` →
`data/local/Daos.kt` (`MoodDao`).

## Pasos
1. **Ubicar la tarea:** encuéntrala en `docs/01-plan-de-trabajo.md` y su comportamiento en
   `docs/02-producto.md`. Si el comportamiento no está definido, propón una definición, escríbela en `docs/02`
   y confírmala antes de programar.
2. **Revisar los guardrails:** si toca datos emocionales, permisos, notificaciones o textos de salud, usa
   también la skill `panbu-privacidad`.
3. **Dominio** (`domain/model/`): Kotlin puro, sin imports de Android. Fechas como `Instant`; los "días" se
   calculan con `LocalDate` y el `Clock` inyectado. La lógica de cálculo va aquí o en el repositorio, nunca en
   el Composable.
4. **Persistencia** (`data/local/`):
   - Entidad en `Entities.kt` con columnas en snake_case, enums guardados por `name` y fechas en epoch millis,
     más sus funciones `toDomain()` / `toEntity()`.
   - DAO en `Daos.kt`: lecturas observables con `Flow`, escrituras con `suspend`.
   - Regístrala en `@Database(entities = …)` de `PanbuDatabase`.
   - **Esquema:** ya hay APK de prueba instalados con datos reales (versión 2 desde el APK 0.3), así que **todo
     cambio sube `version` y lleva su migración**: `AutoMigration` si solo se agregan tablas o columnas con valor
     por defecto; `Migration` escrita a mano si no. Nunca `fallbackToDestructiveMigration`. Pruébala instalando
     el APK nuevo encima del anterior en el emulador.
   - Actualiza `docs/05-modelo-de-datos.md` (tabla y reglas de cálculo).
5. **Repositorio** (`data/repository/`): interfaz `XRepository` + `RoomXRepository @Inject constructor(...)`;
   binding con `@Binds @Singleton` en `di/RepositoryModule.kt`.
6. **ViewModel** (`feature/x/XViewModel.kt`): `@HiltViewModel`, `data class XUiState` inmutable, expuesto con
   `stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), XUiState())`. Los eventos son funciones
   `onAlgo()` que lanzan corrutinas en `viewModelScope`.
7. **Pantalla:** sigue la skill `panbu-ui` (`XScreen` → `XContent` + `@Preview`).
8. **Navegación:** `@Serializable data object XRoute` en `navigation/Routes.kt` y `composable<XRoute>` en
   `PanbuNavHost.kt`. Solo se agrega una pestaña nueva si `docs/02` lo indica.
9. **Tests** en `app/src/test/`: toda regla de cálculo (progreso, sumas, comparaciones, límites de día, semana
   o noche) se prueba con `Clock.fixed(...)` y casos borde (sin datos, cambio de día, división entre cero).
10. **Permisos o dependencias nuevas:** el permiso va primero a la tabla de `docs/06-guardrails.md`; la librería,
    a `gradle/libs.versions.toml` (con un ADR si es una decisión importante).
11. **Cerrar:** `./gradlew testDebugUnitTest lintDebug assembleDebug`, prueba en un celular, marca la casilla en
    el plan y actualiza los docs afectados. Si no pudiste compilar o probar, dilo.
