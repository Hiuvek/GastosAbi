# GastosAbiyKevs

## Buenos días profesor Valdepeña

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

## Como ejecutar

1. Abre el proyecto en Android Studio.
2. Espera a que Gradle termine de sincronizar las dependencias.
3. Conecta un dispositivo Android o inicia un emulador.
4. Selecciona el modulo `app` y ejecuta la aplicacion con el boton Run.

La base de datos `gastos.db` se crea automaticamente la primera vez que se abre la aplicacion.

## Limitaciones conocidas

- La aplicacion guarda los datos solo en el dispositivo local; no sincroniza informacion entre dispositivos.
- Los iconos de las categorias se guardan como una preferencia visual local.
- No incluye fecha, ingresos ni graficas; el alcance se centra en el registro y resumen de gastos.

<img width="526" height="517" alt="image" src="https://github.com/user-attachments/assets/e870df5d-fb07-4a41-896c-ba94d413a2b0" />
