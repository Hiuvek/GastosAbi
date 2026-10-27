# Imagenes por pantalla

- inicio/drawable: icono de inicio.
- agregar_gasto/drawable: monto, concepto y selector.
- lista_gastos/drawable: icono de la lista de gastos.
- categorias/drawable: icono de categorias, reutilizado en los gastos.
- compartidos/drawable: agregar y volver.

Reemplaza los PNG conservando sus nombres. Se recomiendan imagenes cuadradas con fondo transparente.

Las carpetas estan registradas en app/build.gradle.kts como recursos del source set main. Android combina los recursos: cada nombre debe ser unico, aunque este en otra carpeta. Los recursos se siguen usando como R.drawable.ic_nombre.

En Android Studio, usa la vista Project para ver las carpetas fisicas; la vista Android puede agrupar los drawables.
