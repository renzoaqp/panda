# ADR-0004: Cuentas de usuario

- **Estado:** Aceptada en lo general (habrá cuentas; lo decidió el responsable del proyecto el 2026-11-09).
  Los detalles son una **propuesta** pendiente de confirmar: proveedor y formas de iniciar sesión (#11), cuenta
  obligatoria u opcional (#12) y sincronización de datos emocionales (#13).
- **Fecha:** 2026-11-09
- **Modifica:** ADR-0002 (datos locales primero) y los guardrails 11, 12 y 14.

## Contexto
El cliente confirmó que habrá cuentas de usuario (la guía ya menciona "Cerrar sesión" en Ajustes). Para qué
sirven:
- identificar a la persona en sus pedidos de la tienda (Fase 3);
- conservar el perfil al cambiar de celular;
- más adelante, vincular el peluche y, si se aprueba, respaldar los datos.

Al mismo tiempo, Panbu maneja datos emocionales sensibles que hoy se quedan en el celular (ADR-0002). Además,
Google Play exige que toda app que permite crear cuentas permita borrarlas desde la app y desde una página web.

## Decisión (propuesta en los detalles)
1. **Proveedor:** Firebase Authentication, con inicio de sesión con Google (Credential Manager) y con correo +
   contraseña. **No** se incluyen Firebase Analytics, Crashlytics, publicidad ni otros productos que recojan
   datos.
2. **Cuenta opcional:** la app se puede usar sin cuenta ("Continuar sin cuenta"). La cuenta se pide solo para
   lo que la necesita (comprar, cambiar de celular). "Necesito un momento de calma" y las actividades de calma
   nunca piden iniciar sesión.
3. **Lo único que sale del celular es la cuenta:** correo, nombre visible y, si la persona quiere, la foto de
   perfil. Más adelante, los pedidos se asocian a la cuenta.
4. **Los datos emocionales siguen en el celular** (estado de ánimo, diario, sesiones, sueño, uso del celular).
   Sincronizarlos o respaldarlos en la nube requiere un ADR nuevo y **cifrado de extremo a extremo** (el
   servidor no debe poder leerlos).
5. **Consentimiento:** al crear la cuenta se explica en una frase qué se guarda en la nube, con un enlace a la
   política de privacidad.
6. **Borrado de cuenta:** desde Ajustes y desde una página web. Borra la cuenta y todo lo asociado en el
   servidor. "Cerrar sesión" no borra los datos del celular; "Borrar todo" (ADR-0002) sí.

## Consecuencias
- Desde la Fase 1 hacen falta el permiso `INTERNET` y un proyecto de Firebase (`google-services.json`; no tiene
  claves de servidor, pero se revisa antes de subirlo al repo).
- La política de privacidad y la sección "Seguridad de los datos" de Play Console deben declarar el correo, el
  nombre y la foto.
- Más trabajo en la Fase 1: inicio de sesión, registro, recuperar contraseña, pantalla de cuenta, cerrar sesión
  y borrar la cuenta (en la app y en la web).
- Se agrega un tercero (Google/Firebase) que ve los datos de la cuenta; nunca los datos emocionales.

## Alternativas consideradas
- **Supabase:** código abierto y base de datos PostgreSQL; buena opción para no depender de Google, pero pide
  más configuración propia.
- **Backend propio:** control total, pero mucho más trabajo y más responsabilidad de seguridad.
- **Cuenta obligatoria:** simplifica el modelo, pero pone una barrera antes del primer momento de calma y obliga
  a dar datos que la app no necesita para funcionar.
