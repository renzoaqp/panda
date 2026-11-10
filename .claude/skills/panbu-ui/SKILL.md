---
name: panbu-ui
description: Construir o modificar la interfaz de Panbu (Jetpack Compose) respetando su identidad visual (lavanda/crema, tarjetas redondeadas, panda), sus textos cálidos en español, los estados vacíos y la accesibilidad. Úsala al crear o cambiar cualquier pantalla, Composable, componente, color, ícono, ilustración o texto visible de la app.
---

# Interfaz de Panbu

## Antes de empezar
1. Mira el mockup: `00info/WhatsApp Image 2026-11-09 at 22.18.13.jpeg` (Inicio, Estadísticas, Metas, Tienda).
2. Lee el comportamiento de la pantalla en `docs/02-producto.md` y los tokens en `docs/04-diseno-visual.md`.
3. Las pantallas Panbu (espacio de calma) y Ajustes **no están en el mockup**: diséñalas con los mismos
   componentes y el mismo estilo, siguiendo `docs/02-producto.md` §4 y §6.

## Reglas
1. **Colores** solo desde `MaterialTheme.colorScheme` o `PanbuTheme.extraColors`. Nunca `Color(0xFF…)` en una
   pantalla. Si falta un color, agrégalo a `ui/theme/Color.kt` + `Theme.kt` y documéntalo en `docs/04`.
2. **Medidas** con `PanbuSpacing`; **formas** con `MaterialTheme.shapes` (las tarjetas usan `large` = 20 dp).
3. **Reutiliza:** `PanbuScreen` (cabecera con logo + scroll + márgenes), `PanbuCard`, `PanbuProgressBar`,
   `ScreenTitle`, `SectionTitle`. Si un patrón se repite 2 veces, conviértelo en un componente de
   `ui/components/` y agrégalo a la tabla de componentes de `docs/04`.
4. **Textos** en `res/values/strings.xml`, en español, de tú. Nunca hardcodeados.
5. **Estructura:** `XScreen(…, viewModel = hiltViewModel())` lee el estado y llama a
   `XContent(uiState, onEvent…)`, que no tiene estado y tiene `@Preview` con `PanbuTheme` y fondo `0xFFF8F5EF`.
6. **Estado vacío obligatorio:** si no hay datos, un mensaje amable + una acción sugerida. Nunca ceros
   engañosos ni porcentajes inventados.
7. **Progreso:** barra `extraColors.progress`; meta cumplida `extraColors.success`; límite superado
   `extraColors.overLimit` (rosa). **Nunca rojo de error.**
8. **Accesibilidad:** áreas táctiles de al menos 48 dp (`PanbuSpacing.minTouchTarget`); `contentDescription` en
   íconos con acción (`null` si son decorativos y tienen texto al lado); títulos con `semantics { heading() }`;
   texto secundario con `onSurfaceVariant` (ya cumple el contraste AA); que se vea bien con la fuente al 130 %.
9. **Movimiento** suave (300–400 ms). La respiración usa los segundos de `BreathingPattern`.

## Tono de los textos
- Cálido, cercano, frases cortas. Panbu es un compañero, no un médico ni un entrenador.
- Sí: "¡Lo lograste!", "Hoy fue un día difícil, y está bien", "¿Te tomas un minuto para respirar?".
- No: "Fallaste", "Deberías…", "Usaste demasiado el celular", ni términos clínicos ("terapia", "síntomas").
- Máximo un emoji por pantalla.

## Checklist antes de terminar
- [ ] Se parece al mockup (o a lo descrito en `docs/02` si no hay mockup)
- [ ] El `@Preview` renderiza con datos de ejemplo
- [ ] Todos los textos están en `strings.xml`; no hay colores ni medidas sueltos
- [ ] Tiene estado vacío
- [ ] 48 dp, `contentDescription`, títulos como heading
- [ ] Se marcó la tarea en `docs/01-plan-de-trabajo.md`
