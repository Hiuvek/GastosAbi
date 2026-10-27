# Matriz de pruebas

## Automatizadas

| Caso | Cobertura |
| --- | --- |
| Alimentacion: 85.50 + 120.00; Transporte: 35.00 sin descripcion; Entretenimiento sin gastos | ModeloGastosTest: resumen 205.50, 35.00, 0.00 y total 240.50 |
| Editar Transporte a 40.00 | Total 245.50 |
| Rechazar categoria inexistente, cero, negativos y valores no finitos | Insercion y actualizacion SQLite |
| Bloquear eliminacion de categoria con gastos | Restriccion FK y registros intactos |
| Eliminar gasto y despues su categoria | Ambas eliminaciones y consultas individuales |
| Crear, renombrar, validar nombre vacio y duplicado | CRUD de categorias |
| Cambiar categoria, descripcion y monto del gasto | CRUD de gastos |
| Cerrar y reabrir helper | Mismos datos persistidos en SQLite |
| Migrar version 1 a 2 | Datos, IDs, descripcion NULL, importes y secuencia conservados |
| Formulario sin concepto, con centavos | PantallasTest |
| Sin categorias: guardar deshabilitado y accion Crear categoria | PantallasTest |
| Inicio muestra categorias con cero y total general | PantallasTest |
| Punto/coma decimal, redondeo, formato de dos decimales | MontoTest |

Los tests ExampleUnitTest y ExampleInstrumentedTest son los de plantilla; no cuentan como validacion funcional.

## Verificacion manual de entrega

1. Crear Alimentacion, Transporte y Entretenimiento desde Categorias.
2. Registrar los tres gastos de la tabla del PDF; dejar vacio el concepto de Transporte.
3. Capturar Inicio con los totales y Categorias con Entretenimiento en 0.00.
4. En Gastos, editar Transporte de 35.00 a 40.00; comprobar el total 245.50.
5. Intentar eliminar Alimentacion y confirmar el aviso. Eliminar el gasto de Transporte y luego su categoria.
6. Cerrar completamente la app y reabrir; comprobar que se conservan los datos.
7. Capturar una operacion CRUD y entregar junto con los wireframes originales.

Las evidencias automaticas o manuales deben identificarse como tales; no confundir una prueba de helper con el cierre completo de la aplicacion.

## Resultado verificado (26 de octubre de 2026)

- Compilacion de APK y APK de pruebas: correcta.
- Pruebas unitarias: 3 aprobadas (2 nuevas de montos y 1 de plantilla).
- Pruebas instrumentadas: 7 aprobadas (2 de SQLite/migracion, 4 de Compose y 1 de plantilla).
- Emulador Android temporal: alta de las tres categorias y de los tres gastos desde la interfaz; categoria nueva regresa a Agregar gasto; concepto vacio aceptado; resumen 240.50; edicion de Transporte a 40.00; total 245.50 conservado tras `am force-stop` y reapertura de MainActivity.
- Las pruebas se ejecutaron en una sesion temporal de emulador, sin usar el telefono conectado.

## Capturas de la aplicacion en ejecucion

- [Inicio con total 240.50](evidencias/inicio.png)
- [Categorias con Entretenimiento en 0.00](evidencias/categorias.png)
- [Lista de gastos](evidencias/gastos.png)
- [Edicion de gasto sin concepto](evidencias/editar-gasto.png)
- [Inicio tras editar y reabrir, total 245.50](evidencias/inicio-tras-reabrir.png)
