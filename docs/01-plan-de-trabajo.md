# Plan de trabajo — Panbu (Android)

> **Estado:** Fase 0 en curso · **Última actualización:** 2026-11-09
> Las duraciones son orientativas, para 1 desarrollador. Se ajustan al cerrar cada fase.

## Resumen

| Fase | Objetivo | Duración orientativa | Estado |
|---|---|---|---|
| 0 | Base y diseño: proyecto, tema y pantallas navegables | 1–2 semanas | En curso |
| 1 | Funciones principales: ánimo, calma, metas, diario, recordatorios, ajustes | 3–4 semanas | Pendiente |
| 2 | Estadísticas con datos reales y tiempo de pantalla | 2 semanas | Pendiente |
| 3 | Tienda: catálogo, carrito y pedido | 1–2 semanas | Pendiente |
| 4 | Pruebas y publicación en Google Play | 2–3 semanas | Pendiente |
| 5 | Conexión Bluetooth con el peluche | 2–4 semanas (depende del hardware) | Pendiente |

Cada fase termina con un APK instalable que se le muestra al cliente.

## Vía rápida (prioridad actual: un APK cuanto antes)

Decidido el 2026-11-09: **acelerar**. Las fases de abajo son el inventario completo de trabajo, pero el **orden**
lo marcan estas versiones cortas. Cada una es un APK que se instala en un celular.

| Versión | Contenido | Fuera por ahora |
|---|---|---|
| **APK 0.1** | Esqueleto compilado e instalable: tema, 5 pestañas, registro de estado de ánimo | Todo lo demás |
| **APK 0.2 — demo funcional** | Ilustraciones propias del panda · Inicio con datos reales (ánimo + tiempo de calma de hoy) · Respira con Panbu + "Necesito un momento de calma" (guardan sesiones) · Metas predefinidas con progreso automático | Cuentas, tienda, tiempo de pantalla |
| **APK 0.3** | Estadísticas reales (semana) · Mi diario emocional · Recordatorio para dejar el celular · Ajustes básicos · Onboarding con el aviso | Cuentas, tienda, tiempo de pantalla |
| **APK 0.4** | Cuentas opcionales (Firebase, correo) · Tienda con pedido por WhatsApp · Mensajes positivos y sonidos | Tiempo de pantalla, Bluetooth |
| Después | Tiempo de pantalla nocturno · pruebas y publicación (Fase 4) · Bluetooth (Fase 5) | — |

Reglas de la vía rápida: los guardrails no se saltan (sobre todo los de salud y datos reales), pero se puede
dejar para después lo que no se ve en la demo: migraciones (antes de publicar se edita la v1), tema oscuro y
pulido de accesibilidad avanzada.

**Una tarea está terminada cuando:** compila, los tests pasan, se probó en un celular real, respeta los
[guardrails](06-guardrails.md) y la documentación quedó actualizada.

---

## Fase 0 — Base y diseño

**Objetivo:** app instalable con la identidad visual de Panbu, las 5 pestañas y las pantallas del mockup con
datos de ejemplo (solo en compilaciones de depuración).

### Hecho
- [x] Estructura del repositorio y documentación (`docs/`)
- [x] Contexto y reglas para Claude Code (`CLAUDE.md`, `.claude/skills/`)
- [x] Esqueleto Android: Gradle + wrapper, tema Panbu (colores, formas, tipografía base), navegación con barra
      inferior, pantallas base, Room, Hilt, interfaz `PanbuDevice`
- [x] Ejemplo completo de una función de punta a punta: registro de estado de ánimo en Inicio
      (UI → ViewModel → repositorio → Room)

