# Producto — Panbu

Basado en la guía del cliente ([`00info/Guia_de_la_aplicacion_Panbu.pdf`](../00info/Guia_de_la_aplicacion_Panbu.pdf))
y el mockup ([`00info/WhatsApp Image 2026-11-09 at 22.18.13.jpeg`](../00info/)). Este documento completa lo que la
guía deja abierto. Lo marcado como **(supuesto)** debe confirmarlo el cliente.

## Propósito

**Panbu — Tu calma, siempre contigo.** Acompañar a la persona a crear momentos de calma, desarrollar hábitos
saludables y tener rutinas nocturnas con menos pantalla. Lo que la diferencia de otras apps es que combina un
**producto físico (el peluche Panbu)** con una experiencia digital de bienestar. En la fase final, la app y el
peluche se conectan por Bluetooth.

### Objetivos (de la guía)
1. Ayudar a crear momentos de tranquilidad.
2. Promover hábitos saludables para el bienestar emocional.
3. Motivar a reducir el uso del celular por la noche.
4. Ayudar a organizar rutinas de descanso.
5. Crear un vínculo entre la persona y su peluche Panbu.

### Para quién (supuesto)
Jóvenes y adultos que quieren manejar el estrés o la ansiedad del día a día, que usan mucho el celular antes
de dormir o que tienen o quieren un peluche Panbu. **No** está pensada para atender crisis ni para reemplazar
una terapia.

## Principios del producto

1. **Acompaña, no trata.** Panbu no diagnostica ni promete curar. Siempre está a la vista que no sustituye a un
   profesional.
2. **Sin presión.** Metas pequeñas, mensajes amables, nada de castigos ni colores de error.
3. **Datos reales.** Los gráficos muestran solo lo que la persona registró.
4. **Privado por defecto.** Los datos emocionales se quedan en el celular.
5. **Calma primero.** En los momentos de calma no se vende nada.

Las reglas completas están en [06-guardrails.md](06-guardrails.md).

## Mapa de navegación

```
Barra inferior:  Inicio · Estadísticas · Metas · Panbu · Ajustes

Inicio ──────────► Modo calma (Respira con Panbu)
   │  └──────────► Detalle de meta
   └─ ⚙ ──────────► Ajustes
Metas ─── (+) ───► Crear meta
Panbu ───────────► Respira · Un momento para ti · Mensajes · Sonidos · Diario · Descansar
   ├─────────────► "Necesito un momento de calma" (1 toque → respiración de 1 minuto)
   └─────────────► Tienda (Agregar compra) ──► Carrito ──► Pedido
Primera vez ─────► Onboarding ──► Inicio
```

---

## 1. Inicio

Primera pantalla al abrir la app: cómo se siente la persona, qué puede hacer y sus objetivos del día.

| Elemento | Comportamiento |
|---|---|
| Logo + "Tu calma, siempre contigo" | Fijo. Engranaje arriba a la derecha → Ajustes |
| Saludo | "Hola, {nombre}" si hay nombre; si no, "Hola". **(supuesto)** Cambia según la hora: "Buen día", "Buenas tardes", "Buenas noches" |
| ¿Cómo te sientes hoy? | 5 estados: Tranquilo, Ansioso, Triste, Estresado, Feliz. Al tocar uno se guarda un `MoodEntry`. Se puede registrar varias veces al día; Inicio muestra el último. Tocar el mismo estado otra vez no crea un registro duplicado |
| Imagen del peluche | Ilustración de Panbu. **(supuesto)** Cambia de pose según el estado elegido (p. ej., abrazo si está triste) |
| Tu Panbu hoy → Tiempo contigo | Suma de las sesiones de calma de hoy y diferencia con ayer ("↑ 45 min que ayer"). Si ayer no hubo sesiones, no se muestra la comparación |
| Modo calma | Abre "Respira con Panbu" |
| Tus objetivos de hoy | Metas activas con su progreso de hoy. Tocar una → detalle. Meta cumplida → check verde |

