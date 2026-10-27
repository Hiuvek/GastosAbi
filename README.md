# GastosAbi

Aplicación Android para registrar gastos personales. Permite crear categorías, agregar gastos, editarlos, eliminarlos y ver un resumen de lo gastado por categoría.

## Base de datos

La aplicación guarda la información en una base de datos local llamada `gastos.db`.

Tiene dos tablas:

| **Tabla** | **Guarda** |
| --- | --- |
| `categorias` | El identificador y el nombre de cada categoría. |
| `gastos` | El identificador, la categoría, el concepto opcional y el monto de cada gasto. |

Cada gasto pertenece a una categoría. No se puede eliminar una categoría si todavía tiene gastos registrados.

## Decisiones principales

- Los montos aceptan centavos, por ejemplo `85.50`, y se muestran siempre con dos decimales.
- El concepto de un gasto es opcional.
- Las categorías sin gastos se muestran con `$ 0.00` en el resumen.
- La aplicación conserva los datos al cerrarse y volver a abrirse.
- Los iconos de las categorías se guardan como una preferencia visual y no cambian las tablas de gastos.
