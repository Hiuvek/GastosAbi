# Wireframes y navegacion

Especificacion de las tres pantallas, basada en los disenos compartidos. No acredita por si misma la presentacion inicial al profesor.

## Inicio / Resumen

```text
Hola, bienvenido
[ Gasto total registrado: $240.50 ]
[ Gastos: 3 ] [ Categorias utilizadas: 2 ]
Resumen por categoria
Alimentacion       $205.50
Entretenimiento      $0.00
Transporte          $35.00
Tus ultimos gastos
Categoria / concepto / monto / menu editar-eliminar
[Inicio] [Gastos] [+] [Categorias]
```

Sin categorias: indicar que se creen en Categorias con +. Sin gastos: total 0.00 y mensaje para registrar el primero. Las categorias sin movimientos permanecen en el resumen.

## Categorias

```text
[Volver] Categorias [+]
[menu] [icono] Nombre / cantidad de gastos / total [ver]
[Inicio] [Gastos] [+] [Categorias]
```

El + abre Nueva categoria: nombre, selector de icono, Guardar y Cancelar. El menu izquierdo permite editar o eliminar; se explica si existen gastos y se bloquea la eliminacion. Tocar una categoria abre sus gastos. Guardar una categoria nueva conduce a Agregar gasto. Sin categorias: mensaje para pulsar +.

## Gastos

```text
[Volver] Lista de gastos
[Buscar concepto o categoria]
Cantidad de gastos / total
Categoria / concepto opcional / monto / menu
[Inicio] [Gastos] [+] [Categorias]

[Volver] Agregar gasto / Editar gasto
Monto [ $ 0.00 ]
Categoria [seleccionar existente]
Concepto (opcional) [En que gastaste]
[Guardar gasto / Guardar cambios]
```

El menu derecho permite editar o eliminar con confirmacion. Sin categorias, el formulario ofrece Crear categoria y deshabilita Guardar. Sin gastos, se invita a registrar el primero. Busqueda sin coincidencias: mensaje especifico. Los errores de validacion aparecen junto al formulario.

Los bocetos originales disponibles se conservan en `wireframes/`.
