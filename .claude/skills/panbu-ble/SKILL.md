---
name: panbu-ble
description: Implementar o depurar la conexión Bluetooth LE entre la app Panbu y el peluche (Fase 5) - emparejamiento con CompanionDeviceManager, GATT, permisos de Bluetooth, sincronización de abrazos y guía de respiración. Úsala al tocar el paquete device/, PanbuDevice, BlePanbuDevice, el protocolo GATT o cualquier cosa del peluche físico.
---

# Bluetooth con el peluche Panbu

Diseño y protocolo propuesto: `docs/07-bluetooth.md`. **El hardware todavía no está definido:** antes de
implementar, confirma con la persona usuaria qué placa, qué UUIDs y qué formato de datos usa el firmware. No
inventes UUIDs ni formatos y los des por definitivos.

## Reglas de arquitectura
1. Las pantallas y ViewModels **solo** conocen la interfaz `device/PanbuDevice.kt`. Nada de `BluetoothGatt` fuera
   de `device/ble/`.
2. Implementaciones: `NoPanbuDevice` (sin peluche), `FakePanbuDevice` (solo en `src/debug`, simula abrazos para
   la UI) y `BlePanbuDevice` (real). Se cambian en `di/DeviceModule.kt`.
3. Los eventos del peluche se convierten en `CalmSession(source = DataSource.DEVICE, type = MOMENT_WITH_PANBU)`
   en un `DeviceSyncRepository`, nunca dentro de la capa BLE.
4. Si cambia la interfaz `PanbuDevice`, actualiza `docs/07`.

## Reglas de Android BLE
- **Emparejamiento** con `CompanionDeviceManager` + `BluetoothLeDeviceFilter` por el UUID del servicio Panbu.
  Guarda el id de la asociación en DataStore.
- **Permisos** (agrégalos a la tabla de `docs/06-guardrails.md` antes que al manifest): `BLUETOOTH_SCAN` con
  `android:usesPermissionFlags="neverForLocation"` y `BLUETOOTH_CONNECT` (Android 12+). En Android 8–11,
  `BLUETOOTH` y `BLUETOOTH_ADMIN` con `maxSdkVersion="30"`. Se piden al emparejar, nunca al abrir la app.
- **GATT:** una sola operación a la vez (cola con `Mutex` o `Channel`), con timeout y reintentos. Llama a
  `requestMtu` después de conectar. Habilita las notificaciones escribiendo el descriptor CCCD `0x2902`.
  Llama siempre a `close()` al desconectar.
- **Reconexión:** `connectGatt(context, autoConnect = true, …)` para un dispositivo ya conocido; backoff
  exponencial si falla.
- **Sincronización:** al conectar, primero sincroniza la hora (Time Sync), luego descarga el Event Log
  pendiente y por último se suscribe a Hug Events. Los eventos se deduplican por timestamp.
- **Versión del protocolo:** lee `Protocol Version` antes que todo; si no es compatible, muestra "Actualiza la
  app o el peluche" y no sigas.
- **Errores:** el estado `GATT 133` y las desconexiones son normales: reintenta y no muestres errores técnicos
  a la persona.

## Pruebas
- Celular físico (el emulador no sirve para BLE). Mientras no exista el hardware, simula el peluche con
  **nRF Connect** (GATT server) desde otro celular.
- Prueba la lógica de parseo de paquetes y la deduplicación con tests unitarios, usando arrays de bytes de
  ejemplo.
- Casos: Bluetooth apagado, permiso denegado, peluche fuera de rango, batería baja, reconexión después de
  reiniciar el celular.