**Estado vacío:** sin estado de ánimo → "Elige cómo te sientes". Sin metas → tarjeta "Crea tu primera meta". Sin
sesiones → "Aún no tienes momentos de calma hoy. ¿Respiramos un minuto?".

## 2. Estadísticas

| Elemento | Comportamiento |
|---|---|
| Semana / Mes / Total | Semana = lunes a domingo actual. Mes = mes calendario. Total = desde el primer registro |
| Tiempo de calma | Suma de las sesiones de calma del periodo + % frente al periodo anterior |
| Gráfico de barras | Semana: minutos por día. Mes: por semana. Total: por mes |
| ¿Cómo te sientes? | Dona con el % de cada estado de ánimo del periodo; en el centro, el estado más frecuente |
| Tiempo de pantalla (noche) | Promedio diario de uso del celular dentro del horario de descanso. Requiere el permiso "Acceso a datos de uso"; sin permiso, la tarjeta explica para qué sirve y cómo activarlo |
| Comparación | "↑ 32 % que la semana anterior". En verde si mejora y en gris neutro si empeora (nunca en rojo). Sin periodo anterior → no se muestra |

**Estado vacío:** "Cuando registres tus momentos de calma y cómo te sientes, aquí verás tu avance."

## 3. Metas

| Elemento | Comportamiento |
|---|---|
| Título "Metas" + botón (+) | El (+) abre "Crear meta" |
| Menos tiempo de celular en la noche | Límite en minutos por noche. La barra muestra lo usado frente al límite ("1 h 12 min / 2 h", 60 %). Pasar el límite pinta la barra de rosa suave, sin mensajes de culpa |
| Momentos de calma con Panbu | Cantidad de sesiones por día ("3 / 3 veces") |
| Respiración y mindfulness | Cantidad de sesiones de respiración por día |
| Dormir mejor | Horas de sueño frente al objetivo ("7 h 10 min / 8 h") |
| Barras de progreso | Porcentaje redondeado hacia abajo (7 h 10 min de 8 h = 89 %, como en el mockup) |
| Mensaje motivador | "Pequeños hábitos, grandes cambios. Panbu siempre contigo" |

**Crear meta:** tipo (predefinido o personalizada), objetivo (número + unidad), frecuencia (diaria o semanal),
recordatorio opcional. Se pueden editar, pausar y archivar. **No se borran los registros** al archivar una meta.

## 4. Panbu, tu espacio de calma

No aparece en el mockup; se diseña en la Fase 0 con el mismo estilo. Es la sección más importante de la marca.

| Elemento | Comportamiento |
|---|---|
| **Necesito un momento de calma** | Botón grande y siempre visible. Un toque → 1 minuto de respiración suave, sin pasos previos |
| Respira con Panbu | Panda que "se infla" al inhalar y "se desinfla" al exhalar. Patrones: 4-4-4-4 (caja), 4-7-8, suave 4-6. Duración: 1, 3 o 5 min. Vibración suave opcional. Al terminar guarda una `CalmSession` |
| Un momento para ti | 5–8 microactividades de 1–3 min: estirarse, tomar agua, 5-4-3-2-1 con los sentidos, escribir 3 cosas buenas… |
| Mensajes positivos | Frases cortas de acompañamiento; una destacada por día; se pueden marcar como favoritas |
| Sonidos relajantes | Lluvia, naturaleza, ruido blanco suave y música ambiental; bucle y temporizador de apagado (15/30/60 min) |
| Mi diario emocional | Entradas privadas con fecha, estado de ánimo opcional y texto. Cifrado. Bloqueo opcional con huella o PIN |
| Acompáñame a descansar | Rutina a la hora de descanso: aviso → respiración → "deja el celular" → modo descanso (pantalla oscura con el panda dormido). Al día siguiente se pregunta "¿A qué hora despertaste?" para el `SleepLog` |
| Conoce a tu Panbu | Tarjeta que lleva a la tienda. Es la única entrada a la tienda |
| Estado del peluche (Fase 5) | Conectado o desconectado y batería |
| Aviso | "Panbu te acompaña a practicar hábitos de bienestar, pero no sustituye la atención de un profesional." + enlace a "¿Necesitas hablar con alguien?" |

