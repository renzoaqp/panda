# Diseño visual

Referencias: mockup ([`00info/`](../00info/)) y sección 9 de la guía. En código: `app/src/main/java/com/panbu/app/ui/theme/`.

**Estilo:** botones redondeados, letras fáciles de leer, dibujos de pandas, corazones pequeños, fondos claros,
barras de progreso e íconos sencillos.

## Colores

Los colores de la guía son la base. Los "del mockup" se midieron sobre la imagen. Las pantallas **no** usan
códigos de color directamente: usan `MaterialTheme.colorScheme` o `PanbuTheme.extraColors`.

| Token (`PanbuPalette`) | Hex | Origen | Uso | Rol en el tema |
|---|---|---|---|---|
| `Cream` | `#F8F5EF` | Guía | Fondo principal | `background` |
| `Lavender` | `#E8DDF5` | Guía | Fondos suaves, tarjeta de saludo, pestaña activa | `primaryContainer`, `surfaceVariant` |
| `Rose` | `#F7E6EF` | Guía | Detalles y mensajes amables | `secondaryContainer` |
| `Sage` | `#A9C8B0` | Guía | Fondos de logro | `tertiaryContainer` |
| `Purple` | `#7B65AE` | Mockup (botón "Agregar al carrito") | Botones principales, elementos activos | `primary` |
| `PurpleDeep` | `#6B52A3` | Mockup (ícono de pestaña activa) | Enlaces, íconos destacados | — |
| `PurpleSoft` | `#9E8BCD` | Mockup (barras) | Barras de progreso y gráficos | `extraColors.progress` |
| `SageStrong` | `#8DB795` | Mockup (barra 100 %) | Progreso completado | `extraColors.success` |
| `SageText` | `#4C8165` | Mockup ("↑ 32 %") | Comparaciones positivas | `extraColors.positiveText` |
| `RoseStrong` | `#E3A6BE` | Derivado de `Rose` | Barra de un límite superado (sin rojo) | `extraColors.overLimit` |
| `Ink` | `#24212E` | Mockup (títulos) | Texto principal | `onBackground`, `onSurface` |
| `InkSoft` | `#5F5870` | Mockup `#756E87`, oscurecido | Texto secundario (contraste AA sobre crema y lavanda) | `onSurfaceVariant` |
| `Card` | `#FFFDFA` | Mockup | Tarjetas | `surface` |
| `Outline` | `#ECE6F3` | Mockup | Bordes de tarjetas | `outline` |
| `Track` | `#EDE7F6` | Mockup | Fondo de las barras de progreso | `extraColors.progressTrack` |

### Estados de ánimo (`MoodPalette`)

| Estado | Hex | Nota |
|---|---|---|
| Tranquilo | `#B19DE2` | Del mockup |
| Ansioso | `#A9C4EB` | Ajustado (celeste) para distinguirlo de Tranquilo |
| Estresado | `#F7C9A5` | Del mockup |
| Triste | `#F2B8C6` | Ajustado (rosa) para distinguirlo de Tranquilo |
| Feliz | `#F5DC8C` | Nuevo (la guía lo pide; el mockup no lo muestra) |

En el mockup, Tranquilo, Ansioso y Triste tienen tonos casi iguales. Se ajustaron para que los 5 estados se
distingan en la dona. Hay que validarlo con el cliente.

### Tema oscuro (borrador)

La guía pide "Cambiar el tema". Paleta inicial (`Night*` en `PanbuPalette`): fondo `#1E1B26`, tarjeta `#2A2635`,
lavanda `#3B3450`, morado `#C9B8F0`, texto `#EDE8F5`, texto secundario `#B5ADC6`. Hoy la app usa siempre el tema
claro; el selector de tema se implementa en la Fase 1.

## Tipografía

- **Fuente:** Nunito (Google Fonts, licencia OFL), que es redondeada y amable como el mockup. Pendiente de
  agregar en `res/font` (hoy se usa la sans-serif del sistema).
