# ADR-0003: Tienda con pedido externo en la primera versión

- **Estado:** Propuesta (confirmar con el cliente, decisión pendiente #6)
- **Fecha:** 2026-11-09

## Contexto
La tienda vende un producto físico (el peluche y sus accesorios) con precios en soles. La política de pagos de
Google Play indica que los bienes físicos **no** se cobran con Google Play Billing, sino con un procesador de
pagos externo. Integrar un procesador (Culqi, Mercado Pago, Izipay, Niubiz…) de forma segura requiere un backend
para crear los cobros y validar los pagos, además de manejar inventario y envíos.

## Decisión
**Versión 1:** catálogo y carrito dentro de la app; el pedido se envía por **WhatsApp** (mensaje prellenado con
producto, color, accesorios, cantidad y total) o se abre la **tienda web** del cliente si existe. La app no
procesa pagos ni guarda datos de pago.

**Versión 2 (opcional):** pasarela de pago peruana integrada, con un backend propio y un ADR nuevo.

## Consecuencias
- La tienda puede salir en la Fase 3 sin backend ni costos de servidor.
- Menos riesgo legal y de seguridad: no hay datos de tarjetas.
- La experiencia de compra es menos directa, porque el pago se cierra fuera de la app.

## Alternativas consideradas
- **Pasarela integrada desde el inicio:** mejor experiencia, pero necesita backend, contrato con el procesador
  y más pruebas; retrasaría la publicación.
- **Google Play Billing:** no está permitido para bienes físicos.
