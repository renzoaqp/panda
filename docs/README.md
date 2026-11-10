# Documentación de Panbu

Todo lo que se decida sobre Panbu se escribe aquí.

| Documento | Contenido |
|---|---|
| [01-plan-de-trabajo.md](01-plan-de-trabajo.md) | Fases, tareas, decisiones pendientes y riesgos. **Empieza aquí.** |
| [02-producto.md](02-producto.md) | Propósito, principios, pantallas y comportamiento de cada función |
| [03-arquitectura.md](03-arquitectura.md) | Tecnología, capas, estructura de carpetas, convenciones y comandos |
| [04-diseno-visual.md](04-diseno-visual.md) | Colores, tipografía, formas, componentes e ilustraciones |
| [05-modelo-de-datos.md](05-modelo-de-datos.md) | Qué se guarda y cómo se calcula cada métrica |
| [06-guardrails.md](06-guardrails.md) | Reglas obligatorias: salud, datos reales, privacidad, técnica y permisos |
| [07-bluetooth.md](07-bluetooth.md) | Diseño preliminar de la conexión con el peluche (fase final) |
| [08-manual-vscode.md](08-manual-vscode.md) | **Manual paso a paso:** preparar Windows + VS Code, clonar, compilar e instalar en el celular |
| [decisiones/](decisiones/) | Registro de decisiones de arquitectura (ADR) |

Material original del cliente (no editar): [`../00info/`](../00info/) — mockup y guía en PDF.

## Cómo mantener esta documentación

- Si cambias cómo funciona algo, actualiza el documento en el mismo commit.
- Si tomas una decisión difícil de revertir (una librería, un permiso, dónde se guardan los datos, cómo se
  cobra), escribe un ADR en `decisiones/` copiando [la plantilla](decisiones/0000-plantilla.md).
- Las preguntas abiertas van en "Decisiones pendientes" del [plan](01-plan-de-trabajo.md#decisiones-pendientes).