## 5. Tienda — Agregar compra

| Elemento | Comportamiento |
|---|---|
| Título "Agregar compra" + volver | Pantalla secundaria (sin barra inferior) |
| Foto del peluche | Cambia según el color elegido. **Deben ser fotos reales del producto** |
| Nombre, precio, descripción | "Panbu — Tu peluche de calma". Precio real en soles (el S/ 129.00 del mockup es de ejemplo) |
| Color | Clásico, Rosado, Gris |
| Accesorios (opcional) | Funda de viaje, Tarjeta de motivación, Batería de calor suave (solo si se fabrica con materiales seguros y certificados) |
| Cantidad | De 1 a 10 |
| Envío | Texto informativo ("Llega en 3–5 días") |
| Agregar al carrito | Agrega al carrito → resumen → pedido ([ADR-0003](decisiones/0003-tienda-pedido-externo.md)) |

## 6. Ajustes

No aparece en el mockup; se diseña en la Fase 0.

| Opción | Detalle |
|---|---|
| Editar perfil | Nombre y foto (solo en el celular) |
| Notificaciones | Activar/desactivar; máximo 2 al día |
| Horario de descanso | Hora de inicio y fin (por defecto 22:00–06:00). Lo usan los recordatorios, el tiempo de pantalla y "Acompáñame a descansar" |
| Recordatorios para dejar el celular | Cuántos minutos antes del horario de descanso |
| Sonidos de relajación | Sonido preferido |
| Tema | Claro, oscuro o el del sistema |
| Privacidad y mis datos | Qué se guarda, exportar mis datos, borrar todo, permisos activos |
| Ayuda y soporte | Preguntas frecuentes, contacto, "¿Necesitas hablar con alguien?" (línea de ayuda) |
| Aviso legal | Política de privacidad y términos |
| Mi cuenta | Correo, iniciar o cerrar sesión, borrar la cuenta ([ADR-0004](decisiones/0004-cuentas-de-usuario.md)). Sin cuenta: "Crear cuenta / Iniciar sesión" |

## 7. Onboarding, cuenta y permisos

- Primera vez: 3 pantallas (qué es Panbu, cómo te acompaña, tu privacidad) → **Crear cuenta / Iniciar sesión /
  Continuar sin cuenta** → nombre → aviso de que no sustituye a un profesional → Inicio.
- **Cuenta** ([ADR-0004](decisiones/0004-cuentas-de-usuario.md)): con Google o con correo y contraseña. Al crearla
  se explica en una frase qué se guarda en la nube (correo, nombre y foto opcional) y que los datos emocionales
  se quedan en el celular. Se puede cerrar sesión y borrar la cuenta desde Ajustes.
- **La cuenta es opcional** (decidido el 2026-11-09): se pide al comprar o al querer conservar el perfil en otro
  celular. Las funciones de calma nunca la piden. Los datos emocionales no se sincronizan.
- Los permisos **nunca** se piden al inicio: se piden en el momento en que se usan, con una pantalla previa que
  explica para qué (ver la tabla de permisos en [06-guardrails.md](06-guardrails.md#permisos)).

## Tono de los textos

- Cálido, de tú, frases cortas. Panbu habla como un compañero, no como un médico ni como un entrenador.
- Sí: "¡Lo lograste!", "Hoy fue un día difícil, y está bien", "¿Te tomas un minuto para respirar?".
- No: "Fallaste tu meta", "Deberías…", "Usaste demasiado el celular".
- Máximo un emoji por pantalla.

## Fuera de alcance (por ahora)

Chat con inteligencia artificial, diagnósticos o tests psicológicos, red social o compartir, versión para
iPhone (decidido: solo Android), sincronizar los datos emocionales en la nube (decisión pendiente #13).