- El logo "Panbu" es una imagen, no un texto con fuente.

| Estilo | Tamaño / interlineado | Peso | Uso |
|---|---|---|---|
| `displaySmall` | 32 / 40 | Bold | Números grandes ("2 h 15 min") |
| `headlineSmall` | 24 / 32 | Bold | Título de pantalla ("Estadísticas") |
| `titleLarge` | 22 / 28 | Bold | Saludo |
| `titleMedium` | 18 / 24 | SemiBold | Título de sección ("Tu Panbu hoy") |
| `titleSmall` | 16 / 22 | SemiBold | Título de tarjeta |
| `bodyLarge` | 16 / 24 | Regular | Texto principal |
| `bodyMedium` | 14 / 20 | Regular | Texto de tarjetas |
| `bodySmall` | 12 / 16 | Regular | Notas |
| `labelLarge` | 14 / 20 | SemiBold | Botones, chips |
| `labelSmall` | 11 / 16 | Medium | Barra inferior |

## Formas y espaciado

- Tarjetas: esquinas de **20 dp**, borde de 1 dp `Outline`, sin sombra.
- Botones principales y chips: forma de píldora (`CircleShape`).
- Barras de progreso: 8 dp de alto, extremos redondeados.
- Espaciado (`PanbuSpacing`): 4 · 8 · 12 · 16 · 24 · 32 dp. Margen lateral de pantalla: 16 dp.
- Área táctil mínima: 48 dp.

## Componentes

| Componente | Estado | Archivo |
|---|---|---|
| `PanbuScreen` (pantalla con cabecera y scroll) | Hecho | `ui/components/PanbuScreen.kt` |
| `PanbuHeader` (logo + nombre + acciones) | Hecho | `ui/components/PanbuHeader.kt` |
| `ScreenTitle`, `SectionTitle` | Hecho | `ui/components/ScreenTitle.kt` |
| `PanbuCard` | Hecho | `ui/components/PanbuCard.kt` |
| `PanbuProgressBar` | Hecho | `ui/components/PanbuProgressBar.kt` |
| `ChoiceChip` (opción en píldora: ánimo, ritmo, duración) | Hecho | `ui/components/ChoiceChip.kt` |
| `ActionRow` (fila con ícono, texto y ▶), `IconBubble` | Hecho | `ui/components/ActionRow.kt` |
| `GoalRow` (fila de meta: ícono + valor + barra + % + ✓) | Hecho | `ui/components/GoalRow.kt` |
| `KeepScreenOn` (pantalla encendida durante la calma) | Hecho | `ui/components/KeepScreenOn.kt` |
| `formatDuration` / `formatClock` ("2 h 15 min", "4:05") | Hecho | `ui/components/Formatters.kt` |
| Animación de respiración (círculo + panda al ritmo de cada fase) | Hecho | `feature/breathing/BreathingScreen.kt` |
| `PrimaryPillButton` (botón principal en píldora, a todo el ancho) | Hecho | `ui/components/PrimaryPillButton.kt` |
| `TrendText` (comparación: verde si mejora, gris si no; nunca rojo) | Hecho (Inicio y Estadísticas) | `ui/components/TrendText.kt` |
| `SegmentedSelector` (Semana · Mes · Total) | Hecho | `ui/components/SegmentedSelector.kt` |
| `CalmBarChart` (barras con eje redondo; resalta hoy; resumen para TalkBack) | Hecho | `ui/components/charts/CalmBarChart.kt` |
| `MinutesAxis` (elige el paso del eje; Kotlin puro con tests) | Hecho | `ui/components/charts/MinutesAxis.kt` |
| `DonutChart` (dona con separadores y contenido en el centro) | Hecho | `ui/components/charts/DonutChart.kt` |
| `HelpLineCard` ("¿Necesitas hablar con alguien?" + botón que abre el marcador) | Hecho | `ui/components/HelpLineCard.kt` |
| `SettingsRow`, `SettingsSwitchRow` (filas de Ajustes, 48 dp, toda la fila tocable) | Hecho | `ui/components/SettingsRows.kt` |
| `TimePickerDialog`, `formatTime` (reloj de Material 3; 12/24 h según el celular) | Hecho | `ui/components/TimeFormat.kt` |

