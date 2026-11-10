# Bluetooth con el peluche (Fase 5) — diseño preliminar

> **Estado:** borrador. Depende del hardware que defina el equipo de electrónica. La app ya está preparada:
> la interfaz `PanbuDevice` (`app/src/main/java/com/panbu/app/device/`) aísla a las pantallas del protocolo.

## Qué podría hacer el peluche (supuestos a validar)

| Función | Sensor / actuador | Qué hace la app |
|---|---|---|
| Detectar abrazos | Sensor de presión o capacitivo | Registra "Tiempo contigo" automáticamente (`CalmSession` con `source = DEVICE`) |
| Guía de respiración | Motor de vibración o LED que "respira" | La app envía el patrón (4-7-8, etc.) y el peluche lo acompaña |
| Calor suave | Resistencia con control de temperatura | Encender/apagar con límite de tiempo — **solo con certificación de seguridad** |
| Batería | Medición de la batería | Se muestra en la pestaña Panbu |

## Arquitectura en la app

```
Pantallas / ViewModels
        │  (solo conocen la interfaz)
        ▼
  PanbuDevice  ◄── NoPanbuDevice    (hoy: no hay peluche)
               ◄── FakePanbuDevice  (debug: simula abrazos para probar la UI)
               ◄── BlePanbuDevice   (Fase 5: Bluetooth real)
```

- `connectionState: StateFlow<DeviceConnectionState>`: no emparejado, conectando, conectado (con batería),
  desconectado.
- `events: Flow<DeviceEvent>`: abrazo iniciado, abrazo terminado, batería.
- Comandos: `startBreathingGuide(pattern)` y `stopBreathingGuide()`.
- Un `DeviceSyncRepository` (Fase 5) convierte los eventos en `CalmSession` y los guarda en Room.
- Para cambiar de implementación solo se modifica el binding en `di/DeviceModule.kt`.

## Android

- **BLE** (Bluetooth Low Energy), no Bluetooth clásico.
- **Emparejamiento con `CompanionDeviceManager`:** muestra el diálogo del sistema filtrado por el UUID del
  servicio Panbu. En Android 8–11 no hace falta el permiso de ubicación y permite reconectar en segundo plano.
- **Permisos:** `BLUETOOTH_SCAN` (con `neverForLocation`) y `BLUETOOTH_CONNECT` en Android 12+.
- **Conexión:** `BluetoothGatt` con reconexión automática. Las operaciones GATT se ejecutan en cola (una a la
  vez), con timeouts y reintentos.
- **Sincronización:** el peluche guarda los eventos con su hora cuando no está conectado; al conectarse, la
  app los descarga. Así no hace falta un servicio en primer plano permanente.
- **Pruebas:** se necesita un celular físico (el emulador no sirve para BLE). La app **nRF Connect** permite
  simular el peluche desde otro celular mientras no exista el hardware.

## Protocolo GATT (propuesta inicial)

| Servicio / característica | UUID | Propiedades | Contenido |
|---|---|---|---|
| Device Information | `0x180A` (estándar) | Read | Fabricante, modelo, versión de firmware |
| Battery | `0x180F` (estándar) | Read, Notify | % de batería |
| **Panbu Service** | 128 bits propio (por definir) | — | — |
| ├ Protocol Version | propio | Read | 1 byte, para la compatibilidad entre app y firmware |
| ├ Hug Events | propio | Notify | `{tipo, timestamp}` en tiempo real |
| ├ Event Log | propio | Read / Indicate | Eventos guardados sin conexión, por índice |
| ├ Breathing Command | propio | Write | `{inhala, sostén, exhala, sostén, duración}` |
| └ Time Sync | propio | Write | Hora actual, para que el peluche ponga la hora a sus eventos |

## Preguntas para el equipo de hardware

1. ¿Qué placa? (ESP32-C3/S3, nRF52…)
2. ¿Qué sensores y actuadores lleva? ¿Dónde van dentro del peluche?
3. ¿Batería? ¿Cuánto dura? ¿Cómo se carga? ¿Se puede lavar el peluche?
4. ¿Tiene reloj? ¿Guarda eventos cuando no está conectado? ¿Cuántos?
5. ¿Se podrá actualizar el firmware por Bluetooth (OTA)?
6. Certificaciones: seguridad de juguetes, baterías y del accesorio de calor.
