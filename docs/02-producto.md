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
Primera vez ─────► Bienvenida (5 pasos) ──► Inicio
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

Implementada en el **APK 0.3** (salvo el tiempo de pantalla, que llega en la Fase 2). Las fórmulas exactas están en
[05-modelo-de-datos.md](05-modelo-de-datos.md#reglas-de-cálculo) y cada una tiene un test.

| Elemento | Comportamiento |
|---|---|
| Semana / Mes / Total | Selector en forma de píldora. **Semana** = de lunes a domingo de la semana actual. **Mes** = mes calendario actual. **Total** = desde el primer registro (sesión de calma o estado de ánimo) hasta hoy. Al abrir la pantalla se ve Semana; el periodo elegido se mantiene si se gira el celular |
| Fechas del periodo | Debajo del selector: "9 – 15 nov", "Noviembre de 2026" o "Desde el 9 de noviembre de 2026" |
| Tiempo de calma | Suma de las sesiones de calma del periodo (cada sesión cuenta el día en que empezó; las de menos de 30 s no cuentan) |
| Comparación | "↑ 32% que la semana anterior" / "↓ 10% que el mes anterior" / "Igual que…". Se compara con **el mismo tramo** del periodo anterior: un miércoles, de lunes a miércoles contra de lunes a miércoles de la semana pasada. En verde si sube y en gris neutro si baja, **nunca en rojo**. No se muestra en Total, ni si el periodo actual o el anterior están en 0 |
| Gráfico de barras | Semana: una barra por día (Lun…Dom). Mes: una barra por semana de lunes a domingo, recortada al mes ("1", "2–8", …, "30"). Total: una barra por mes ("sep", "oct", "nov"), como máximo los últimos 12 meses (la suma sí incluye todo). La barra de hoy va en morado más intenso. Eje con pasos redondos (5 min, 15 min, 1 h…) |
| ¿Cómo te sientes? | Dona con el % de cada estado de ánimo registrado en el periodo; en el centro, el más frecuente con su %. Al lado, la leyenda ordenada de mayor a menor. Debajo: "20 registros en este periodo". Los % suman siempre 100 |
| Tiempo de pantalla (noche) | **Fase 2.** Promedio diario de uso del celular dentro del horario de descanso. Requiere el permiso "Acceso a datos de uso"; sin permiso, la tarjeta explica para qué sirve y cómo activarlo. Hoy la tarjeta dice "Próximamente" y no muestra ningún número |

**Estados vacíos (nunca gráficos en cero):**

| Situación | Qué se ve |
|---|---|
| Sin sesiones ni estados de ánimo en el periodo | Tarjeta con el panda: "Cuando registres tus momentos de calma y cómo te sientes, aquí verás tu avance." + botón **Respirar con Panbu** |
| Hay estados de ánimo pero no sesiones | Tarjeta de tiempo de calma con "0 min" y "Aún no hay momentos de calma esta semana. ¿Respiramos un minuto?" (sin gráfico) |
| Hay sesiones pero no estados de ánimo | Tarjeta de ánimo con "Aún no registraste cómo te sientes en este periodo. Puedes hacerlo en Inicio." |

**Accesibilidad:** el selector se lee como pestañas; el gráfico de barras se lee como un resumen ("Tiempo de calma:
Lun, 25 min; Mar, 40 min; …"); la dona es decorativa porque la leyenda dice lo mismo en texto.

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

**Metas iniciales (APK 0.2):** la app trae dos metas diarias, con progreso automático a partir de las sesiones
de calma. Se crean la primera vez que se abre la app, si todavía no hay metas.

| Meta | Objetivo | Qué cuenta |
|---|---|---|
| Momentos de calma con Panbu | 3 por día | Cualquier sesión de calma de al menos 30 s (respiración, momento con Panbu…) |
| Respiración y mindfulness | 2 por día | Solo las sesiones de respiración y mindfulness de al menos 30 s |

"Menos celular en la noche" y "Dormir mejor" se agregan cuando existan sus datos (tiempo de pantalla y registro de
sueño). Así nunca se muestra una meta con números inventados.

**Un momento con Panbu** (Inicio → Tu Panbu hoy): un temporizador para registrar el tiempo de calma con el peluche
(abrazarlo, descansar con él). Se toca "Comenzar", Panbu cuenta el tiempo, y "Terminar" lo guarda como "Tiempo
contigo". En la Fase 5, el peluche lo registrará solo.

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

No aparece en el mockup; se diseñó con los mismos componentes. **APK 0.3:** perfil, horario de descanso,
recordatorio, ayuda y "Borrar todos mis datos". El resto aparece en una tarjeta "Próximamente".

| Opción | Detalle | Estado |
|---|---|---|
| Tu nombre | Diálogo "¿Cómo te llamas?" (máximo 30 caracteres, puede quedar vacío). Se usa solo para el saludo de Inicio | ✅ APK 0.3 |
| Horario de descanso | Dos relojes seguidos: "¿A qué hora empiezas a descansar?" y "¿A qué hora te levantas?" (por defecto 22:00–06:00). Lo usan el recordatorio, el tiempo de pantalla (Fase 2) y "Acompáñame a descansar". Las horas siguen el formato 12/24 h del celular | ✅ APK 0.3 |
| Recordatorio para dejar el celular | Interruptor, **apagado por defecto**. Al activarlo en Android 13 o más, primero se explica para qué es el permiso y después se pide. Si la persona no lo da, el interruptor queda apagado y se explica con calma cómo activarlo en los ajustes del celular | ✅ APK 0.3 |
| Avisarme antes | 15 min, 30 min o 1 h antes del inicio del descanso (por defecto 30). Debajo: "Te avisaremos a las 21:30." | ✅ APK 0.3 |
| Ayuda | Tarjeta "¿Necesitas hablar con alguien?" con la Línea 113 y un botón que abre el marcador del teléfono | ✅ APK 0.3 |
| Privacidad y mis datos | Qué se guarda y dónde. **Borrar todos mis datos**, con confirmación: borra la base de datos, las preferencias y el recordatorio, y vuelve a la bienvenida. Exportar mis datos y la lista de permisos activos llegan después | ✅ Borrar · Exportar pendiente |
| Mi cuenta | Correo, iniciar o cerrar sesión, borrar la cuenta ([ADR-0004](decisiones/0004-cuentas-de-usuario.md)). Sin cuenta: "Crear cuenta / Iniciar sesión" | APK 0.4 |
| Sonidos de relajación | Sonido preferido | APK 0.4 |
| Tema | Claro, oscuro o el del sistema | Pendiente |
| Política de privacidad y términos | Textos del cliente | Pendiente (cliente) |
| Versión | "Versión 0.3.0" al final de la pantalla | ✅ APK 0.3 |

### Recordatorio para dejar el celular

- **Cuándo:** una vez al día, a la hora `inicio del descanso − minutos de anticipación` (con un margen de hasta 10
  minutos, ver [ADR-0005](decisiones/0005-recordatorios-con-alarmmanager.md)). Si el descanso empieza 00:30 y el
  aviso es 1 h antes, llega a las 23:30 del día anterior.
- **Texto:** "Se acerca tu hora de descanso — ¿Dejamos el celular un rato y respiramos con Panbu? Tu descanso
  empieza a las 10:00 p. m.". Al tocarlo se abre la app. Nunca menciona la tienda.
- Sigue funcionando después de reiniciar el celular, actualizar la app o cambiar la hora o la zona horaria.
- Hoy es la única notificación de la app (el máximo es 2 al día).

## 7. Bienvenida (onboarding), cuenta y permisos

**APK 0.3:** la primera vez que se abre la app (y después de "Borrar todos mis datos") aparece la bienvenida, en
pantalla completa y sin barra inferior. Tiene 5 pasos, con puntos de avance arriba y los botones "Siguiente" y
"Atrás" abajo (el botón "atrás" del celular también vuelve al paso anterior):

| Paso | Contenido |
|---|---|
| 1. Hola, soy Panbu | El panda con el corazón y "Tu calma, siempre contigo. Te acompaño a crear momentos de tranquilidad en tu día." |
| 2. Así te acompaño | Respiramos juntos · Registras cómo te sientes · Metas pequeñas, sin presión · Un aviso para dejar el celular |
| 3. Tu privacidad | Lo que registras se queda en este celular · Sin publicidad ni seguimiento · Puedes borrarlo todo desde Ajustes |
| 4. ¿Cómo te llamas? | Campo opcional ("Lo usamos solo para saludarte. Puedes dejarlo vacío.") |
| 5. Antes de empezar | El aviso "Panbu te acompaña a practicar hábitos de bienestar, pero no sustituye la atención de un profesional de la salud." + la tarjeta de la línea de ayuda + **Entendido, empecemos** |

Al terminar se guarda `onboardingDone = true` (y el nombre) y la app pasa sola a Inicio.

- **Cuenta** ([ADR-0004](decisiones/0004-cuentas-de-usuario.md), **APK 0.4**): entre los pasos 3 y 4 se sumará
  **Crear cuenta / Iniciar sesión / Continuar sin cuenta**. Al crearla se explica en una frase qué se guarda en la
  nube (correo, nombre y foto opcional) y que los datos emocionales se quedan en el celular.
- **La cuenta es opcional** (decidido el 2026-11-09): se pide al comprar o al querer conservar el perfil en otro
  celular. Las funciones de calma nunca la piden. Los datos emocionales no se sincronizan.
- Los permisos **nunca** se piden en la bienvenida: se piden en el momento en que se usan, con una explicación
  previa (ver la tabla de permisos en [06-guardrails.md](06-guardrails.md#permisos)).

## Ayuda humana: "¿Necesitas hablar con alguien?"

Tarjeta rosada que aparece en **la pestaña Panbu** (al final, junto al aviso), en **Ajustes › Ayuda** y en **el
último paso de la bienvenida** (guardrail 2). Texto: "Si estás pasando por un momento muy difícil, hablar con
alguien puede ayudar. En Perú puedes llamar gratis al 113 (Minsa) y elegir la opción de salud mental. Atienden
las 24 horas." El botón **Llamar al 113** abre el marcador con el número escrito (no llama solo y no necesita
permisos).

**Pendiente del cliente:** confirmar la ruta del menú de la Línea 113. Fuentes oficiales del Minsa dicen "opción 5";
otras, "opción 3 y luego 5". Por eso el texto dice "elegir la opción de salud mental", que es correcto en los dos
casos. El número vive en `HelpLine.PHONE` (`ui/components/HelpLineCard.kt`) y en los textos `help_*`.

## Tono de los textos

- Cálido, de tú, frases cortas. Panbu habla como un compañero, no como un médico ni como un entrenador.
- Sí: "¡Lo lograste!", "Hoy fue un día difícil, y está bien", "¿Te tomas un minuto para respirar?".
- No: "Fallaste tu meta", "Deberías…", "Usaste demasiado el celular".
- Máximo un emoji por pantalla.

## Fuera de alcance (por ahora)

Chat con inteligencia artificial, diagnósticos o tests psicológicos, red social o compartir, versión para
iPhone (decidido: solo Android), sincronizar los datos emocionales en la nube (decisión pendiente #13).