### Gráficos

Se dibujan con `Canvas` de Compose, **sin librerías de gráficos** (una dependencia menos y control total del
estilo). Reglas:

- Barras en `extraColors.progress` (`PurpleSoft`); la barra de hoy en `primary` (`Purple`). Esquinas de arriba
  redondeadas (6 dp), ancho máximo 28 dp, líneas guía de 1 dp en `outline`.
- Etiquetas en `labelSmall` y `onSurfaceVariant`. Si no caben (muchas barras o letra grande), se muestra una de cada
  N, siempre incluida la última.
- Una barra con muy poco tiempo (por ejemplo 40 s en un eje de 1 h) se dibuja con al menos 3 dp para que no
  parezca vacía; una barra en 0 no se dibuja.
- Dona de 24 dp de grosor que empieza arriba y gira en el sentido del reloj, con una línea fina del color de la
  tarjeta entre porciones. Colores de `MoodPalette`. En el centro, el panda, el % y el estado más frecuente.
- Todo gráfico tiene su equivalente en texto (leyenda o `contentDescription`).

## Íconos

Íconos de línea redondeada, 24 dp, en `res/drawable/ic_*.xml`. Hay versiones provisionales de: inicio, luna (`ic_moon`), cuaderno (`ic_journal`),
estadísticas, metas, panda, ajustes, volver, agregar y bolsa. Se reemplazarán por los definitivos del diseñador
(o por Material Symbols Rounded).

## Ilustraciones necesarias

**No hay ilustrador: las ilustraciones las creamos nosotros** (decisión del 2026-11-09). Se hacen en vector
(SVG → VectorDrawable en `res/drawable/`), con un estilo propio y simple: formas redondas, colores de la paleta,
mejillas rosadas y corazón lavanda. Se toma como referencia el aspecto del peluche del mockup, sin copiar esas
imágenes (parecen generadas por IA). Antes de convertirlas, se revisan renderizadas como PNG.

**La tienda es la excepción:** ahí se muestran fotos reales del peluche, porque una ilustración no debe
representar un producto que se vende (guardrail 7, sin engaños).

| Ilustración | Dónde |
|---|---|
| Logo Panbu en vector (SVG) | Cabecera, ícono de la app, splash (hoy: cara provisional `ic_panbu_face`) |
| ✅ Panda sentado con corazón — `res/drawable/ill_panda_heart.xml` | Inicio, Metas, Respira con Panbu, Un momento con Panbu, espacio de calma |
| Panda saludando | Onboarding |
| Panda respirando (2 frames: inflado/desinflado) | Respira con Panbu |
| Panda dormido | Acompáñame a descansar, modo descanso |
| Panda dando un abrazo | Estado triste/ansioso |
| Panda celebrando | Meta cumplida |
| **Fotos reales** del peluche en Clásico, Rosado y Gris | Tienda |
| Íconos de accesorios (funda, tarjeta, calor) | Tienda |

## Accesibilidad

- Contraste AA: texto normal 4.5:1, texto grande e íconos 3:1. `InkSoft` se oscureció por esta razón.
- Todo ícono con acción lleva `contentDescription`; los decorativos van con `null`.
- Las barras de progreso informan su valor a TalkBack (`progressBarRangeInfo`).
- Los gráficos tienen un resumen en texto para lectores de pantalla.
- Probar con fuente al 130 % y con "Quitar animaciones" activado.

## Movimiento

Animaciones suaves de 300–400 ms. La respiración sigue los segundos del `BreathingPattern`. Si el sistema
tiene las animaciones desactivadas, la respiración se guía con texto y vibración.