### Entorno (VS Code + línea de comandos; Android Studio es opcional)
Detalle en [03-arquitectura.md › Entorno de desarrollo](03-arquitectura.md#entorno-de-desarrollo).
- [ ] JDK 21 y Android SDK (command-line tools) instalados; `ANDROID_HOME` configurado
- [ ] `adb` reconoce un celular Android con "Depuración USB" activada
- [ ] Primer `./gradlew assembleDebug` exitoso y app instalada con `./gradlew installDebug`
- [ ] Tests de captura de pantalla (Roborazzi) para ver las pantallas sin Android Studio
- [ ] Hacer commit de `app/schemas/` (lo genera Room en la primera compilación)

### Diseño
- [ ] Tipografía Nunito en `res/font` (licencia OFL) conectada en `ui/theme/Type.kt`
- [ ] Logo vectorial definitivo e ícono de la app (hoy hay uno provisional)
- [ ] Crear las ilustraciones propias del panda en vector. No hay ilustrador, así que las diseñamos nosotros
      (lista en [04-diseno-visual.md](04-diseno-visual.md#ilustraciones-necesarias))
- [ ] Componentes: fila de meta, tarjeta de métrica, botón principal, selector Semana/Mes/Total,
      gráfico de barras, gráfico de dona
- [ ] Inicio igual al mockup
- [ ] Estadísticas igual al mockup
- [ ] Metas igual al mockup
- [ ] Diseñar **Panbu, tu espacio de calma** (no está en el mockup; ver [02-producto.md §4](02-producto.md#4-panbu-tu-espacio-de-calma))
- [ ] Diseñar **Ajustes** (no está en el mockup; ver [02-producto.md §6](02-producto.md#6-ajustes))
- [ ] Tienda "Agregar compra" igual al mockup (se entra desde la pestaña Panbu)
- [ ] Estados vacíos de todas las pantallas
- [ ] `DemoData` en el source set `app/src/debug/` para la demo con pantallas llenas (nunca en release)

**Terminado cuando:** el cliente navega todas las pantallas en un celular real y aprueba el diseño.

---

## Fase 1 — Funciones principales

**Objetivo:** que cada botón haga algo real y guarde datos reales.

- [ ] Onboarding: bienvenida en 3 pasos, crear cuenta / iniciar sesión / continuar sin cuenta, nombre, aviso
      "Panbu no sustituye a un profesional"
- [ ] Cuentas de usuario ([ADR-0004](decisiones/0004-cuentas-de-usuario.md)): registro, inicio de sesión con Google y
      con correo, recuperar contraseña, perfil, cerrar sesión
- [ ] Borrar la cuenta desde la app y desde una página web (requisito de Google Play)
- [ ] Estado de ánimo: 5 estados y nota opcional; Inicio muestra el último del día
- [ ] Respira con Panbu: animación de respiración, patrones (4-4-4-4, 4-7-8, suave 4-6), duración 1/3/5 min;
      guarda una `CalmSession`
- [ ] Botón "Necesito un momento de calma": un toque → 1 minuto de respiración suave
- [ ] Tiempo contigo: iniciar y terminar un momento de calma (fuente `MANUAL`)
- [ ] Metas: crear (+), editar, pausar y archivar; tipos predefinidos y personalizada; progreso del día o semana
- [ ] Mi diario emocional: crear, editar y borrar; texto cifrado; bloqueo opcional con huella o PIN
- [ ] Mensajes positivos: colección local y un mensaje destacado por día
- [ ] Un momento para ti: 5–8 microactividades (estirarse, tomar agua, ejercicio 5-4-3-2-1…)
- [ ] Sonidos relajantes: 3–5 audios con licencia libre, en bucle, con temporizador de apagado
- [ ] Acompáñame a descansar: rutina guiada a la hora de descanso; registra un `SleepLog`
- [ ] Recordatorios: notificación para dejar el celular (hora de descanso − N min), máximo 2 al día
- [ ] Ajustes: perfil local (nombre y foto), notificaciones, horario de descanso, recordatorios, sonido preferido,
      tema claro/oscuro/sistema, privacidad (exportar y borrar datos), ayuda (con línea de ayuda), aviso legal
- [ ] Tests unitarios del progreso de metas y de la duración de sesiones

**Terminado cuando:** cada función guarda y recupera datos reales después de cerrar y abrir la app, y los
tests pasan.

---

## Fase 2 — Estadísticas reales

**Objetivo:** que los gráficos muestren solo lo que la persona registró (nunca datos inventados).

- [ ] Consultas por periodo: Semana (barras por día), Mes (barras por semana), Total (barras por mes)
- [ ] Gráfico de barras y gráfico de dona dibujados con Canvas, con descripción para lectores de pantalla
- [ ] Comparación con el periodo anterior (si no hay periodo anterior, no se muestra porcentaje)
- [ ] Tiempo de pantalla nocturno con `UsageStatsManager`: pantalla que explica para qué sirve → Ajustes del
      sistema; sin permiso, la tarjeta lo explica (no muestra 0)
- [ ] Resumen nocturno diario guardado cada mañana con WorkManager (Android solo conserva los eventos de uso
      unos días)
- [ ] Progreso automático de metas: celular de noche, sueño, momentos de calma, respiración
- [ ] Tests con datos conocidos → valores esperados en cada gráfico

**Terminado cuando:** con un conjunto de datos de prueba, cada número y barra coincide con el valor calculado
a mano.

---

## Fase 3 — Tienda

**Objetivo:** que alguien pueda pedir su Panbu desde la app.

- [ ] Catálogo local (JSON en `assets/`): peluche, 3 colores, accesorios, precios reales, información de envío
- [ ] Pantalla "Agregar compra": el color cambia la foto, accesorios opcionales, cantidad de 1 a 10
- [ ] Carrito con resumen y total
- [ ] Pedido v1: mensaje prellenado por WhatsApp o enlace a una tienda web ([ADR-0003](decisiones/0003-tienda-pedido-externo.md))
- [ ] (Opcional, v2) pasarela de pago peruana + backend — requiere un ADR nuevo
- [ ] La tienda nunca aparece en flujos de calma ni en notificaciones (guardrail)

**Terminado cuando:** un pedido de prueba llega al vendedor con producto, color, accesorios, cantidad y total
correctos.

---

## Fase 4 — Pruebas y publicación

- [ ] Pruebas en al menos 3 celulares: gama baja (Android 8–10), gama media y uno reciente (Android 15/16)
- [ ] Pantallas pequeñas, fuente grande, modo oscuro, giro de pantalla
- [ ] Accesibilidad con TalkBack
- [ ] Revisión de privacidad con la checklist de [06-guardrails.md](06-guardrails.md#checklist-antes-de-publicar)
- [ ] Firma de release; keystore y contraseñas fuera del repositorio y con respaldo
- [ ] Política de privacidad publicada en una URL pública
- [ ] Play Console: cuenta de desarrollador, ficha, capturas, "Seguridad de los datos", declaración de app de
      salud, clasificación de contenido
- [ ] Prueba cerrada: las cuentas personales nuevas de Play Console deben tener 12 testers durante 14 días antes
      de pasar a producción (verificar la regla vigente al momento de publicar)
- [ ] Publicación en producción

---

## Fase 5 — Bluetooth con el peluche

Detalle técnico en [07-bluetooth.md](07-bluetooth.md).

- [ ] Definir con el equipo de electrónica: placa, sensores, batería y protocolo
- [ ] `BlePanbuDevice` detrás de la interfaz `PanbuDevice` (sin tocar las pantallas)
- [ ] Emparejamiento con `CompanionDeviceManager`
- [ ] Reconexión automática y sincronización de los eventos guardados en el peluche
- [ ] Abrazos → "Tiempo contigo" automático (`CalmSession` con fuente `DEVICE`)
- [ ] Guía de respiración en el peluche (vibración o luz) sincronizada con la app
- [ ] Estado del peluche (conectado, batería) en la pestaña Panbu
- [ ] Pruebas con el prototipo físico

---

## Decisiones

Los números no cambian, porque otros documentos y el código los citan ("decisión pendiente #4").

| # | Pregunta | Estado | Qué afecta | Se necesita antes de |
|---|---|---|---|---|
| 1 | ¿Solo Android, o también iPhone más adelante? | ✅ **Solo Android** (2026-11-09) | [ADR-0001](decisiones/0001-kotlin-jetpack-compose.md) | — |
| 2 | Nombre de paquete definitivo (hoy `com.panbu.app`) | Pendiente | No se puede cambiar después de publicar | Fase 4 |
| 3 | Ilustraciones del panda y fotos reales del peluche en 3 colores | ✅ **Las ilustraciones las creamos nosotros** (2026-11-09). ⏳ Las fotos reales del peluche para la tienda siguen pendientes | Diseño y tienda | Fase 0 / Fase 3 |
| 4 | ¿Habrá cuentas de usuario? | ✅ **Sí** (2026-11-09); los detalles son #11–#13 | [ADR-0004](decisiones/0004-cuentas-de-usuario.md) | — |
| 5 | Precio real, accesorios definitivos, ¿existe el accesorio de calor? | Pendiente | Tienda | Fase 3 |
| 6 | Canal de venta: WhatsApp, tienda web o pasarela de pago | Pendiente | Tienda | Fase 3 |
| 7 | Hardware del peluche (placa, sensores, batería) | Pendiente | Bluetooth | Fase 5 |
| 8 | Audios relajantes: origen y licencia | Pendiente | Sonidos | Fase 1 |
| 9 | Textos legales: política de privacidad y términos | Pendiente | Publicación | Fase 4 |
| 10 | Línea de ayuda a mostrar (verificar que esté vigente) | Pendiente | Ayuda | Fase 1 |
| 11 | Proveedor de cuentas y formas de iniciar sesión | Propuesta: Firebase Authentication **con correo primero** (Google después); los usuarios de prueba se crean en la consola de Firebase. Las cuentas llegan en el APK 0.4 | ADR-0004 | APK 0.4 |
| 12 | ¿La cuenta es obligatoria o se puede usar la app sin cuenta? | ✅ **Opcional** (2026-11-09) | ADR-0004, onboarding | — |
| 13 | ¿Los datos emocionales se sincronizan con la cuenta? | ✅ **No, al inicio no** (2026-11-09); más adelante, quizás una copia cifrada opcional | ADR-0002, privacidad | — |

## Riesgos

| Riesgo | Mitigación |
|---|---|
| La persona no da el permiso de uso → no hay tiempo de pantalla | Función opcional; la meta de celular también se puede registrar a mano |
| Google Play revisa con más cuidado las apps de salud mental | Textos sin promesas médicas, aviso visible, política de privacidad clara |
| No llegan a tiempo las ilustraciones o fotos reales | Imágenes provisionales; la tienda no se publica sin fotos reales |
| El hardware del peluche cambia | La interfaz `PanbuDevice` aísla a la app del protocolo |
| Los pagos integrados son complejos | Pedido externo en la v1 |
| Las cuentas traen obligaciones (borrado de cuenta, política de privacidad, "Seguridad de los datos") | Guardar en la nube solo lo mínimo; el borrado de cuenta se hace desde el primer día |
| Las ilustraciones hechas por nosotros no se parecen al peluche real | Basarlas en el mockup; la tienda siempre usa fotos reales |
